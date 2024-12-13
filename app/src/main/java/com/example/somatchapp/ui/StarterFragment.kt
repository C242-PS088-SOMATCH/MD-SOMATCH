package com.example.somatchapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.somatchapp.R
import com.example.somatchapp.databinding.FragmentStarterBinding
import com.example.somatchapp.ui.auth.LoginActivity
import com.example.somatchapp.ui.auth.RegisterActivity

class StarterFragment : Fragment() {

    private var _binding: FragmentStarterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStarterBinding.inflate(inflater, container, false)

        // Set up the button click listeners
        binding.buttonLogin.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonRegister.setOnClickListener {
            // Navigate to RegisterActivity
            val intent = Intent(activity, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Call playAnimation to start animations
        playAnimation()

        return binding.root
    }

    private fun playAnimation() {
        // Set initial alpha to 0 for all elements
        binding.buttonLogin.alpha = 0f
        binding.buttonRegister.alpha = 0f
        binding.tvWelcome.alpha = 0f
        binding.tvDescription.alpha = 0f

        // Create the animations for each view
        val ivLogoAnim = ObjectAnimator.ofFloat(binding.ivLogo, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val login = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(700)
        val signup = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(700)
        val title = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding.tvDescription, View.ALPHA, 1f).setDuration(600)

        // Play animations together for buttons
        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        // Play animations sequentially for title, description, and buttons
        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }

        // Start the translation animation for the logo
        ivLogoAnim.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}