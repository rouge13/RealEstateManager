package com.openclassrooms.realestatemanager.property_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListViewModel(private val repository: PropertyRepository): ViewModel() {
    // Get all the properties from the database and display them in the recyclerview
    var allProperties: LiveData<List<PropertyEntity>> = repository.allProperties.asLiveData()
}