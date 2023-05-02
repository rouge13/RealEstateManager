package com.openclassrooms.realestatemanager.ui.property

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.databinding.FragmentInfoPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyInfoFragment : Fragment() {
    private lateinit var binding: FragmentInfoPropertyBinding
    private val viewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory((requireActivity().application as MainApplication).agentRepository,
            (requireActivity().application as MainApplication).propertyRepository,
            (requireActivity().application as MainApplication).addressRepository,
            (requireActivity().application as MainApplication).photoRepository,
            requireActivity().application as MainApplication
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayPropertyDetails()
    }

    private fun displayPropertyDetails() {
        viewModel.getSelectedProperty.observe(viewLifecycleOwner) { propertyWithDetails ->
            propertyWithDetails?.let {
                val photoViewPager = binding.photoPropertyViewPager
                val photoList = propertyWithDetails.photos
                val adapter =
                    propertyWithDetails.property.isSold?.let { PropertyInfoAdapter(this, photoList, it) }
                photoViewPager.adapter = adapter
                binding.squareFeetValue.text = propertyWithDetails.property.squareFeet.toString()
                binding.roomsValue.text = propertyWithDetails.property.roomsCount.toString()
                binding.bedroomsValue.text = propertyWithDetails.property.bedroomsCount.toString()
                binding.bathroomsValue.text = propertyWithDetails.property.bathroomsCount.toString()
                binding.description.text = propertyWithDetails.property.description
                if (propertyWithDetails.property.isSold == true) {
                    binding.date.setTextColor(resources.getColor(android.R.color.holo_red_dark))
                    binding.date.text = "Solded on : ${propertyWithDetails.property.dateSold}"
                } else
                    binding.date.text = "Selling date: ${propertyWithDetails.property.dateStartSelling}"

                binding.imageMoveAfterButton.setOnClickListener {
                    photoViewPager.currentItem = photoViewPager.currentItem + 1
                }
                binding.imageMoveBeforeButton.setOnClickListener {
                    photoViewPager.currentItem = photoViewPager.currentItem - 1
                }
                binding.backwardProperty.setOnClickListener {
                    requireActivity().onBackPressed()
                }
            }
        }
    }
}

