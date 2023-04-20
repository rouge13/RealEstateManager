package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val addressRepository: AddressRepository,
    private val photoRepositoy: PhotoRepository
) : ViewModel() {
    // Get selected property
    private val _selectedProperty = MutableLiveData<PropertyWithDetails>()
    val selectedProperty: LiveData<PropertyWithDetails> get() = _selectedProperty
    fun selectProperty(property: PropertyWithDetails) {
        _selectedProperty.value = property
    }
    // Get all the properties from the database
    val allProperties: LiveData<List<PropertyEntity>> = propertyRepository.allProperties.asLiveData()
    // Get all the addresses from the database
    val allAddresses: LiveData<List<AddressEntity>> = addressRepository.allAddress.asLiveData()
    // Get all the photos from the database
    val allPhotos: LiveData<List<PhotoEntity>> = photoRepositoy.allPhoto.asLiveData()
    // Create a LiveData for the combined data
    val propertiesWithDetails: MediatorLiveData<List<PropertyWithDetails>> = MediatorLiveData<List<PropertyWithDetails>>().apply {
        fun updateCombinedData() {
            viewModelScope.launch {
                val combinedData = combinePropertiesWithDetails(allProperties.value, allAddresses.value, allPhotos.value)
                postValue(combinedData)
            }
        }
        addSource(allProperties) { _ ->
            updateCombinedData()
        }
        addSource(allAddresses) { _ ->
            updateCombinedData()
        }
        addSource(allPhotos) { _ ->
            updateCombinedData()
        }
    }

    private suspend fun combinePropertiesWithDetails(
        properties: List<PropertyEntity>?,
        addresses: List<AddressEntity>?,
        photo: List<PhotoEntity>?
    ): List<PropertyWithDetails>? {
        if (properties == null || addresses == null || photo == null) {
            return null
        }

        val combinedData = properties.map { property ->
            val address = addresses.find { it.propertyId == property.id }
            val photoItem = photo.find { it.propertyId == property.id }
            PropertyWithDetails(property, address ?: AddressEntity(),  photoItem ?: PhotoEntity())
        }
        return combinedData
    }


}