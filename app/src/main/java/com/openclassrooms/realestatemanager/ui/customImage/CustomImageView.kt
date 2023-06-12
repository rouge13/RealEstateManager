package com.openclassrooms.realestatemanager.ui.customImage

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.openclassrooms.realestatemanager.R


class CustomImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    private var description: String = ""
    private var soldOut: Boolean = false
    private var descriptionTextView: TextView
    private var soldOutTextView: TextView
    private var imageView: ImageView

    init {
        // Obtain the custom attributes
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView)
        description = typedArray.getString(R.styleable.CustomImageView_descriptionValue) ?: ""
        soldOut = typedArray.getBoolean(R.styleable.CustomImageView_soldValue, false)
        typedArray.recycle()

        // Create the description TextView dynamically
        descriptionTextView = TextView(context).apply {
            paramOfDescriptionTextView(context)
        }

        // Create the soldOut TextView dynamically
        soldOutTextView = TextView(context).apply {
            paramOfSoldTextView(context)
        }

        // Create the ImageView for displaying the image
        imageView = ImageView(context).apply {
            paramOfImageView()
        }

        // Add the views to the CustomImageView
        addView(imageView)
        addView(descriptionTextView)
        addView(soldOutTextView)

        // Apply the initial attribute value
        setDescription(description)
        setSoldOut(soldOut)
    }

    private fun ImageView.paramOfImageView() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        adjustViewBounds = true
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    private fun TextView.paramOfDescriptionTextView(context: Context) {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            setMargins(0, 0, 0, 16)
        }
        setTextColor(ContextCompat.getColor(context, R.color.black))
        setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        visibility = GONE
    }

    private fun TextView.paramOfSoldTextView(context: Context) {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }
        setTextColor(ContextCompat.getColor(context, R.color.yellow))
        setBackgroundColor(ContextCompat.getColor(context, R.color.red))
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
        text = context.getString(R.string.sold_property)
        visibility = GONE
    }

    private fun setDescription(description: String) {
        this.description = description
        descriptionTextView.text = description
        descriptionTextView.visibility = if (description.isNotEmpty()) View.VISIBLE else View.GONE
        invalidate()
    }

    fun setDescriptionValue(value: String) {
        setDescription(value)
    }

    fun setSoldOut(soldOut: Boolean) {
        this.soldOut = soldOut
        soldOutTextView.visibility = if (soldOut) View.VISIBLE else View.GONE
        alpha = if (soldOut) 0.3f else 1f
        invalidate()
    }

    fun setImageDrawable(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }

    fun setImageResource(resourceId: Int) {
        imageView.setImageResource(resourceId)
    }
}





