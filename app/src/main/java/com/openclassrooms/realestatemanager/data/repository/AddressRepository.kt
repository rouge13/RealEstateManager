package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.AddressDao
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import kotlinx.coroutines.flow.Flow

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
}