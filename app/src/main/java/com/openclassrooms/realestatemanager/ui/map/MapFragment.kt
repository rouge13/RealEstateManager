package com.openclassrooms.realestatemanager.ui.map

import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.openclassrooms.realestatemanager.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.model.LocationDetails
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel

class MapFragment : Fragment() {
    private val agentViewModel: SharedAgentViewModel by activityViewModels {
        ViewModelFactory((requireActivity().application as MainApplication).agentRepository,
            (requireActivity().application as MainApplication).propertyRepository,
            (requireActivity().application as MainApplication).addressRepository,
            (requireActivity().application as MainApplication).photoRepository,
            requireActivity().application as MainApplication
        )
    }
    private lateinit var fragmentMapBinding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
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

        }
    }

    private fun updateMapWithAgentLocation(location: LocationDetails?) {
        location?.let {
            val newLocation = LatLng(it.latitude, it.longitude)
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(newLocation).title("Marker in your location"))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15f))
        }
    }
}
