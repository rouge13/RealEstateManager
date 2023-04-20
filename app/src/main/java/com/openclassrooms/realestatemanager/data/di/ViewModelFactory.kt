package com.openclassrooms.realestatemanager.data.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class ViewModelFactory(
    private val agentRepository: AgentRepository,
    private val propertyRepository: PropertyRepository,
    private val addressRepository: AddressRepository,
    private val photoRepositoy: PhotoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedAgentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedAgentViewModel(agentRepository) as T
        } else if (modelClass.isAssignableFrom(SharedPropertyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedPropertyViewModel(
                propertyRepository,
                addressRepository,
                photoRepositoy
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}