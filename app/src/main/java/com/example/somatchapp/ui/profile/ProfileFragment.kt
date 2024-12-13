package com.example.somatchapp.ui.profile

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.somatchapp.R
import com.example.somatchapp.data.UserRepository
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.databinding.FragmentProfileBinding
import com.example.somatchapp.ui.auth.LoginActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        // Initialize UserRepository
        val apiService = ApiConfig().getApiService()
        val userRepository = UserRepository(apiService)

        // Use factory to initialize ViewModel
        val factory = ProfileViewModelFactory(userRepository)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        setupObservers()
        setupListeners()

        // Fetch user profile
        fetchUserProfile()
    }

    private fun setupObservers() {
        viewModel.userProfileResponse.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val userProfile = response.body()
                userProfile?.data?.let { data ->
                    // Handle first user profile if the data is a list
                    val firstUserProfile = data.firstOrNull()
                    firstUserProfile?.let {
                        binding.username.text = it.name
                        binding.userEmail.text = it.email
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Gagal mengambil profil: ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setupListeners() {
        binding.profileSetting.setOnClickListener {
            navController.navigate(R.id.action_navigation_profile_to_profile_setting_fragment)
        }

        binding.myCatalog.setOnClickListener {
            val action = ProfileFragmentDirections.actionNavigationProfileToMyCatalogFragment("empty")
            navController.navigate(action)
        }

        binding.logout.setOnClickListener {
            showLogoutDialog()
        }

        binding.deleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ -> clearUserSession() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteAccountDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Akun")
            .setMessage("Apakah Anda yakin ingin menghapus akun? Tindakan ini tidak dapat dibatalkan.")
            .setPositiveButton("Hapus") { _, _ -> handleDeleteAccount() }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun handleDeleteAccount() {
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            val bearerToken = "$token"
            viewModel.deleteUser(bearerToken)
            clearUserSession()
        } else {
            Toast.makeText(requireContext(), "Token tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    private fun clearUserSession() {
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE)
        sharedPreferences.edit().remove("token").apply()

        Log.d("Logout", "Token dihapus dari SharedPreferences")
        Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun fetchUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (token != null) {
            val bearerToken = "$token"
            viewModel.getUserProfile(bearerToken)
        } else {
            Toast.makeText(requireContext(), "Token tidak ditemukan, silakan login ulang.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}