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
        val photoDrawable = getPhotoDrawable(photoEntity.photoURI)
        if (photoDrawable != null) {
            binding.customImageView.setImageDrawable(photoDrawable)
        } else {
            binding.customImageView.setImageResource(DEFAULT_PHOTO_RESOURCE_ID)
        }

        // Set the description and soldOut separately in the CustomImageView
        binding.customImageView.setDescriptionValue(photoEntity.description ?: "")
        binding.customImageView.setSoldOut(soldOut)


    }

    private fun getPhotoDrawable(photo: String?): Drawable? {
        if (!photo.isNullOrEmpty()) {
            val context = requireContext()
            val resourceId = context.resources.getIdentifier(photo, "drawable", context.packageName)
            if (resourceId != 0) {
                return ContextCompat.getDrawable(context, resourceId)
            }

            try {
                val uri = Uri.parse(photo)
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val drawable = Drawable.createFromStream(inputStream, uri.toString())
                    inputStream.close()
                    return drawable
                }
            } catch (e: Exception) {
                // Handle any exceptions that may occur while loading the drawable from URI
            }
        }

        return null
    }

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



