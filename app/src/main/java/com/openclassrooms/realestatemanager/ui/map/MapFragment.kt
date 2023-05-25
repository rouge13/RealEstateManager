package com.openclassrooms.realestatemanager.ui.map

import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.openclassrooms.realestatemanager.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.LocationDetails
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel

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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync { map ->
            googleMap = map
            agentViewModel.getLocationLiveData().observe(viewLifecycleOwner) {
                if (it != null) {
                    updateMapWithAgentLocation(it)
                }
            }
            propertyViewModel.getPropertiesWithDetails.observe(viewLifecycleOwner) {
                if (it != null) {
                    setMarkers(it, view)
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
        propertiesWithDetails.forEach { propertyWithDetails ->
            val addressString =
                (propertyWithDetails.address?.streetNumber + " " + propertyWithDetails.address?.streetName + " " + propertyWithDetails.address?.city + " " + propertyWithDetails.address?.zipCode + " " + propertyWithDetails.address?.country)
            val address = Geocoder(view.context).getFromLocationName(addressString, 1)
            val location = address?.get(0)?.latitude?.let { it1 ->
                address[0]?.longitude?.let { it2 ->
                    LatLng(
                        it1,
                        it2
                    )
                }
            }
            val marker = googleMap.addMarker(
                MarkerOptions().position(location!!).title(propertyWithDetails.property.typeOfHouse)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            marker?.let { propertyMarkers[it] = propertyWithDetails }
        }
        googleMap.setOnMarkerClickListener { marker ->
            propertyMarkers[marker]?.let { propertyWithDetails ->
                propertyViewModel.setSelectProperty(propertyWithDetails)
                if (!activity?.resources?.getBoolean(R.bool.isTwoPanel)!!){
                    findNavController().navigate(R.id.infoPropertyFragment)
                } else {
                    findNavController().popBackStack()
                }
                true
            } ?: false
        }
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
}
