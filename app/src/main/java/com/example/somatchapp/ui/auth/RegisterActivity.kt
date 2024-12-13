package com.example.somatchapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.somatchapp.R
import com.example.somatchapp.data.UserRepository
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userViewModel: AuthViewModel  // ViewModel to handle API calls
    private lateinit var userRepository: UserRepository  // Repository to interact with the API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the repository and ViewModel using the factory
        userRepository = UserRepository(ApiConfig().getApiService())  // Adjust based on your API service setup
        val viewModelFactory = AuthViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, viewModelFactory).get(AuthViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonRegister.buttonText = "Daftar"
        binding.constraintProgressBar.visibility = View.GONE
        binding.etsConfirmPassword.setHintText("Konfirmasi Password")

        binding.tvLogin.setOnClickListener {
            navigateToLogin()
        }

        // Add text watchers for validation
        binding.etUsername.addTextChangedListener { validateInputs() }
        binding.etEmail.addTextChangedListener { validateInputs() }
        binding.etsPassword.addTextChangedListener { validateInputs() }
        binding.etsConfirmPassword.addTextChangedListener { validateInputs() }

        binding.buttonRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val name = username
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etsPassword.text.toString().trim()

            binding.constraintProgressBar.visibility = View.VISIBLE
            // Call register function
            register(name, email, username, password)
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateInputs() {
        val nameText = binding.etUsername.text?.toString()?.trim()
        val emailText = binding.etEmail.text?.toString()?.trim()
        val passwordText = binding.etsPassword.text?.toString()?.trim()
        val confirmPasswordText = binding.etsConfirmPassword.text?.toString()?.trim()

        val isInputValid = !nameText.isNullOrEmpty() &&
                !emailText.isNullOrEmpty() &&
                !passwordText.isNullOrEmpty() &&
                !confirmPasswordText.isNullOrEmpty()

        val isPasswordMatch = passwordText == confirmPasswordText

        if (!isPasswordMatch) {
            binding.etsConfirmPassword.error = getString(R.string.password_not_match)
        } else {
            binding.etsConfirmPassword.error = null
        }

        binding.buttonRegister.isEnabled = isInputValid && isPasswordMatch
    }

    // Register the user by calling the API
    private fun register(name: String, email: String, username: String, password: String) {
        // Show a loading indicator if needed (optional)

        userViewModel.register(name, email, username, password) { response ->
            binding.constraintProgressBar.visibility = View.GONE
            if (response.isSuccessful) {
                // Handle success
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                navigateToLogin()  // Navigate to LoginActivity after successful registration
            } else {
                // Handle error - show an error message
                Toast.makeText(this, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}