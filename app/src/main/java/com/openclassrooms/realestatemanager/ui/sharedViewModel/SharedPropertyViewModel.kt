package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedPropertyViewModel(private val repository: PropertyRepository): ViewModel() {
    // Get all the properties from the database and display them in the recyclerview
    val allProperties: LiveData<List<PropertyEntity>> = repository.allProperties.asLiveData()
}