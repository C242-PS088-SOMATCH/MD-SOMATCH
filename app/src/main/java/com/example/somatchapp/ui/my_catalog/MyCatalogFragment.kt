package com.example.somatchapp.ui.my_catalog

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.somatchapp.R
import com.example.somatchapp.data.local.OutfitStyleRoomDatabase
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.local.room.MyCatalogRoomDatabase
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.data.repository.MyCatalogRepository
import com.example.somatchapp.databinding.FragmentMyCatalogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class MyCatalogFragment : Fragment() {

    private var _binding: FragmentMyCatalogBinding? = null
    private val binding get() = _binding!!
    private lateinit var myCatalogAdapter: MyCatalogAdapter
    private lateinit var navController: NavController
    private lateinit var viewModel: MyCatalogViewModel
    private lateinit var itemType: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCatalogBinding.inflate(inflater, container, false)

        itemType = MyCatalogFragmentArgs.fromBundle(requireArguments()).itemType

        // Setup RecyclerView
        myCatalogAdapter = MyCatalogAdapter(
            emptyList(),
            requireContext(),
            itemType,
            outfitStyleDao = OutfitStyleRoomDatabase.getDatabase(requireContext()).outfitStyleDao(),
        )
        binding.rvMyOutfit.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = myCatalogAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize NavController
        navController = findNavController()

        // Initialize ViewModel
        val repository = MyCatalogRepository(ApiConfig().getApiService())
        val viewModelFactory = MyCatalogViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MyCatalogViewModel::class.java)

        // Observe the catalog list data
        observeViewModel()

        // Fetch data from API
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        if (token != null) {
            viewModel.fetchMyCatalog(token)
        } else {
            Toast.makeText(requireContext(), "Token tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show()
        }

        if (itemType != "empty") {
            binding.buttonAddOutfits.visibility = View.GONE
        } else {
            binding.constraintHint.visibility = View.GONE
            binding.buttonSave.visibility = View.GONE
        }

        binding.buttonAddOutfits.setOnClickListener {
            navController.navigate(R.id.action_my_catalog_fragment_to_common_scanner_fragment)
        }

        binding.buttonSave.setOnClickListener {
            saveSelectedOutfit()
        }
    }

    private fun observeViewModel() {
        viewModel.catalogList.observe(viewLifecycleOwner) { catalogList ->
            // Menyembunyikan spinner saat data berhasil dimuat
            binding.spinKit.visibility = View.GONE

            if (catalogList != null) {
                myCatalogAdapter.updateData(catalogList) // Update UI dengan data yang diterima
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            // Menyembunyikan spinner jika terjadi error
            binding.spinKit.visibility = View.GONE

            if (errorMessage != null) {
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveSelectedOutfit() {
        val database2 = OutfitStyleRoomDatabase.getDatabase(requireContext())
        val outfitStyleDao = database2.outfitStyleDao()

        // Retrieve selected data from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        val selectedId = sharedPreferences.getInt("selected_id", -1)
        val selectedImageUrl = sharedPreferences.getString("selected_image_url", null)

        if (selectedId == -1 || selectedImageUrl == null) {
            Toast.makeText(requireContext(), "Pilih item terlebih dahulu!", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // Create a new OutfitStyle entity
                val newOutfitStyle = OutfitStyle(
                    type = itemType,
                    image = selectedImageUrl,
                    myCatalogId = selectedId
                )

                // Insert the new OutfitStyle into the database
                outfitStyleDao.insert(newOutfitStyle)

                // Show a success message
                Toast.makeText(requireContext(), "Item berhasil disimpan", Toast.LENGTH_SHORT).show()

                // Navigate back
                navController.popBackStack()
                navController.popBackStack()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        myCatalogAdapter.clearSelectedId()

        super.onDestroyView()
        _binding = null
    }
}
