package com.openclassrooms.realestatemanager.ui.property_list

import androidx.lifecycle.*
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class PropertyListViewModel(private val repository: PropertyRepository): ViewModel() {
    // Get all the properties from the database and display them in the recyclerview
    val allProperties: LiveData<List<PropertyEntity>> = repository.allProperties.asLiveData()

}

class PropertyListModelFactory(private val repository: PropertyRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PropertyListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PropertyListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}