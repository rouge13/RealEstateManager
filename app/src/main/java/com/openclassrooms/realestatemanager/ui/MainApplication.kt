package com.openclassrooms.realestatemanager.ui

import android.app.Application
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository

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

    // Initialize the agentRepository
    val agentRepository by lazy {
        AgentRepository(database.agentDao())
    }

    // Initialize the addressRepository
    val addressRepository by lazy {
        AddressRepository(database.addressDao())
    }

    // Initialize the photoRepository
    val photoRepository by lazy {
        PhotoRepository(database.photoDao())
    }
}