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
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(property: PropertyEntity)


}