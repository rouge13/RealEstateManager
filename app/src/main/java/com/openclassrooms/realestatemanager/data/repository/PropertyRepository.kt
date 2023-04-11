package com.openclassrooms.realestatemanager.data.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyRepository(private val propertyDao: PropertyDao) {
    val allProperties: Flow<List<PropertyEntity>> = propertyDao.getAllProperties()

    suspend fun insert(property: PropertyEntity) {
        propertyDao.insert(property)
    }
}