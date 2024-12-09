package com.example.somatchapp.ui.components.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.somatchapp.databinding.FragmentCardMyOutfitBinding

class CardMyOutfitFragment : Fragment() {

    private var _binding: FragmentCardMyOutfitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardMyOutfitBinding.inflate(inflater, container, false)

        // Mendapatkan data dari Bundle
        val catalogImage = arguments?.getString("catalog_image")

        // Menampilkan gambar jika ada
        catalogImage?.let {
            binding.myOutfitImage.setImageURI(it.toUri())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
