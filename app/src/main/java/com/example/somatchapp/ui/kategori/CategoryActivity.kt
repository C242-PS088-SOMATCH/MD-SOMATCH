package com.example.somatchapp.ui.kategori

import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.somatchapp.R
import com.google.android.material.button.MaterialButton

class CategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.category)

        // Referensi CheckBox
        val checkBoxColorful = findViewById<CheckBox>(R.id.checkBoxColorful)
        val checkBoxEarthTone = findViewById<CheckBox>(R.id.checkBoxEarthTone)
        val checkBoxMinimalist = findViewById<CheckBox>(R.id.checkBoxMinimalist)
        val checkBoxNeutral = findViewById<CheckBox>(R.id.checkBoxNeutral)
        val checkBoxMonochrome = findViewById<CheckBox>(R.id.checkBoxMonochrome)
        val checkBoxPastel = findViewById<CheckBox>(R.id.checkBoxPastel)
        val checkBoxFloral = findViewById<CheckBox>(R.id.checkBoxFloral)
        val checkBoxBold = findViewById<CheckBox>(R.id.checkBoxBold)
        val checkBoxVintage = findViewById<CheckBox>(R.id.checkBoxVintage)

        // Referensi tombol "Lanjut"
        val btnLanjut = findViewById<MaterialButton>(R.id.btnLanjut)

        // Daftar semua CheckBox
        val checkBoxes = listOf(
            checkBoxColorful,
            checkBoxEarthTone,
            checkBoxMinimalist,
            checkBoxNeutral,
            checkBoxMonochrome,
            checkBoxPastel,
            checkBoxFloral,
            checkBoxBold,
            checkBoxVintage
        )

        // Awalnya tombol "Lanjut" dinonaktifkan
        btnLanjut.isEnabled = false

        // Fungsi untuk memperbarui status tombol "Lanjut"
        fun updateButtonState() {
            btnLanjut.isEnabled = checkBoxes.any { it.isChecked }
        }

        // Set listener untuk setiap CheckBox
        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateButtonState()
            }
        }

        // Set onClickListener untuk tombol "Lanjut"
        btnLanjut.setOnClickListener {
            // Ambil kategori yang dipilih
            val selectedCategories = checkBoxes.filter { it.isChecked }.map { it.text.toString() }

            // Tampilkan kategori yang dipilih sebagai toast
            Toast.makeText(this, "Kategori yang dipilih: $selectedCategories", Toast.LENGTH_SHORT).show()

            // Tambahkan logika tambahan jika diperlukan
            // Misalnya, navigasi ke activity berikutnya
        }
    }
}
