package com.openclassrooms.realestatemanager.data.dao

import androidx.annotation.NonNull
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface PhotoDao {
    // Get all photos
    @Query("SELECT * FROM photo ORDER BY id ASC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    // Get all photos related to a property
    @Query("SELECT * FROM photo WHERE propertyId = :propertyId")
    fun getAllPhotosRelatedToASpecificProperty(propertyId: Int): Flow<List<PhotoEntity>>?

    // Insert photo
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photo: PhotoEntity): Long

    // Update photo with the propertyId
    @Query(
        """
        UPDATE photo 
        SET propertyId = :propertyId 
        WHERE id = :photoId
        """
    )
    suspend fun updatePhotoWithPropertyId(
        propertyId: Int,
        photoId: Int
    )

    // Delete photo
    @Query("DELETE FROM photo WHERE id = :photoId")
    suspend fun deletePhoto(photoId: Int)

}