package com.example.somatchapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.somatchapp.R
import com.example.somatchapp.databinding.ActivityMainBinding
import com.example.somatchapp.ui.auth.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = getTokenFromSharedPreferences(this)

        // Pastikan NavController diinisialisasi setelah layout
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController

        // Jika token tidak ada, navigasi ke StarterFragment dan sembunyikan UI
        if (token == null) {
            navController.navigate(R.id.starter_fragment)
            setUIVisibility(View.GONE) // Menyembunyikan BottomNavigationView, FAB, dan AppBar
        } else {
            // Set up BottomNavigationView dengan NavController
            val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.navigation_home, R.id.navigation_profile)
            )
            binding.bottomNavigationView.setupWithNavController(navController)

            // Handle perubahan visibilitas UI (BottomNavigationView, FAB, AppBar)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.navigation_home, R.id.navigation_profile -> {
                        setUIVisibility(View.VISIBLE) // Menampilkan UI di halaman ini
                    }
                    R.id.starter_fragment -> {
                        setUIVisibility(View.GONE) // Menyembunyikan UI di StarterFragment
                    }
                    else -> {
                        setUIVisibility(View.GONE) // Menyembunyikan UI di halaman lain
                    }
                }
            }

            // Set click listener untuk FAB
            binding.aiFab.setOnClickListener {
                navController.navigate(R.id.ai_chooser_fragment)
            }

            // Menangani back press untuk menavigasi kembali di NavController
            onBackPressedDispatcher.addCallback(this) {
                if (navController.previousBackStackEntry != null) {
                    navController.popBackStack()
                } else {
                    finish()
                }
            }
        }
    }

    // Helper function untuk mendapatkan token dari SharedPreferences
    private fun getTokenFromSharedPreferences(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    // Fungsi untuk mengatur visibilitas UI
    private fun setUIVisibility(visibility: Int) {
        binding.bottomNavigationView.visibility = visibility
        binding.aiFab.visibility = visibility
        binding.bottomAppBar.visibility = visibility
    }
}