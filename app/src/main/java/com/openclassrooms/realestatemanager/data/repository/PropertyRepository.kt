package com.openclassrooms.realestatemanager.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.openclassrooms.realestatemanager.data.dao.AddressDao
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyRepository(private val propertyDao: PropertyDao) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    // Get all the properties from the database
    val allProperties: Flow<List<PropertyEntity>> = propertyDao.getAllProperties()

    fun insert(property: PropertyEntity) {
        ioScope.launch {
            propertyDao.insert(property)
        }
    }
}