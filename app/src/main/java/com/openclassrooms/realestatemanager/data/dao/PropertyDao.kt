package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface PropertyDao {
    // Get all properties
    @Query("SELECT * FROM property ORDER BY id ASC")
    fun getAllProperties(): Flow<List<PropertyEntity>>
//    // Get property by id
//    @Query("SELECT * FROM property WHERE id = :id")
//    fun getPropertyById(id: Long): PropertyEntity
//
//    // Insert property
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertProperty(property: PropertyEntity)
//
//    // Update property
//    fun updateProperty(property: PropertyEntity)



}