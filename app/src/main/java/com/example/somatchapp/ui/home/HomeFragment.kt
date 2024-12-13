package com.example.somatchapp.ui.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.somatchapp.R
import com.example.somatchapp.data.local.entity.TrendFashion
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.data.repository.MyCatalogRepository
import com.example.somatchapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var allItemAdapter: AllItemAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var myCatalogRepository: MyCatalogRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize repository
        myCatalogRepository = MyCatalogRepository(ApiConfig().getApiService()) // Or use Dependency Injection

        // Initialize ViewModel with the ViewModelFactory
        homeViewModel = ViewModelProvider(this, HomeViewModelFactory(myCatalogRepository)).get(HomeViewModel::class.java)

        // Disable Night Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NavController
        navController = findNavController()

        // Setup RecyclerView for Trend Fashion (dummy data)
        val trendList = listOf(
            TrendFashion(R.drawable.image_monochrome_style, "Monokrom", "#Hitam #Putih"),
            TrendFashion(R.drawable.image_pastel_style, "Pastel", "#Biru #Pink"),
            TrendFashion(R.drawable.image_earthtone_style, "Earthtone", "#Hijau #Coklat"),
            TrendFashion(R.drawable.image_colorful_style, "Colorful", "#Ungu #Oranye"),
            TrendFashion(R.drawable.image_minimalist_style, "Minimalist", "#Putih #Abu"),
            TrendFashion(R.drawable.image_neutral_style, "Neutral", "#Krem #Putih"),
            TrendFashion(R.drawable.image_florals_style, "Florals", "#Hitam #Pink"),
            TrendFashion(R.drawable.image_vintage_style, "Vintage", "#Putih #Coklat")
        )

        val trendAdapter = TrendFashionAdapter(trendList)
        binding.rvTrendList.apply {
            adapter = trendAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        // Setup RecyclerView for All Items (API Data)
        allItemAdapter = AllItemAdapter()
        binding.rvProductList.apply {
            adapter = allItemAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        // Observe ViewModel Data
        observeViewModel()

        // Fetch data from ViewModel, passing the context
        homeViewModel.fetchItems(requireContext()) // Pass context to fetchItems

        setupNavigation()
    }

    private fun observeViewModel() {
        homeViewModel.items.observe(viewLifecycleOwner) { items ->
            binding.spinKit.visibility = View.GONE
            Log.d(TAG, "Items fetched: $items")
            allItemAdapter.submitList(items)
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.spinKit.visibility = View.GONE
            Log.e(TAG, "Error fetching items: $error")
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    // Navigation Example (if applicable)
    private fun setupNavigation() {
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