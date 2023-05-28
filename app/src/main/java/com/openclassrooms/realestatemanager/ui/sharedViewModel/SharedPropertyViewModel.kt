package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import kotlinx.coroutines.flow.firstOrNull
import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import kotlinx.coroutines.launch


/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    // Get and Set selected property
    private val _selectedProperty = MutableLiveData<PropertyWithDetails>()
    val getSelectedProperty: LiveData<PropertyWithDetails> get() = _selectedProperty
    fun setSelectProperty(property: PropertyWithDetails) {
        _selectedProperty.value = property
    }
    // Search criteria and null by default
    private val _searchCriteria = MutableLiveData<SearchCriteria?>(null)
    val searchCriteria: LiveData<SearchCriteria?> get() = _searchCriteria
    fun setSearchCriteria(criteria: SearchCriteria?) {
        _previousSearchCriteria.value = _searchCriteria.value
        _searchCriteria.value = criteria
    }

    private val _previousSearchCriteria = MutableLiveData<SearchCriteria?>(null)
    val previousSearchCriteria: LiveData<SearchCriteria?> get() = _previousSearchCriteria


    private val propertiesWithDetailsMediator = MediatorLiveData<List<PropertyWithDetails>>()

    init {
        propertiesWithDetailsMediator.addSource(_searchCriteria) { criteria ->
            viewModelScope.launch {
                val properties = if (criteria != null) {
                    propertyRepository.getFilteredProperties(criteria).firstOrNull()
                } else {
                    propertyRepository.getAllProperties.firstOrNull()
                }
                properties?.let {
                    val propertyDetails = combinePropertiesWithDetails(it)
                    propertiesWithDetailsMediator.postValue(propertyDetails)
                }
            }
        }
        // Observe insertedProperty LiveData
        propertyRepository.insertedProperty.observeForever { insertedProperty ->
            viewModelScope.launch {
                if (insertedProperty != null) {
                    // Initialize all properties
                    val properties = propertyRepository.getAllProperties.firstOrNull()
                    properties?.let {
                        val propertyDetails = combinePropertiesWithDetails(it)
                        propertiesWithDetailsMediator.value = propertyDetails
                    }
                }
            }
        }

    }

    val getPropertiesWithDetails: LiveData<List<PropertyWithDetails>> = propertiesWithDetailsMediator

    private suspend fun combinePropertiesWithDetails(
        properties: List<PropertyEntity>?
    ): List<PropertyWithDetails> {
        if (properties == null) {
            return emptyList()
        }
        val combinedData = properties.mapNotNull { property ->
            val propertyId = property.id
            if (propertyId != null) {
                val propertyAddress = addressRepository.getAddressRelatedToASpecificProperty(propertyId)
                    .firstOrNull()
                val propertyPhotos = photoRepository.getAllPhotosRelatedToASpecificProperty(propertyId)
                    ?.firstOrNull()
                Log.d("ViewModel", "Property ID: $propertyId, Address: $propertyAddress")
                PropertyWithDetails(property, propertyAddress, propertyPhotos)
            } else {
                null
            }
        }
        return combinedData
    }

    // Update property
    suspend fun updateProperty(property: PropertyEntity) {
        propertyRepository.update(property)
    }

    // Update the address of the property
    suspend fun updateAddress(address: AddressEntity) {
        addressRepository.updateAddress(address)
    }

    // Insert property
    suspend fun insertProperty(property: PropertyEntity): Long? {
        return propertyRepository.insert(property)
    }

    // Insert address of the property
    suspend fun insertAddress(address: AddressEntity) {
        addressRepository.insert(address)
    }

}






