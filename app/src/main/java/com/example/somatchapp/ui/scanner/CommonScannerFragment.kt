package com.example.somatchapp.ui.scanner

import android.Manifest
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
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class CommonScannerFragment : Fragment(R.layout.fragment_scanner) {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private var imageCapture: ImageCapture? = null
    private var savedUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val permission = getGalleryPermission()
                if (permission == Manifest.permission.CAMERA) {
                    startCamera()
                } else {
                    openGallery() // Izin galeri diberikan, buka galeri
                }
            } else {
                val permissionType =
                    if (getGalleryPermission() == Manifest.permission.CAMERA) "Camera" else "Gallery"
                Toast.makeText(requireContext(), "$permissionType permission is required", Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                savedUri = it
                displaySelectedImage() // Menampilkan gambar dari galeri
            }
        }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
        private const val GALLERY_REQUEST_CODE = 1002
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

        binding.cameraButton.setOnClickListener {
            takePhoto()
        }

        binding.galleryButton.setOnClickListener {
            val galleryPermission = getGalleryPermission()
            if (ContextCompat.checkSelfPermission(requireContext(), galleryPermission)
                == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                // Minta izin akses galeri
                requestPermissionLauncher.launch(galleryPermission)
            }
        }

        binding.saveButton.setOnClickListener {
            saveImage()
        }

        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.displayImage.visibility = View.GONE

        // Data untuk dropdown
        val options = listOf("Atasan", "Bawahan", "Tas", "Aksesoris", "Alas Kaki")

        // Buat adapter untuk Spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, // Layout untuk item dropdown
            options
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // Pasangkan adapter ke Spinner
        binding.spinnerDropdown.adapter = adapter

        // Listener untuk item yang dipilih
        binding.spinnerDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
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
                    displayCapturedImage() // Menampilkan gambar dari kamera
                }
            }
        )
    }

    private fun openGallery() {
        val galleryPermission = getGalleryPermission()
        if (ContextCompat.checkSelfPermission(requireContext(), galleryPermission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Izin telah diberikan, buka galeri
            galleryLauncher.launch("image/*")
        } else {
            // Minta izin akses galeri
            requestPermissionLauncher.launch(galleryPermission)
        }
    }

    private fun saveImage() {
        savedUri?.let { uri ->
            val myCatalog = MyCatalog(
                id = 0,
                image = uri.toString(),
                isSelected = false,
                inStyle = false
            )

            val myCatalogRepository = MyCatalogRepository(
                MyCatalogRoomDatabase.getDatabase(requireContext()).myCatalogDao(),
                ApiConfig().getApiService()
            )

            viewLifecycleOwner.lifecycleScope.launch {
                myCatalogRepository.insert(myCatalog)
                Toast.makeText(requireContext(), "Image saved to catalog", Toast.LENGTH_SHORT).show()
            }
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