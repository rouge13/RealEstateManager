package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.ProximityEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface ProximityDao {
    // Get all Proximities
    @Query("SELECT * FROM proximity ORDER BY id ASC")
    fun getAllProximities(): Flow<List<ProximityEntity>>
    // Insert Proximity
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(proximity: ProximityEntity)
    // Get Address related to a property
    @Query("SELECT * FROM proximity WHERE id = :propertyId")
    fun getProximityRelatedToASpecificProperty(propertyId: Int): ProximityEntity?
}