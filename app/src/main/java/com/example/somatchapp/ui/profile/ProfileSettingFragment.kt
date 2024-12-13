package com.example.somatchapp.ui.profile

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.somatchapp.R
import com.example.somatchapp.data.UserRepository
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.data.remote.retrofit.ApiService
import com.example.somatchapp.databinding.FragmentProfileSettingBinding

class ProfileSettingFragment : Fragment() {

    private var _binding: FragmentProfileSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // Inisialisasi ViewModel
        val bearerToken = "$token" // Ganti dengan token Anda
        // Initialize UserRepository
        val apiService = ApiConfig().getApiService()
        val userRepository = UserRepository(apiService)

        binding.etsNewPassword.setHintText("Password Baru")

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(userRepository)
        )[ProfileViewModel::class.java]

        // Observasi data profil pengguna
        observeUserProfile()

        // Memuat data profil pengguna
        profileViewModel.getUserProfile(bearerToken)
    }

    private fun observeUserProfile() {
        profileViewModel.userProfileResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                binding.spinKit.visibility = View.GONE
                val userProfileList = response.body()?.data

                if (!userProfileList.isNullOrEmpty()) {
                    val userProfile = userProfileList[0] // Ambil elemen pertama
                    binding.etUsername.setText(userProfile.username)
                    binding.etEmail.setText(userProfile.email)
                } else {
                    Toast.makeText(requireContext(), "Data profil kosong", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.spinKit.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Gagal memuat data: ${response.message()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}