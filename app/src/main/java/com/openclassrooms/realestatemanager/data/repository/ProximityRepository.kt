package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.ProximityDao
import com.openclassrooms.realestatemanager.data.model.ProximityEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class ProximityRepository(private val proximityDao: ProximityDao) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    // Get all the propertiesProximity from the database
    val allProximity: Flow<List<ProximityEntity>> = proximityDao.getAllProximities()

    // Insert Proximity
    fun insert(proximity: ProximityEntity){
        ioScope.launch {
            proximityDao.insert(proximity)
        }
    }
}