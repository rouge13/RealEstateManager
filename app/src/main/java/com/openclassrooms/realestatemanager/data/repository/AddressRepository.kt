package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.AddressDao
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddressRepository(private val addressDao: AddressDao) {
    // Get all the propertiesAddress from the database
    val allAddress: Flow<List<AddressEntity>> = addressDao.getAllAddress()
    // Insert Address
    suspend fun insert(address: AddressEntity) {
            addressDao.insert(address)
    }

    // Get Address related to a property
    fun getAddressRelatedToASpecificProperty(propertyId: Int): Flow<AddressEntity?> {
        return addressDao.getAddressRelatedToASpecificProperty(propertyId)
    }

    // Update Address
    suspend fun updateAddress(address: AddressEntity) {
        address.id?.let {
            addressDao.updateAddress(
                it,
                address.apartmentDetails,
                address.streetNumber,
                address.streetName,
                address.city,
                address.boroughs,
                address.zipCode,
                address.country,
                address.latitude,
                address.longitude
            )
        }
    }
}