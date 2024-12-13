package com.example.somatchapp.ui.scanner

import android.Manifest
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.somatchapp.R
import com.example.somatchapp.data.local.entity.MyCatalog
import com.example.somatchapp.data.local.room.MyCatalogRoomDatabase
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.data.repository.MyCatalogRepository
import com.example.somatchapp.databinding.FragmentScannerBinding
import com.example.utils.ScannerUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CommonScannerFragment : Fragment(R.layout.fragment_scanner) {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private var imageCapture: ImageCapture? = null
    private var savedUri: Uri? = null
    private lateinit var selectedType: String

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                savedUri = it
                displaySelectedImage()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentScannerBinding.bind(view)

        navController = findNavController()

        // Check for camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.spinKit.visibility = View.GONE

        binding.cameraButton.setOnClickListener {
            takePhoto()
        }

        binding.galleryButton.setOnClickListener {
            // Check for gallery permission
            if (ContextCompat.checkSelfPermission(requireContext(), getGalleryPermission())
                == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                requestPermissionLauncher.launch(getGalleryPermission())
            }
        }

        binding.saveButton.setOnClickListener {
            binding.spinKit.visibility = View.VISIBLE
            compressAndUploadImage()
        }

        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.displayImage.visibility = View.GONE

        val options = listOf("upperwear", "bottomwear", "footwear", "bag", "hat")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // Layout untuk item dropdown
            options
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        binding.spinnerDropdown.adapter = adapter

        binding.spinnerDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedType = options[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getGalleryPermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(binding.imagePreview.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
                Log.d("CommonScannerFragment", "Camera started successfully")
            } catch (exc: Exception) {
                Log.e("CommonScannerFragment", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            requireContext().filesDir,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CommonScannerFragment", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    displayCapturedImage()
                }
            }
        )
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun compressAndUploadImage() {
        savedUri?.let { uri ->
            // Tampilkan SpinKit saat proses dimulai
            binding.spinKit.visibility = View.VISIBLE

            // Kompres gambar menggunakan ScannerUtils
            val compressedUri = ScannerUtils.compressImage(requireContext(), uri)

            if (compressedUri != null) {
                val database = MyCatalogRoomDatabase.getDatabase(requireContext())
                val sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE)
                val token = sharedPreferences.getString("token", null)

                val file = File(compressedUri.path!!)
                val apiService = ApiConfig().getApiService()
                val repository = MyCatalogRepository(apiService)

                // Membuat MultipartBody untuk unggah
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, requestBody)

                // Mengunggah menggunakan repository
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = repository.uploadImageToMyCatalog(
                            "Bearer $token",
                            imagePart,
                            selectedType
                        )
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        // Sembunyikan SpinKit setelah proses selesai
                        binding.spinKit.visibility = View.GONE
                    }
                }
            } else {
                // Sembunyikan SpinKit jika gagal kompres gambar
                binding.spinKit.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to compress image", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            // Sembunyikan SpinKit jika tidak ada gambar yang dipilih
            binding.spinKit.visibility = View.GONE
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun displayCapturedImage() {
        savedUri?.let {
            binding.displayImage.setImageURI(it)
            binding.displayImage.visibility = View.VISIBLE
        }
    }

    private fun displaySelectedImage() {
        savedUri?.let {
            binding.displayImage.setImageURI(it)
            binding.displayImage.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}