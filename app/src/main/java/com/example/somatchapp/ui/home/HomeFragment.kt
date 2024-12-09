package com.example.somatchapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.somatchapp.R
import com.example.somatchapp.data.local.entity.TrendFashion
import com.example.somatchapp.databinding.FragmentHomeBinding
import com.example.somatchapp.ui.ai_match.ScannerFragmentDirections.Companion.actionScannerFragmentToMyCatalogFragment

class HomeFragment : Fragment(R.layout.fragment_recommendation_scanner) {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var navController: NavController

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NavController
        navController = findNavController()

        // Setup RecyclerView
        val trendList = listOf(
            TrendFashion(R.drawable.image_monochrome_style, "Monokrom", "#Hitam #Putih"),
            TrendFashion(R.drawable.image_pastel_style, "Pastel", "#Biru #Pink"),
            TrendFashion(R.drawable.image_earthtone_style, "Earthtone", "#Hijau #Coklat")
        )

        val trendAdapter = TrendFashionAdapter(trendList)
        binding.rvTrendList.apply {
            adapter = trendAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        // Navigation example
        binding.myCatalogButton.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToMyCatalogFragment("empty")
            navController.navigate(action)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}