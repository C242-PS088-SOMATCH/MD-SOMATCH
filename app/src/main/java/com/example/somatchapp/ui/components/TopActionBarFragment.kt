package com.example.somatchapp.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.somatchapp.databinding.FragmentComponentTopActionbarBinding

class TopActionBarFragment : Fragment() {

    private var _binding: FragmentComponentTopActionbarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using ViewBinding
        _binding = FragmentComponentTopActionbarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener for the back arrow
        binding.arrowBack.setOnClickListener {
            findNavController().navigateUp() // Navigate to the previous page
        }

        // Update the text of tv_top_actionbar based on the fragment id
        updateActionBarTitle()
    }

    private fun updateActionBarTitle() {
        val currentFragmentId = findNavController().currentDestination?.id
        val fragmentIdName = resources.getResourceEntryName(currentFragmentId ?: 0)

        // Remove the word "fragment", replace underscores with spaces, and capitalize each word
        val formattedTitle = fragmentIdName
            .replace("_", " ") // Replace underscores with spaces
            .replace("fragment", "") // Remove the word "fragment"
            .split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

        binding.tvTopActionbar.text = formattedTitle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}