package com.openclassrooms.realestatemanager.data.dao

import androidx.lifecycle.LiveData
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
    suspend fun insert(address: AddressEntity)
    // Get Address related to a property
    @Query("SELECT * FROM address WHERE propertyId = :propertyId")
    fun getAddressRelatedToASpecificProperty(propertyId: Int): Flow<AddressEntity?>

    @Query(
        """
    UPDATE address
    SET apartmentDetails = :apartmentDetails,
        streetNumber = :streetNumber,
        streetName = :streetName,
        city = :city,
        boroughs = :boroughs,
        zipCode = :zipCode,
        country = :country,
        latitude = :latitude,
        longitude = :longitude
    WHERE id = :addressId
    """
    )
    suspend fun updateAddress(
        addressId: Int,
        apartmentDetails: String?,
        streetNumber: String?,
        streetName: String?,
        city: String?,
        boroughs: String?,
        zipCode: String?,
        country: String?,
        latitude: Double?,
        longitude: Double?
    )
}