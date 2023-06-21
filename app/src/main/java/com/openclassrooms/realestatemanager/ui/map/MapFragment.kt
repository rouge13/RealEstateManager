package com.openclassrooms.realestatemanager.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.LocationDetails
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.property.PropertyInfoFragment
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MapFragment : Fragment() {
    private val agentViewModel: SharedAgentViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val propertyViewModel: SharedPropertyViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels { ViewModelFactory(requireActivity().application as MainApplication) }
    private lateinit var fragmentMapBinding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private var agentMarker: Marker? = null
    private val propertyMarkers = mutableMapOf<Marker, PropertyWithDetails>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)
        return fragmentMapBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = if(!isDualPanel()) {childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?} else {childFragmentManager.findFragmentById(R.id.map_600sp) as SupportMapFragment?}
        mapFragment?.getMapAsync { map ->
            googleMap = map
            viewLifecycleOwner.lifecycleScope.launch {
                propertyViewModel.getPropertiesWithDetails.collect {
                    setMarkers(it, view)
                }
            }
            agentViewModel.getLocationLiveData().observe(viewLifecycleOwner) {
                if (it != null) {
                    updateMapWithAgentLocation(it)
                }
            }
        }
        initSharedNavigationViewModelSearchAction()
    }

    private fun initSharedNavigationViewModelSearchAction() {
        sharedNavigationViewModel.searchClicked.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                if (!activity?.resources?.getBoolean(R.bool.isTwoPanel)!!) {
                    val action = MapFragmentDirections.actionMapFragmentToSearchFragment()
                    findNavController().navigate(action)
                } else {
                    findNavController().popBackStack()
                }
                sharedNavigationViewModel.doneNavigatingToSearch()
            }
        }
    }

    private fun setMarkers(propertiesWithDetails: List<PropertyWithDetails>, view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            val propertyWithCoordinates = propertiesWithDetails.map { propertyWithDetails ->
                val addressString = (propertyWithDetails.address?.streetNumber + " " + propertyWithDetails.address?.streetName + " " + propertyWithDetails.address?.city + " " + propertyWithDetails.address?.zipCode + " " + propertyWithDetails.address?.country)
                val location = withContext(Dispatchers.IO) { geocodeAddress(addressString) }
                propertyWithDetails to location
            }

            // Clear existing markers
            clearMarkers()

            propertyWithCoordinates.forEach { (propertyWithDetails, location) ->
                initMarker(location, propertyWithDetails)
            }
        }
        googleMap.setOnMarkerClickListener { marker ->
            propertyMarkers[marker]?.let { propertyWithDetails ->
                propertyViewModel.setSelectProperty(propertyWithDetails)
                if (!activity?.resources?.getBoolean(R.bool.isTwoPanel)!!){
                    findNavController().navigate(R.id.infoPropertyFragment)
                } else {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.info_fragment_container, PropertyInfoFragment())
                        .commit()
                }
                true
            } ?: false
        }
    }

    private fun initMarker(
        location: LatLng?,
        propertyWithDetails: PropertyWithDetails
    ) {
        val marker = location?.let {
            MarkerOptions().position(it)
                .title(propertyWithDetails.property.typeOfHouse + "" + propertyWithDetails.property.id.toString())
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        }?.let {
            googleMap.addMarker(
                it
            )
        }
        marker?.let { propertyMarkers[it] = propertyWithDetails }
    }

    private fun updateMapWithAgentLocation(location: LocationDetails?) {
        location?.let {
            val newLocation = LatLng(it.latitude, it.longitude)
            if (agentMarker == null) {
                agentMarker = googleMap.addMarker(
                    MarkerOptions()
                        .position(newLocation)
                        .title(getString(R.string.agent_location))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
            } else {
                agentMarker?.position = newLocation
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 10f))
        }
    }

    private suspend fun geocodeAddress(addressString: String): LatLng? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(requireContext())
                val addresses = geocoder.getFromLocationName(addressString, 1)
                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val location = addresses[0]
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("Geocoding", "Address: $addressString, Latitude: $latitude, Longitude: $longitude")
                        return@withContext LatLng(latitude, longitude)
                    }
                }
            } catch (e: IOException) {
                // Handle geocoding error
                e.printStackTrace()
            }
            return@withContext null
        }
    }

    private fun clearMarkers() {
        for (marker in propertyMarkers.keys) {
            marker.remove()
        }
        propertyMarkers.clear()
    }

    override fun onResume() {
        super.onResume()
        if (isDualPanel()) {
            childFragmentManager.beginTransaction()
                .replace(R.id.info_fragment_container, PropertyInfoFragment())
                .commit()
        }
    }

    private fun isDualPanel(): Boolean {
        return resources.getBoolean(R.bool.isTwoPanel)
    }
}
