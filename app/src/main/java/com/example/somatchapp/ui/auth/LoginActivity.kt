package com.example.somatchapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.example.somatchapp.data.UserRepository
import com.example.somatchapp.data.remote.retrofit.ApiConfig
import com.example.somatchapp.data.remote.retrofit.LoginResponse
import com.example.somatchapp.databinding.ActivityLoginBinding
import com.example.somatchapp.ui.MainActivity
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UserViewModel
        val userRepository = UserRepository(ApiConfig().getApiService())
        val factory = AuthViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonLogin.buttonText = "Masuk"
        binding.constraintProgressBar.visibility = View.GONE

        binding.tvRegister.setOnClickListener {
            navigateToMain()
        }

        binding.etEmail.addTextChangedListener { validateInputs() }
        binding.etsPassword.addTextChangedListener { validateInputs() }

        binding.buttonLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etsPassword.text.toString().trim()

            binding.constraintProgressBar.visibility = View.VISIBLE

            // Call login function from ViewModel
            login(email, password)
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun validateInputs() {
        val emailText = binding.etEmail.text?.toString()?.trim()
        val passwordText = binding.etsPassword.text?.toString()?.trim()
        binding.buttonLogin.isEnabled = !emailText.isNullOrEmpty() && !passwordText.isNullOrEmpty()
    }

    private fun login(email: String, password: String) {

        userViewModel.login(email, password) { response: Response<LoginResponse> ->
            binding.constraintProgressBar.visibility = View.GONE
            if (response.isSuccessful) {
                val token = response.body()?.token
                saveTokenToSharedPreferences(token)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTokenToSharedPreferences(token: String?) {
        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        token?.let {
            editor.putString("token", it)
            editor.apply()
            Log.d("LoginActivity", "Token saved: $it")
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}