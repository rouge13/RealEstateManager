package com.openclassrooms.realestatemanager.ui.customviews

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import com.openclassrooms.realestatemanager.R

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class DeleteableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val imageView: ImageView
    private val deleteButton: ImageView
    private var onDeleteButtonClickListener: OnDeleteButtonClickListener? = null

    init {
        // Create ImageView
        imageView = ImageView(context)
        val imageParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = imageParams
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        addView(imageView)

        // Create Delete Button
        deleteButton = ImageView(context)
        val buttonParams = LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
        )
        buttonParams.gravity = Gravity.TOP or Gravity.END
        deleteButton.layoutParams = buttonParams
        deleteButton.setImageResource(R.drawable.ic_delete_black_24)
        deleteButton.setOnClickListener {
            onDeleteButtonClickListener?.onDeleteButtonClick()
        }
        addView(deleteButton)

        // Retrieve custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(
                it, R.styleable.DeleteableImageView, defStyleAttr, 0
            )
            val imageDrawable = typedArray.getDrawable(R.styleable.DeleteableImageView_imageDrawable)
            setImageDrawable(imageDrawable)
            typedArray.recycle()
        }
    }

    fun setImageDrawable(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }

    fun setOnDeleteButtonClickListener(listener: OnDeleteButtonClickListener) {
        onDeleteButtonClickListener = listener
    }

    interface OnDeleteButtonClickListener {
        fun onDeleteButtonClick()
    }
}