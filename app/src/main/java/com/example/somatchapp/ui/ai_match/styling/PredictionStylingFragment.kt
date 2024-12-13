package com.example.somatchapp.ui.ai_match.styling

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.somatchapp.R
import com.example.somatchapp.data.local.OutfitStyleRoomDatabase
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.repository.OutfitStyleRepository
import com.example.somatchapp.databinding.FragmentPredictionStylingBinding

class PredictionStylingFragment : Fragment() {
    private var _binding: FragmentPredictionStylingBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var stylingViewModel: StylingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPredictionStylingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NavController
        navController = findNavController()

        // Ambil repository dari database
        val repository = OutfitStyleRepository(OutfitStyleRoomDatabase.getDatabase(requireContext()).outfitStyleDao())

        // Gunakan factory untuk membuat ViewModel
        val factory = StylingViewModelFactory(requireActivity().application, repository)
        stylingViewModel = ViewModelProvider(this, factory).get(StylingViewModel::class.java)

        // Observe outfitStyles from ViewModel
        stylingViewModel.allOutfitStyles.observe(viewLifecycleOwner, Observer { outfitStyles ->
            // Update UI with the latest data
            updateUI(outfitStyles)

            // Enable nextButton only if both upperwear and bottomwear have valid images
            val hasUpperwearImage = outfitStyles.any { it.type == "upperwear" && it.image != null }
            val hasBottomwearImage = outfitStyles.any { it.type == "bottomwear" && it.image != null }
            binding.nextButton.isEnabled = hasUpperwearImage && hasBottomwearImage
        })

        setupClickListeners()

        binding.nextButton.setOnClickListener {
            navController.navigate(R.id.action_prediction_styling_fragment_to_predictionResultFragment)
        }
    }

    private fun setupClickListeners() {
        binding.ibBag.setOnClickListener {
            navigateToScanner("bag", "Tas")
        }
        binding.ibTopwear.setOnClickListener {
            navigateToScanner("upperwear", "Atasan")
        }
        binding.ibBottomwear.setOnClickListener {
            navigateToScanner("bottomwear", "Bawahan")
        }
        binding.ibAccessories.setOnClickListener {
            navigateToScanner("accessories", "Aksesoris")
        }
        binding.ibFootwear.setOnClickListener {
            navigateToScanner("footwear", "Alas Kaki")
        }
    }

    private fun navigateToScanner(itemType: String, itemTitle: String) {
        val action = PredictionStylingFragmentDirections.actionPredictionStylingFragmentToScannerFragment(itemType, itemTitle)
        navController.navigate(action)
    }

    private fun updateUI(outfitStyles: List<OutfitStyle>) {
        // You can now set the images or other views in your UI based on the outfitStyles data
        outfitStyles.forEach { outfitStyle ->
            val imageUri = outfitStyle.image

            // Log the imageUri and type for debugging purposes
            Log.d("OutfitStyle", "Type: ${outfitStyle.type}, Image URI: $imageUri")

            when (outfitStyle.type) {
                "bag" -> setImage(binding.ibBag, imageUri)
                "upperwear" -> setImage(binding.ibTopwear, imageUri)
                "bottomwear" -> setImage(binding.ibBottomwear, imageUri)
                "accessories" -> setImage(binding.ibAccessories, imageUri)
                "footwear" -> setImage(binding.ibFootwear, imageUri)
            }
        }
    }

    private fun setImage(imageView: ImageView, imageUri: String?) {
        if (imageUri != null) {
            try {
                val uri = Uri.parse(imageUri)
                Glide.with(imageView.context)
                    .load(uri)
                    .placeholder(R.drawable.ic_blank_gallery)
                    .into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
                imageView.setImageResource(R.drawable.ic_blank_gallery) // Fallback jika terjadi exception
            }
        } else {
            imageView.setImageResource(R.drawable.ic_add_style) // Default jika URI null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}