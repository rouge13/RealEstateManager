package com.openclassrooms.realestatemanager.ui.property

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.FragmentPhotoBinding

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PhotoFragment(private val get: PhotoEntity, private val soldOut: Boolean) : Fragment() {

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set the photo and description in the view
        val idPhotoResources = resources.getIdentifier(
            get.photo, "drawable", requireContext().packageName
        )
        binding.imageInfoView.setImageResource(idPhotoResources)
        binding.roomType.text = get.description
        // Hide the sold out banner if the property is not sold

        if (!soldOut) {
            binding.soldText.visibility = View.GONE
            binding.imageInfoView.alpha = 1f
        } else {
            binding.soldText.visibility = View.VISIBLE
            binding.imageInfoView.alpha = 0.3f
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}