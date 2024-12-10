package com.example.somatchapp.ui.my_catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.somatchapp.R
import com.example.somatchapp.data.local.OutfitStyleRoomDatabase
import com.example.somatchapp.data.local.entity.MyCatalog
import com.example.somatchapp.data.local.entity.OutfitStyle
import com.example.somatchapp.data.local.room.MyCatalogRoomDatabase
import com.example.somatchapp.data.repository.OutfitStyleRepository
import com.example.somatchapp.databinding.FragmentMyCatalogBinding
import kotlinx.coroutines.launch

class MyCatalogFragment : Fragment() {

    private var _binding: FragmentMyCatalogBinding? = null
    private val binding get() = _binding!!
    private lateinit var myCatalogAdapter: MyCatalogAdapter
    private lateinit var navController: NavController
    private lateinit var itemType: String

    private lateinit var outfitStyleRepository: OutfitStyleRepository

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
            { selectedCatalog -> updateSelectedCatalog(selectedCatalog) },
            itemType
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

        // Get itemType argument
        itemType = MyCatalogFragmentArgs.fromBundle(requireArguments()).itemType

        // Initialize OutfitStyleRepository
        val database = MyCatalogRoomDatabase.getDatabase(requireContext())
        outfitStyleRepository = OutfitStyleRepository(database.outfitStyleDao())

        // Set visibility based on itemType
        if (itemType != "empty") {
            binding.buttonAddOutfits.visibility = View.GONE
        } else {
            binding.constraintHint.visibility = View.GONE
            binding.buttonSave.visibility = View.GONE
        }

        // Load data from database
        loadMyCatalogData()

        // Handle button click
        binding.buttonAddOutfits.setOnClickListener {
            navController.navigate(R.id.action_myCatalogFragment_to_commonScannerFragment)
        }

        binding.buttonSave.setOnClickListener {
            saveSelectedOutfit()
        }
    }

    private fun loadMyCatalogData() {
        val database = MyCatalogRoomDatabase.getDatabase(requireContext())
        val dao = database.myCatalogDao()

        lifecycleScope.launch {
            if (itemType != "empty") {
                dao.getAllOutOfStyle().observe(viewLifecycleOwner) { catalogList ->
                    myCatalogAdapter.updateData(catalogList)
                }
            } else {
                dao.getAllMyCatalog().observe(viewLifecycleOwner) { catalogList ->
                    myCatalogAdapter.updateData(catalogList)
                }
            }
        }
    }

    private fun updateSelectedCatalog(selectedCatalog: MyCatalog) {
        val database = MyCatalogRoomDatabase.getDatabase(requireContext())
        val dao = database.myCatalogDao()

        lifecycleScope.launch {
            // Reset isSelected for all items
            dao.resetSelection()
            // Set isSelected for the selected item
            dao.updateMyCatalog(selectedCatalog.copy(isSelected = true))
        }
    }

    private fun saveSelectedOutfit() {
        val database = MyCatalogRoomDatabase.getDatabase(requireContext())
        val myCatalogDao = database.myCatalogDao()
        val database2 = OutfitStyleRoomDatabase.getDatabase(requireContext())
        val outfitStyleDao = database2.outfitStyleDao()

        lifecycleScope.launch {
            // Get the selected MyCatalog item
            val selectedCatalog = myCatalogDao.getSelectedMyCatalog()

            if (selectedCatalog != null) {
                // Create a new OutfitStyle entity
                val newOutfitStyle = OutfitStyle(
                    type = itemType,
                    image = selectedCatalog.image,
                    myCatalogId = selectedCatalog.id
                )

                // Insert the new OutfitStyle into the database
                outfitStyleDao.insert(newOutfitStyle)

                myCatalogDao.setInStyle(selectedCatalog.id)
                myCatalogDao.resetSelection()

                // Show a success message
                Toast.makeText(requireContext(), "Item berhasil disimpan", Toast.LENGTH_SHORT).show()

                // Navigate back
                navController.popBackStack()
                navController.popBackStack()

            } else {
                // Show an error message if no item is selected
                Toast.makeText(requireContext(), "Pilih item terlebih dahulu!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
