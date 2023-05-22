package com.openclassrooms.realestatemanager.ui.property

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.databinding.FragmentInfoPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyInfoFragment : Fragment() {
    private lateinit var binding: FragmentInfoPropertyBinding
    private val sharedPropertyViewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels {
        ViewModelFactory(
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
        initSharedNavigationViewModelSearchAction()
        displayPropertyDetails(view)
    }

    private fun initSharedNavigationViewModelSearchAction() {
        sharedNavigationViewModel.searchClicked.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                val action =
                    PropertyInfoFragmentDirections.actionInfoPropertyFragmentToSearchFragment()
                findNavController().navigate(action)
                sharedNavigationViewModel.doneNavigatingToSearch()
            }
        }
    }

    private fun displayPropertyDetails(view: View) {
        sharedPropertyViewModel.getSelectedProperty.observe(viewLifecycleOwner) { propertyWithDetails ->
            propertyWithDetails?.let {
                binding.fragmentInfoProperty.visibility = View.VISIBLE
                initIsSoldOrNot(propertyWithDetails)
                val photoViewPager = binding.photoPropertyViewPager
                val photoList = propertyWithDetails.photos
                val adapter = propertyWithDetails.property.isSold?.let {
                    PropertyInfoAdapter(
                        this,
                        photoList,
                        it
                    )
                }
                photoViewPager.adapter = adapter
                initStaticMapImageView(propertyWithDetails, view)
                initAllTextView(propertyWithDetails)
                initAllButtons(photoViewPager)
            }
        }
    }

    private fun initStaticMapImageView(propertyWithDetails: PropertyWithDetails, view: View) {
        val staticMapImageView: ImageView = view.findViewById(R.id.static_map_image_view)
        val addressString = (propertyWithDetails.address?.streetNumber + " " + propertyWithDetails.address?.streetName + " " + propertyWithDetails.address?.city + " " + propertyWithDetails.address?.zipCode + " " + propertyWithDetails.address?.country)
        val address = Geocoder(view.context).getFromLocationName(addressString, 1)
        val location = address?.get(0)?.latitude?.let { it1 -> address[0]?.longitude?.let { it2 -> LatLng(it1, it2) } }
        val marker = "&markers=color:red%7Alabel:S%7C" + location?.latitude + "," + location?.longitude
        val staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + location?.latitude + "," + location?.longitude + "&zoom=15&size=400x400&markers=$marker&key=" + getString(R.string.google_map_key)
        Glide.with(view)
            .load(staticMapUrl)
            .centerCrop()
            .into(staticMapImageView)
    }

    private fun initAllTextView(propertyWithDetails: PropertyWithDetails) {
        Log.d("PropertyInfoFragment", "initAllTextView: $propertyWithDetails")
        binding.squareFeetValue.text = propertyWithDetails.property.squareFeet.toString()
        binding.roomsValue.text = propertyWithDetails.property.roomsCount.toString()
        binding.bedroomsValue.text = propertyWithDetails.property.bedroomsCount.toString()
        binding.bathroomsValue.text = propertyWithDetails.property.bathroomsCount.toString()
        binding.description.text = propertyWithDetails.property.description
        initAddressDetails(propertyWithDetails)
    }

    private fun initAddressDetails(propertyWithDetails: PropertyWithDetails) {
        Log.d("PropertyInfoFragment", "initAddressDetails: $propertyWithDetails")
        binding.locationLine1.text = (buildString {
            append(propertyWithDetails.address?.streetNumber)
            append(" ")
            append(propertyWithDetails.address?.streetName)
        })
        if (propertyWithDetails.address?.apartmentDetails != "") {
            binding.locationLine2.text = (buildString {
                append(propertyWithDetails.address?.apartmentDetails)
            })
        }
        binding.locationLine3.text = (buildString {
            append(propertyWithDetails.address?.city)
        })
        binding.locationLine4.text = (buildString {
            append(propertyWithDetails.address?.zipCode)
        })
        binding.locationLine5.text = (buildString {
            append(propertyWithDetails.address?.country)
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initIsSoldOrNot(propertyWithDetails: PropertyWithDetails) {
        Log.d("PropertyInfoFragment", "initIsSoldOrNot: $propertyWithDetails")
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        if (propertyWithDetails.property.isSold == true) {
            binding.date.setTextColor(resources.getColor(R.color.red))
            val dateSold = propertyWithDetails.property.dateSold?.let { Date(it) }
            binding.date.text = "Sold on : ${dateSold?.let { sdf.format(it) }}"
        } else {
            binding.date.setTextColor(resources.getColor(R.color.green))
            val dateStartSelling = propertyWithDetails.property.dateStartSelling?.let { Date(it) }
            binding.date.text = "Sale date: ${dateStartSelling?.let { sdf.format(it) }}"
        }
    }

    private fun initAllButtons(photoViewPager: ViewPager2) {
        Log.d("PropertyInfoFragment", "initAllButtons: $photoViewPager")

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

    override fun onResume() {
        super.onResume()
        if (isDualPanel()) {
            binding.backwardProperty.visibility = View.GONE
        } else {
            binding.backwardProperty.visibility = View.VISIBLE
        }
    }

    private fun isDualPanel(): Boolean {
        return activity?.resources?.getBoolean(R.bool.isTwoPanel) == true
    }
}

