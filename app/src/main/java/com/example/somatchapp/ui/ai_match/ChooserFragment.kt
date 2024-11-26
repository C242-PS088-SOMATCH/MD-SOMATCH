package com.example.somatchapp.ui.ai_match

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.somatchapp.R
import com.example.somatchapp.data.local.entity.ChooserCardData
import com.example.somatchapp.databinding.FragmentRecommendationChooserBinding

class ChooserFragment : Fragment() {

    private var _binding: FragmentRecommendationChooserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecommendationChooserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NavController
        val navController = findNavController()

        val recommendations = listOf(
            ChooserCardData(
                imageResId = R.drawable.recommendation_outfit_illustration,
                title = "Rekomendasi pakaian",
                description = "Kami merekomendasikan item yang belum kamu miliki",
                navigateLink = R.id.action_deciderFragment_to_recommendationStylingFragment
            ),
            ChooserCardData(
                imageResId = R.drawable.prediction_percentage_illustration,
                title = "Prediksi kecocokan",
                description = "Kami akan memprediksi kecocokan pakaian kamu",
                navigateLink = R.id.action_deciderFragment_to_recommendationStylingFragment
            )
        )

        val adapter = ChooserAdapter(recommendations, navController)
        binding.rvRecommendation.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecommendation.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

