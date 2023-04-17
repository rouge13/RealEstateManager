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
    private val ioScope = CoroutineScope(Dispatchers.IO)
    // Get all the propertiesAddress from the database
    val allAddress: Flow<List<AddressEntity>> = addressDao.getAllAddress()

    // Insert Address
    fun insert(address: AddressEntity) {
        ioScope.launch {
            addressDao.insert(address)
        }
    }
}