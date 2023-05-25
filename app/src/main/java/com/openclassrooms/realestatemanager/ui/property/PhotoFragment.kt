package com.openclassrooms.realestatemanager.ui.property

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        // Set the photo and description in the view
        val photoResourceId = getPhotoResourceId(photoEntity.photo)
        binding.imageInfoView.setImageResource(photoResourceId)
        binding.roomType.text = photoEntity.description

        // Hide the sold out banner if the property is not sold
        binding.soldText.visibility = if (soldOut) View.VISIBLE else View.GONE
        binding.imageInfoView.alpha = if (soldOut) 0.3f else 1f
    }

    private fun getPhotoResourceId(photo: String?): Int {
        return if (photo.isNullOrEmpty()) {
            DEFAULT_PHOTO_RESOURCE_ID
        } else {
            // Retrieve the resource ID using the photo name
            val context = requireContext()
            context.resources.getIdentifier(photo, "drawable", context.packageName)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEFAULT_PHOTO_RESOURCE_ID = R.drawable.ic_default_property
        private val defaultPhoto = PhotoEntity(
            id = -1,
            photo = null,
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


