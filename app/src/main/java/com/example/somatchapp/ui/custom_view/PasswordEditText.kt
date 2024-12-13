package com.example.somatchapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.somatchapp.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var passwordToggleImage: Drawable
    private var isPasswordVisible = false

    init {
        // Gambar toggle untuk password visibility
        passwordToggleImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility_24dp) as Drawable
        setOnTouchListener(this)

        // Atur inputType default sebagai password
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        // Atur hint default
        hint = "Password"

        // Tambahkan TextWatcher untuk validasi panjang password
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    showPasswordToggleButton()
                } else {
                    hidePasswordToggleButton()
                }

                if (s.length in 1..7) {
                    error = "Password minimal 8 karakter"
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    // Fungsi untuk mengatur hint secara dinamis
    fun setHintText(hintText: String) {
        hint = hintText
    }

    private fun showPasswordToggleButton() {
        setButtonDrawables(endOfTheText = passwordToggleImage)
    }

    private fun hidePasswordToggleButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(startOfTheText, topOfTheText, endOfTheText, bottomOfTheText)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val passwordToggleStart: Float
            var isPasswordToggleClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                passwordToggleStart = (passwordToggleImage.intrinsicWidth + paddingStart).toFloat()
                if (event.x < passwordToggleStart) isPasswordToggleClicked = true
            } else {
                passwordToggleStart = (width - paddingEnd - passwordToggleImage.intrinsicWidth).toFloat()
                if (event.x > passwordToggleStart) isPasswordToggleClicked = true
            }

            if (isPasswordToggleClicked) {
                if (event.action == MotionEvent.ACTION_UP) {
                    togglePasswordVisibility()
                    return true
                }
            }
        }
        return false
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            passwordToggleImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility_off_24dp) as Drawable
        } else {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            passwordToggleImage = ContextCompat.getDrawable(context, R.drawable.ic_visibility_24dp) as Drawable
        }

        setSelection(text?.length ?: 0)

        setButtonDrawables(endOfTheText = passwordToggleImage)
    }
}