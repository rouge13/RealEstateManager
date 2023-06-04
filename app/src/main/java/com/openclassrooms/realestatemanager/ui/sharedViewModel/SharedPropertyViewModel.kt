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
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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
                    // Combine the newly inserted property with details
                    val propertyAddress =
                        insertedProperty.id?.let {
                            addressRepository.getAddressRelatedToASpecificProperty(it)
                                .firstOrNull()
                        }
                    val propertyPhotos =
                        insertedProperty.id?.let {
                            photoRepository.getAllPhotosRelatedToASpecificProperty(it)
                                ?.firstOrNull()
                        }
                    val propertyWithDetails =
                        PropertyWithDetails(insertedProperty, propertyAddress, propertyPhotos)

                    // Add the combined property with details to the list
                    val currentPropertiesWithDetails = propertiesWithDetailsMediator.value.orEmpty().toMutableList()
                    currentPropertiesWithDetails.add(propertyWithDetails)
                    propertiesWithDetailsMediator.value = currentPropertiesWithDetails
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
                val propertyAddress =
                    addressRepository.getAddressRelatedToASpecificProperty(propertyId)
                        .firstOrNull()
                val propertyPhotos =
                    photoRepository.getAllPhotosRelatedToASpecificProperty(propertyId)
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
    suspend fun updateProperty(property: PropertyEntity, photos: List<PhotoEntity>) {
        withContext(Dispatchers.IO) {
            propertyRepository.update(property)
            val updatedProperty = property.id?.let { propertyRepository.getPropertyById(it.toLong()) }
            updatedProperty?.let {
                val propertyAddress = updatedProperty.id?.let { it1 ->
                    addressRepository.getAddressRelatedToASpecificProperty(it1)
                        .firstOrNull()
                }
                val propertyWithDetails = PropertyWithDetails(updatedProperty, propertyAddress, photos)

                // Update the property with details in the list
                val currentPropertiesWithDetails = propertiesWithDetailsMediator.value.orEmpty().toMutableList()
                val updatedIndex = currentPropertiesWithDetails.indexOfFirst { propertyWithDetails ->
                    propertyWithDetails.property.id == updatedProperty.id
                }
                if (updatedIndex != -1) {
                    currentPropertiesWithDetails[updatedIndex] = propertyWithDetails
                    propertiesWithDetailsMediator.postValue(currentPropertiesWithDetails)
                }
            }
        }
    }


    // Update the address of the property
    suspend fun updateAddress(address: AddressEntity) {
        addressRepository.updateAddress(address)
    }

    // Update the address of the property with location
    suspend fun updateAddressWithLocation(address: AddressEntity, latitude: Double?, longitude: Double?) {
        val updatedAddress = address.copy(latitude = latitude, longitude = longitude)
        addressRepository.updateAddress(updatedAddress)
    }

    // Insert property
    suspend fun insertProperty(property: PropertyEntity): Long? {
        return propertyRepository.insert(property)
    }

    // Insert address of the property
    suspend fun insertAddress(address: AddressEntity) {
        addressRepository.insert(address)
    }

    // Insert photo of the property
    suspend fun insertPhoto(photo: PhotoEntity): Long? {
        val insertedPhotoId = photoRepository.insert(photo)
        insertedPhotoId?.let { photoId ->
            val currentPendingPhotoIds = pendingPhotoIds.value.orEmpty().toMutableList()
            currentPendingPhotoIds.add(photoId.toInt())
            pendingPhotoIds.value = currentPendingPhotoIds
        }
        return insertedPhotoId
    }


    // Keep the photoId in a pending MutableListOf to be able to insert it in the database when the property is inserted
    val pendingPhotoIds = MutableLiveData<List<Int>>()

    // Get all the pending photoIds
    val getPendingPhotoIds: LiveData<List<Int>> get() = pendingPhotoIds

    // Add photoId to the pending list
    fun addPendingPhotoId(photoId: Int) {
        val currentPendingPhotoIds = pendingPhotoIds.value.orEmpty().toMutableList()
        currentPendingPhotoIds.add(photoId)
        pendingPhotoIds.value = currentPendingPhotoIds
    }


    // Update all photos with the propertyId
    suspend fun updatePhotosWithPropertyId(photoId: Int, propertyId: Int) {
        photoRepository.updatePhotoWithPropertyId(photoId, propertyId)
    }

    // Delete photo
    suspend fun deletePhoto(photoId: Int) {
        val currentPendingPhotoIds = pendingPhotoIds.value.orEmpty().toMutableList()
        currentPendingPhotoIds.remove(photoId)
        pendingPhotoIds.value = currentPendingPhotoIds

        // Update the photos of the _selectedProperty by excluding the deleted photo
        _selectedProperty.value?.let { propertyWithDetails ->
            val updatedPhotos = propertyWithDetails.photos?.filter { it.id != photoId }
            val updatedPropertyWithDetails = propertyWithDetails.copy(photos = updatedPhotos)
            _selectedProperty.value = updatedPropertyWithDetails
        }

        // Update the photos of the propertiesWithDetailsMediator list by excluding the deleted photo
        val updatedList = propertiesWithDetailsMediator.value.orEmpty().toMutableList()
        val propertyWithDeletedPhoto = updatedList.find { it -> it.photos?.any { it.id == photoId } == true }
        propertyWithDeletedPhoto?.let { propertyDetails ->
            val updatedPhotos = propertyDetails.photos?.filter { it.id != photoId }
            val updatedPropertyWithDetails = propertyDetails.copy(photos = updatedPhotos)
            val index = updatedList.indexOf(propertyDetails)
            if (index != -1) {
                updatedList[index] = updatedPropertyWithDetails
                propertiesWithDetailsMediator.value = updatedList
            }
        }

        // Delete the photo from the database
        photoRepository.deletePhoto(photoId)
    }

}






