package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import kotlinx.coroutines.flow.firstOrNull
import android.util.Log


/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    // Get selected property
    private val _selectedProperty = MutableLiveData<PropertyWithDetails>()
    val getSelectedProperty: LiveData<PropertyWithDetails> get() = _selectedProperty

    fun selectProperty(property: PropertyWithDetails) {
        _selectedProperty.value = property
    }

    // Combine the data in a single LiveData object
    val propertiesWithDetails: LiveData<List<PropertyWithDetails>> = propertyRepository.allProperties.asLiveData().switchMap { properties ->
        liveData {
            val combinedData = combinePropertiesWithDetails(properties)
            emit(combinedData)
        }
    }

    private suspend fun combinePropertiesWithDetails(
        properties: List<PropertyEntity>?
    ): List<PropertyWithDetails> {
        if (properties == null) {
            return emptyList()
        }

        val combinedData = properties.mapNotNull { property ->
            val propertyId = property.id
            if (propertyId != null) {
                val propertyAddress = addressRepository.getAddressRelatedToASpecificProperty(propertyId).firstOrNull()
                val propertyPhotos = photoRepository.getAllPhotosRelatedToASpecificProperty(propertyId)?.firstOrNull()
                Log.d("ViewModel", "Property ID: $propertyId, Address: $propertyAddress")
                PropertyWithDetails(property, propertyAddress, propertyPhotos)
            } else {
                null
            }
        }
        return combinedData
    }

}





