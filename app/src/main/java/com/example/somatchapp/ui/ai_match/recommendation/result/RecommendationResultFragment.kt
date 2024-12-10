package com.example.somatchapp.ui.ai_match.recommendation.result

import android.app.AlertDialog
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.somatchapp.R

class RecommendationResultFragment : Fragment() {

    companion object {
        fun newInstance() = RecommendationResultFragment()
    }

    private val viewModel: RecommendationResultViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recommendation_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton: Button = view.findViewById(R.id.save_button)

        saveButton.setOnClickListener {
            showSaveConfirmationDialog()
        }
    }

    private fun showSaveConfirmationDialog() {
        // Inflate layout modal
        val dialogView = layoutInflater.inflate(R.layout.fragment_modal_save_confirm, null)

        // Buat AlertDialog
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // Akses elemen di dalam layout modal
        val btnCancel = dialogView.findViewById<TextView>(R.id.button_cancel)
        val btnConfirm = dialogView.findViewById<TextView>(R.id.button_save)

        btnCancel.setOnClickListener {
            dialog.dismiss() // Tutup modal
        }

        btnConfirm.setOnClickListener {
            // Tambahkan logika untuk menyimpan data
            Toast.makeText(requireContext(), "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
            dialog.dismiss() // Tutup modal
        }

        // Tampilkan dialog
        dialog.show()
    }
}