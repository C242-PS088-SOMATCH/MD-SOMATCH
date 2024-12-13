package com.example.somatchapp.ui.ai_match.prediction.result

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.somatchapp.R
import com.example.somatchapp.data.local.OutfitStyleRoomDatabase
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.data.repository.ModelRepository
import com.example.somatchapp.data.repository.OutfitStyleRepository
import com.example.somatchapp.databinding.FragmentPredictionResultBinding

class PredictionResultFragment : Fragment() {

    private lateinit var binding: FragmentPredictionResultBinding
    private lateinit var predictionViewModel: PredictionViewModel
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPredictionResultBinding.inflate(inflater, container, false)

        // Initialize NavController
        navController = findNavController()

        // Initialize the ViewModel
        val modelRepository = ModelRepository(ApiConfig().getApiService())
        val outfitStyleRepository = OutfitStyleRepository(OutfitStyleRoomDatabase.getDatabase(requireContext()).outfitStyleDao())

        val viewModelFactory = PredictionViewModelFactory(modelRepository, outfitStyleRepository)
        predictionViewModel = ViewModelProvider(this, viewModelFactory).get(PredictionViewModel::class.java)

        // Observe the prediction result
        predictionViewModel.predictionResult.observe(viewLifecycleOwner) { result ->
            binding.tvPredictionResult.text = result
        }

        // Observe the loading status
        predictionViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.recyclerViewImages.visibility = View.GONE
                binding.predictionResultLayout.visibility = View.GONE
                binding.loadingOrnament.visibility = View.VISIBLE
                binding.ornamentBg.visibility = View.VISIBLE
            } else {
                binding.loadingOrnament.visibility = View.GONE
                binding.ornamentBg.visibility = View.GONE
                binding.recyclerViewImages.visibility = View.VISIBLE
                binding.predictionResultLayout.visibility = View.VISIBLE
            }
        }

        // Initialize the RecyclerView and Adapter
        val outfitStyles = mutableListOf<OutfitStyle>() // Empty list initially
        val outfitStyleAdapter = OutfitStyleAdapter(requireContext(), outfitStyles) { outfitStyle ->
        }

        // Set LayoutManager for RecyclerView
        binding.recyclerViewImages.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerViewImages.adapter = outfitStyleAdapter

        // Observe the outfit styles (update the list)
        predictionViewModel.allOutfitStyles.observe(viewLifecycleOwner) { outfitList ->
            val filteredOutfitList = outfitList.filter { !it.image.isNullOrEmpty() }
            outfitStyles.clear()
            outfitStyles.addAll(filteredOutfitList)
            outfitStyleAdapter.notifyDataSetChanged()

            // Get upperwear and bottomwear images
            val upperwear = filteredOutfitList.firstOrNull { it.type == "upperwear" }
            val bottomwear = filteredOutfitList.firstOrNull { it.type == "bottomwear" }

            // Get token from SharedPreferences
            val sharedPreferences = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("token", null)

            if (token != null && upperwear != null && bottomwear != null) {
                val bearerToken = "Bearer $token"
                val upper = upperwear.image
                val bottom = bottomwear.image
                if (upper != null) {
                    if (bottom != null) {
                        predictionViewModel.predictOutfit(bearerToken, upper, bottom)
                    }
                }
            } else {
                binding.tvPredictionResult.text = "Token or outfit images not found"
            }
        }

        // Retry and finish buttons
        binding.btnRetry.setOnClickListener {
            navController.navigate(R.id.action_predictionResultFragment_to_prediction_styling_fragment)
        }

        binding.btnFinish.setOnClickListener {
            navController.navigate(R.id.action_predictionResultFragment_to_navigation_home)
        }

        return binding.root
    }
}
