package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.ProximityEntity
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.repository.ProximityRepository
import kotlinx.coroutines.launch

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

    // Create a LiveData for the combined data
    val propertiesWithDetails: MediatorLiveData<List<PropertyWithDetails>> = MediatorLiveData<List<PropertyWithDetails>>().apply {
        fun updateCombinedData() {
            viewModelScope.launch {
                val combinedData = combinePropertiesWithDetails(allProperties.value, allAddresses.value, allProximity.value)
                postValue(combinedData)
            }
        }

        addSource(allProperties) { _ ->
            updateCombinedData()
        }
        addSource(allAddresses) { _ ->
            updateCombinedData()
        }
        addSource(allProximity) { _ ->
            updateCombinedData()
        }
    }

    private suspend fun combinePropertiesWithDetails(
        properties: List<PropertyEntity>?,
        addresses: List<AddressEntity>?,
        proximity: List<ProximityEntity>?
    ): List<PropertyWithDetails>? {
        if (properties == null || addresses == null || proximity == null) {
            return null
        }

        val combinedData = properties.map { property ->
            val address = addresses.find { it.propertyId == property.id }
            val proximityItem = proximity.find { it.propertyId == property.id }
            PropertyWithDetails(property, address ?: AddressEntity(), proximityItem ?: ProximityEntity())
        }
        return combinedData
    }
}