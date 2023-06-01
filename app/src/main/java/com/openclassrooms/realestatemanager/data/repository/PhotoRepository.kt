package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PhotoRepository(private val photoDao: PhotoDao) {
    // Get all the propertiesPhoto from the database
    val allPhoto: Flow<List<PhotoEntity>> = photoDao.getAllPhotos()
    // Insert Photo
    suspend fun insert(photo: PhotoEntity): Long? {
           return photoDao.insert(photo)
    }

    // Get all photos related to a property
    fun getAllPhotosRelatedToASpecificProperty(propertyId: Int): Flow<List<PhotoEntity>>? {
        return photoDao.getAllPhotosRelatedToASpecificProperty(propertyId)
    }

    // Update photo with the propertyId
    suspend fun updatePhotoWithPropertyId(photoId: Int, propertyId: Int) {
        photoDao.updatePhotoWithPropertyId(photoId = photoId, propertyId = propertyId)
    }

}
