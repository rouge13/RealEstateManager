package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.ui.utils.Utils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull


/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedPropertyViewModel(
    private val propertyRepository: PropertyRepository,
    private val addressRepository: AddressRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {
    // Get and Set selected property
    private val _selectedProperty = MutableLiveData<PropertyWithDetails?>()
    val getSelectedProperty: MutableLiveData<PropertyWithDetails?> get() = _selectedProperty
    fun setSelectProperty(property: PropertyWithDetails? = null) {
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
        searchCriteria.asFlow(),
        propertyRepository.getAllProperties,
        addressRepository.getAllAddresses,
        photoRepository.getAllPhotos
    ) { criteria, properties, addresses, photos ->
        if (criteria != null) {
            val filteredProperties =
                propertyRepository.getFilteredProperties(criteria).firstOrNull()
            combinePropertiesWithDetails(filteredProperties, addresses, photos)
        } else {
            combinePropertiesWithDetails(properties, addresses, photos)
        }
    }

    private fun combinePropertiesWithDetails(
        properties: List<PropertyEntity>?,
        addresses: List<AddressEntity>,
        photos: List<PhotoEntity>
    ): List<PropertyWithDetails> {
        if (properties == null) {
            return emptyList()
        }
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

    // Update is Primary photo with the photoId
    suspend fun updateIsPrimaryPhoto(isPrimary: Boolean, photoId: Int) {
        photoRepository.updateIsPrimaryPhoto(isPrimary = isPrimary, photoId = photoId)
    }

    // Delete photo
    suspend fun deletePhoto(photoId: Int) {
        // Delete the photo from the database
        photoRepository.deletePhoto(photoId)
    }

    suspend fun getAllPhotosRelatedToSetThePropertyId(propertyId: Int? = null): List<PhotoEntity>? {
        return photoRepository.getAllPhotosRelatedToASpecificProperty(propertyId)
    }

    suspend fun deletePhotosWithNullPropertyId() {
        photoRepository.deletePhotosWithNullPropertyId()
    }

    // Update the primary photo of the property
    suspend fun updatePrimaryPhoto(propertyId: Int?, photoUri: String) {
        propertyRepository.updatePrimaryPhoto(propertyId, photoUri)
    }

    // Convert property price
    fun convertPropertyPrice(price: Int, isEuroSelected: Boolean? = false): Int? {
        val convertedPrice = if (isEuroSelected == true) {
            convertDollarsToEuros(price)
        } else {
            convertEurosToDollars(price)
        }
        return convertedPrice
    }

    // Convert to Euros
    private fun convertDollarsToEuros(dollars: Int): Int {
        // Perform the conversion logic here
        return Utils.convertDollarsToEuros(dollars)
    }

    // Convert to Dollars
    private fun convertEurosToDollars(euros: Int): Int {
        // Perform the conversion logic here
        return Utils.convertEurosToDollars(euros)
    }
}






