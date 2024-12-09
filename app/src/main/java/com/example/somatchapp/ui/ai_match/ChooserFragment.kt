package com.example.somatchapp.ui.ai_match

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.somatchapp.R
import com.example.somatchapp.data.local.OutfitStyleRoomDatabase
import com.example.somatchapp.data.local.entity.ChooserCardData
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.repository.OutfitStyleRepository
import com.example.somatchapp.databinding.FragmentRecommendationChooserBinding
import com.example.somatchapp.ui.ai_match.styling.StylingViewModel
import com.example.somatchapp.ui.ai_match.styling.StylingViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChooserFragment : Fragment() {

    private var _binding: FragmentRecommendationChooserBinding? = null
    private val binding get() = _binding!!

    private lateinit var stylingViewModel: StylingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationChooserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NavController
        val navController = findNavController()

        // Initialize ViewModel
        val repository = OutfitStyleRepository(OutfitStyleRoomDatabase.getDatabase(requireContext()).outfitStyleDao())
        val factory = StylingViewModelFactory(requireActivity().application, repository)
        stylingViewModel = ViewModelProvider(this, factory).get(StylingViewModel::class.java)

        // Reset data in Room
        resetOutfitStyles()

        // Set up RecyclerView
        val recommendations = listOf(
            ChooserCardData(
                imageResId = R.drawable.image_recommendation_outfit_illustration,
                title = "Rekomendasi pakaian",
                description = "Kami merekomendasikan item yang belum kamu miliki",
                navigateLink = R.id.action_deciderFragment_to_recommendationStylingFragment
            ),
            ChooserCardData(
                imageResId = R.drawable.image_prediction_percentage_illustration,
                title = "Prediksi kecocokan",
                description = "Kami akan memprediksi kecocokan pakaian kamu",
                navigateLink = R.id.action_chooserFragment_to_predictionStylingFragment
            )
        )

        val adapter = ChooserAdapter(recommendations, navController)
        binding.rvRecommendation.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecommendation.adapter = adapter
    }

    private fun resetOutfitStyles() {
        val defaultOutfitStyles = listOf(
            OutfitStyle(1, "bag", null, null),
            OutfitStyle(2, "top wear", null, null),
            OutfitStyle(3, "accessories", null, null),
            OutfitStyle(4, "bottom wear", null, null),
            OutfitStyle(5, "footwear", null, null)
        )

        // Launch a coroutine to reset data
        stylingViewModel.viewModelScope.launch(Dispatchers.IO) {
            stylingViewModel.deleteAll() // Hapus semua data lama
            stylingViewModel.insertAll(defaultOutfitStyles) // Tambahkan data default
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

