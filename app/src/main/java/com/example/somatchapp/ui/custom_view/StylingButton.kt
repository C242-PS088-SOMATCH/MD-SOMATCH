package com.example.somatchapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.somatchapp.R

class StylingButton : AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var enabledTextColor: Int = 0
    private var disabledTextColor: Int = 0
    private var enabledBackground: Drawable
    private var disabledBackground: Drawable


    var buttonText: String = "Lanjut"
        set(value) {
            field = value
            invalidate()
        }

    init {
        enabledTextColor = Color.parseColor("#FFFFFF")
        disabledTextColor = Color.parseColor("#A9A7AF")

        enabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_styling) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_styling_disable) as Drawable
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isEnabled) enabledBackground else disabledBackground

        setTextColor(if (isEnabled) enabledTextColor else disabledTextColor)

        textSize = 12f
        gravity = Gravity.CENTER

        text = buttonText
    }
}