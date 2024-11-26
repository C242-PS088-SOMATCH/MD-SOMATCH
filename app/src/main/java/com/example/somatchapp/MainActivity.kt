package com.example.somatchapp

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.somatchapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Setup AppBarConfiguration for top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_profile
            )
        )

        // Setup BottomNavigationView with NavController
        val navView: BottomNavigationView = binding.bottomNavigationView
        navView.setupWithNavController(navController)

        // Handle visibility of BottomNavigationView and FAB
        val aiFAB = binding.aiFab
        val bottAppBar = binding.bottomAppBar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home, R.id.navigation_profile -> {
                    navView.visibility = View.VISIBLE
                    aiFAB.visibility = View.VISIBLE
                    bottAppBar.visibility = View.VISIBLE
                }
                else -> {
                    navView.visibility = View.GONE
                    aiFAB.visibility = View.GONE
                    bottAppBar.visibility = View.GONE
                }
            }
        }

        // Set click listener for the FAB
        aiFAB.setOnClickListener {
            // Navigate to the DeciderFragment
            navController.navigate(R.id.chooserFragment)
        }

        // Handle back press to navigate back in NavController
        onBackPressedDispatcher.addCallback(this) {
            if (navController.previousBackStackEntry != null) {
                navController.popBackStack() // Go back to the previous destination
            } else {
                finish() // Exit the app if no back stack is present
            }
        }
    }
}
