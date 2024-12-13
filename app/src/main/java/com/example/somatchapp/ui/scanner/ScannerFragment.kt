package com.example.somatchapp.ui.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.example.somatchapp.data.local.OutfitStyleRoomDatabase
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.local.room.MyCatalogRoomDatabase
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.data.repository.MyCatalogRepository
import com.example.somatchapp.data.repository.OutfitStyleRepository
import com.example.somatchapp.databinding.FragmentRecommendationScannerBinding
import com.example.utils.ScannerUtils
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ScannerFragment : Fragment(R.layout.fragment_recommendation_scanner) {

    private var _binding: FragmentRecommendationScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    private var imageCapture: ImageCapture? = null
    private var savedUri: Uri? = null // Update dynamically after capturing an image

    private lateinit var outfitStyleRepository: OutfitStyleRepository
    private lateinit var itemType: String
    private lateinit var itemTitle: String

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        _binding = FragmentRecommendationScannerBinding.bind(view)

        // Get arguments
        itemType = ScannerFragmentArgs.fromBundle(requireArguments()).itemType
        itemTitle = ScannerFragmentArgs.fromBundle(requireArguments()).itemTitle

        // Initialize repository
        val database = OutfitStyleRoomDatabase.getDatabase(requireContext())
        outfitStyleRepository = OutfitStyleRepository(database.outfitStyleDao())

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.cameraButton.setOnClickListener { takePhoto() }
        binding.galleryButton.setOnClickListener { openGallery() }
        binding.arrowBack.setOnClickListener { findNavController().navigateUp() }

        // Initialize buttonSave as hidden
        binding.buttonSave.visibility = View.GONE
        binding.spinKit.visibility = View.GONE

        // Set the buttonSave click listener
        binding.buttonSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                compressAndUploadImage()
                findNavController().navigateUp()
            }
        }

        binding.buttonMyCatalog.setOnClickListener {
            val action = ScannerFragmentDirections.actionScannerFragmentToMyCatalogFragment(itemType)
            navController.navigate(action)
        }

        binding.pageTitle.text = "Ambil Gambar ${itemTitle.replaceFirstChar { it.uppercase() }}"
        binding.displayImage.visibility = View.GONE // Hide ImageView initially
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    startCamera()
                } else if (ContextCompat.checkSelfPermission(requireContext(), getGalleryPermission())
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    openGallery() // Jika izin galeri diberikan, langsung buka galeri
                }
            } else {
                Toast.makeText(requireContext(), "Permission is required to proceed", Toast.LENGTH_SHORT).show()
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
                Log.d("ScannerFragment", "Camera started successfully")
            } catch (exc: Exception) {
                Log.e("ScannerFragment", "Use case binding failed", exc)
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
                    Log.e("ScannerFragment", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    savedUri = Uri.fromFile(photoFile)
                    displayImage()
                }
            }
        )
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                savedUri = it
                displayImage()
            }
        }

    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), getGalleryPermission())
            == PackageManager.PERMISSION_GRANTED
        ) {
            galleryLauncher.launch("image/*")
        } else {
            requestPermissionLauncher.launch(getGalleryPermission())
        }
    }

    private fun getGalleryPermission(): String {
        // Gunakan izin READ_MEDIA_IMAGES untuk Android 13+ atau READ_EXTERNAL_STORAGE untuk versi lama
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }


    private fun displayImage() {
        savedUri?.let {
            binding.displayImage.setImageURI(it) // Display the image
            binding.displayImage.visibility = View.VISIBLE
            binding.buttonSave.visibility = View.VISIBLE // Show save button
        }
    }

    private suspend fun compressAndUploadImage() {
        savedUri?.let { uri ->
            try {
                // Menampilkan loading spinner
                binding.spinKit.visibility = View.VISIBLE

                // Kompres gambar menggunakan fungsi dari ScannerUtils
                val compressedImageUri = ScannerUtils.compressImage(requireContext(), uri)

                if (compressedImageUri != null) {
                    val compressedImageFile = File(compressedImageUri.path!!)
                    val sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", null)

                    val apiService = ApiConfig().getApiService()
                    val repository = MyCatalogRepository(apiService)

                    // Buat MultipartBody.Part dari file hasil kompresi
                    val requestFile = compressedImageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imagePart = MultipartBody.Part.createFormData("image", compressedImageFile.name, requestFile)

                    // Siapkan bearer token untuk autentikasi (sesuaikan sumber token Anda)
                    val bearerToken = "Bearer $token"

                    // Panggil API untuk mengunggah gambar
                    val response = repository.uploadImageToMyCatalog(
                        bearerToken = bearerToken,
                        image = imagePart,
                        type = itemType
                    )

                    if (response.isSuccessful && response.body() != null) {
                        val imageUrl = response.body()?.imageUrl ?: ""
                        val catalogId = response.body()?.imageId ?: 0

                        // Simpan ke database
                        saveToDatabase(catalogId, imageUrl)
                        Toast.makeText(requireContext(), "Image successfully uploaded", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Upload failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to compress image", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ScannerFragment", "Failed to upload image", e)
            } finally {
                // Sembunyikan loading spinner
                binding.spinKit.visibility = View.GONE
            }
        } ?: run {
            Toast.makeText(requireContext(), "No image to upload", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveToDatabase(catalogId: Int, imageUrl: String) {
        val outfitStyle = OutfitStyle(
            type = itemType,
            image = imageUrl,
            myCatalogId = catalogId
        )
        try {
            outfitStyleRepository.insert(outfitStyle)
            binding.buttonSave.visibility = View.GONE
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save to database: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}