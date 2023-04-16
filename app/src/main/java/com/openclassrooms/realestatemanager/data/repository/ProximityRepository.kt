package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.ProximityDao
import com.openclassrooms.realestatemanager.data.model.ProximityEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class ProximityRepository(private val proximityDao: ProximityDao) {
    // Get all the propertiesProximity from the database
    val allProximity: Flow<List<ProximityEntity>> = proximityDao.getAllProximities()

    // Insert Proximity
    suspend fun insert(proximity: ProximityEntity){
        proximityDao.insert(proximity)
    }
}