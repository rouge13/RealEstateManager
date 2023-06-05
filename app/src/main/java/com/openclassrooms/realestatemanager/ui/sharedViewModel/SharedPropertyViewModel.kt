package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine


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

    val getPropertiesWithDetails: Flow<List<PropertyWithDetails>> = combine(
        propertyRepository.getAllProperties,
        addressRepository.getAllAddresses,
        photoRepository.getAllPhotos
    ) { properties, addresses, photos ->
        combinePropertiesWithDetails(properties, addresses, photos)
    }

    private suspend fun combinePropertiesWithDetails(
        properties: List<PropertyEntity>,
        addresses: List<AddressEntity>,
        photos: List<PhotoEntity>
    ): List<PropertyWithDetails> {
        val combinedData = mutableListOf<PropertyWithDetails>()
        for (property in properties) {
            val propertyAddress = addresses.find { it.propertyId == property.id }
            val propertyPhotos = photos.filter { it.propertyId == property.id }
            val propertyWithDetails = PropertyWithDetails(property, propertyAddress, propertyPhotos)
            combinedData.add(propertyWithDetails)
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

    // Update the address of the property with location
    suspend fun updateAddressWithLocation(
        address: AddressEntity,
        latitude: Double?,
        longitude: Double?
    ) {
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
        return photoRepository.insert(photo)
    }

    // Update all photos with the propertyId
    suspend fun updatePhotosWithPropertyId(photoId: Int, propertyId: Int) {
        photoRepository.updatePhotoWithPropertyId(photoId, propertyId)
    }

    // Delete photo
    suspend fun deletePhoto(photoId: Int) {
        // Delete the photo from the database
        photoRepository.deletePhoto(photoId)
    }

    suspend fun getAllPhotosRelatedToSetThePropertyId(): Flow<List<PhotoEntity>>? {
        return photoRepository.getAllPhotosRelatedToASpecificProperty()
    }

    suspend fun deletePhotosWithNullPropertyId() {
        photoRepository.deletePhotosWithNullPropertyId()
    }
}






