package com.example.somatchapp.ui.profile


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.somatchapp.R


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find Views by ID
        val profileSetting = view.findViewById<LinearLayout>(R.id.profile_setting)
        val lemari = view.findViewById<LinearLayout>(R.id.lemari)
        val keluar = view.findViewById<LinearLayout>(R.id.keluar)
        val hapusAkun = view.findViewById<LinearLayout>(R.id.hapus_akun)

        // Navigate to Profile Setting Activity
        profileSetting.setOnClickListener {
            val intent = Intent(requireContext(), ProfileSettingActivity::class.java)
            startActivity(intent)
        }

//        // Navigate to Lemari Activity
//        wardrobe.setOnClickListener {
//            val intent = Intent(requireContext(), LemariActivity::class.java)
//            startActivity(intent)
//        }

        // Logout Alert Dialog
        keluar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { _, _ ->
                    // Handle logout logic here
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Delete Account Alert Dialog
        hapusAkun.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Hapus Akun")
                .setMessage("Apakah Anda yakin ingin menghapus akun? Tindakan ini tidak dapat dibatalkan.")
                .setPositiveButton("Hapus") { _, _ ->
                    // Handle delete account logic here
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
}
