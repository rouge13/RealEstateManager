package com.openclassrooms.realestatemanager.ui.property

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.FragmentPhotoBinding

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PhotoFragment : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var photoEntity: PhotoEntity
    private var soldOut: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the arguments
        val args = requireArguments()
        photoEntity = args.getParcelable("photoEntity") ?: defaultPhoto
        soldOut = args.getBoolean("soldOut")

        // Set the photo and description in the CustomImageView
        val photoUrl = getPhotoUrl(photoEntity.photoURI)
        if (photoUrl != null) {
            binding.customImageView.loadImage(photoUrl, calculateImageAspectRatio())
        } else {
            binding.customImageView.setImageResource(DEFAULT_PHOTO_RESOURCE_ID)
        }

        // Set the description and soldOut separately in the CustomImageView
        binding.customImageView.setDescriptionValue(photoEntity.description ?: "")
        binding.customImageView.setSoldOut(soldOut)


    }

    private fun getPhotoUrl(photo: String?): String? {
        if (!photo.isNullOrEmpty()) {
            val context = requireContext()
            val resourceId = context.resources.getIdentifier(photo, "drawable", context.packageName)
            if (resourceId != 0) {
                // Return the resource identifier as a string
                return "android.resource://${context.packageName}/$resourceId"
            } else {
                // Return the URI for other photo types
                return photo
            }
        }
        return null
    }

    private fun calculateImageAspectRatio(): Float {
        // Calculate the aspect ratio based on your logic
        // For example, you can return a fixed aspect ratio or calculate it dynamically
        return 1f // Default aspect ratio is 1:1
    }
//    private fun getPhotoDrawable(photo: String?): Drawable? {
//        if (!photo.isNullOrEmpty()) {
//            val context = requireContext()
//            val resourceId = context.resources.getIdentifier(photo, "drawable", context.packageName)
//            if (resourceId != 0) {
//                return ContextCompat.getDrawable(context, resourceId)
//            }
//        }
//        return null
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEFAULT_PHOTO_RESOURCE_ID = R.drawable.ic_default_property
        private val defaultPhoto = PhotoEntity(
            id = -1,
            photoURI = null,
            description = "No photo!"
        )

        fun newInstance(photoEntity: PhotoEntity?, soldOut: Boolean): PhotoFragment {
            val args = Bundle().apply {
                putParcelable("photoEntity", photoEntity)
                putBoolean("soldOut", soldOut)
            }
            val fragment = PhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}



