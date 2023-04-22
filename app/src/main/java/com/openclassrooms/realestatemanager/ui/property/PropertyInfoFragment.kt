package com.openclassrooms.realestatemanager.ui.property

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.databinding.FragmentInfoPropertyBinding

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyInfoFragment : Fragment() {
    private lateinit var binding: FragmentInfoPropertyBinding
    private val args: InfoPropertyFragmentArgs by navArgs()
    private val propertyWithDetails: PropertyWithDetails? by lazy { args.propertyWithDetails }


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

        propertyWithDetails?.let { displayPropertyDetails(it) }
    }

    private fun displayPropertyDetails(propertyWithDetails: PropertyWithDetails) {
        binding.roomType.text = propertyWithDetails.property.typeOfHouse
        // binding. .text = propertyWithDetails.property.price.toString()
        binding.squareFeetValue.text = propertyWithDetails.property.squareFeet.toString()
        binding.roomsValue.text = propertyWithDetails.property.roomsCount.toString()
        binding.bedroomsValue.text = propertyWithDetails.property.bedroomsCount.toString()
        binding.bathroomsValue.text = propertyWithDetails.property.bathroomsCount.toString()
        binding.description.text = propertyWithDetails.property.description
        if (propertyWithDetails.property.isSold == true)
            binding.date.text = "Sold on ${propertyWithDetails.property.dateSold}"
        else
            binding.date.text = "Selling date: ${propertyWithDetails.property.dateStartSelling}"
    }
}

