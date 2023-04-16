package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface AddressDao {
    // Get all address
    @Query("SELECT * FROM address ORDER BY id ASC")
    fun getAllAddress(): Flow<List<AddressEntity>>
    // Insert Address
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(address: AddressEntity)
    // Get Address related to a property
    @Query("SELECT * FROM address WHERE id = :propertyId")
    fun getAddressRelatedToASpecificProperty(propertyId: Int): AddressEntity?
}