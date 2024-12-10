package com.example.somatchapp.ui.ai_match

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.example.somatchapp.data.repository.OutfitStyleRepository
import com.example.somatchapp.databinding.FragmentRecommendationScannerBinding
import kotlinx.coroutines.launch
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

    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
        private const val GALLERY_REQUEST_CODE = 1002
    }

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

        // Set the buttonSave click listener
        binding.buttonSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                saveImageToDatabase()
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
                val permission = getGalleryPermission()
                if (permission == Manifest.permission.CAMERA) {
                    startCamera()
                } else {
                    openGallery()
                }
            } else {
                val permissionType =
                    if (getGalleryPermission() == Manifest.permission.CAMERA) "Camera" else "Gallery"
                Toast.makeText(requireContext(), "$permissionType permission is required", Toast.LENGTH_SHORT).show()
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
        val galleryPermission = getGalleryPermission()
        if (ContextCompat.checkSelfPermission(requireContext(), galleryPermission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Izin diberikan, buka galeri
            galleryLauncher.launch("image/*")
        } else {
            // Minta izin
            requestPermissionLauncher.launch(galleryPermission)
        }
    }

    private fun getGalleryPermission(): String {
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

    private suspend fun saveImageToDatabase() {
        savedUri?.let { uri ->
            val outfitStyle = OutfitStyle(
                type = itemType,
                image = uri.toString()
            )
            outfitStyleRepository.insert(outfitStyle)
            Toast.makeText(requireContext(), "Image saved to database", Toast.LENGTH_SHORT).show()

            // Hide save button after saving
            binding.buttonSave.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}