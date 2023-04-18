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
class PhotoRepositoy(private val photoDao: PhotoDao) {
    private val ioScope = CoroutineScope(Dispatchers.IO)
    // Get all the propertiesPhoto from the database
    val allPhoto: Flow<List<PhotoEntity>> = photoDao.getAllPhotos()

    // Insert Photo
    fun insert(photo: PhotoEntity) {
        ioScope.launch {
            photoDao.insert(photo)
        }
    }
}
