package com.example.somatchapp.ui.profile

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.somatchapp.R
import com.google.android.material.textfield.TextInputEditText

class ProfileSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setting)

        // Reference UI elements
        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmail)
        val etOldPassword = findViewById<TextInputEditText>(R.id.etOldPassword)
        val etNewPassword = findViewById<TextInputEditText>(R.id.etNewPassword)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            saveProfileChanges(
                username = etUsername.text.toString(),
                email = etEmail.text.toString(),
                oldPassword = etOldPassword.text.toString(),
                newPassword = etNewPassword.text.toString()
            )
        }
    }

    private fun saveProfileChanges(username: String, email: String, oldPassword: String, newPassword: String) {
        // Validate inputs
        if (username.isBlank()) {
            showError("Username tidak boleh kosong")
            return
        }

        if (email.isBlank()) {
            showError("Email tidak boleh kosong")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Format email tidak valid")
            return
        }

        if (oldPassword.isBlank()) {
            showError("Password lama tidak boleh kosong")
            return
        }

        if (newPassword.isBlank() || newPassword.length < 8) {
            showError("Password baru harus memiliki minimal 8 karakter")
            return
        }

        // Save profile changes (add your logic for saving changes)
        Toast.makeText(this, "Perubahan profil berhasil disimpan!", Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
