package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.ProximityEntity
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.repository.ProximityRepository

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val addressRepository: AddressRepository,
    private val proximityRepository: ProximityRepository
) : ViewModel() {
    // Get all the properties from the database
    val allProperties: LiveData<List<PropertyEntity>> =
        propertyRepository.allProperties.asLiveData()
    // Get all the addresses from the database
    val allAddresses: LiveData<List<AddressEntity>> =
        addressRepository.allAddress.asLiveData()
    // Get all the proximity from the database
    val allProximity: LiveData<List<ProximityEntity>> =
        proximityRepository.allProximity.asLiveData()
}