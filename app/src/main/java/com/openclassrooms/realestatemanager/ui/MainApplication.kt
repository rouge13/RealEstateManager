package com.openclassrooms.realestatemanager.ui

import android.app.Application
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainApplication : Application() {
    // Create instance of the database to iniatialize it
    private val database: PropertyDatabase by lazy {
        PropertyDatabase.getDatabase(this)
    }

    // Initialize the propertyRepository
    val propertyRepository by lazy {
        PropertyRepository(database.propertyDao())
    }
}