package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.ui.LocationLiveData
import com.openclassrooms.realestatemanager.ui.MainApplication
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedAgentViewModel(private val repository: AgentRepository, application: MainApplication) : AndroidViewModel(
    application) {
    // Init location
    private val locationLiveData = application.applicationContext.let { LocationLiveData(it) }
    // Get agent location
    fun getLocationLiveData() = locationLiveData
    // Start location update
    fun startLocationUpdates() {
        locationLiveData.startLocationUpdates()
    }
    // Get filtered agent by email in MutalbeLiveData
    val _agentByEmailFiltered = MutableLiveData<AgentEntity?>()

    // Get filtered agent by email live data
    fun getAgentByEmailFiltered(email: String): LiveData<AgentEntity?> {
        return _agentByEmailFiltered
    }

    val allAgents: LiveData<List<AgentEntity>> = repository.allAgents.asLiveData()
    // Function to get agent by email and password
    fun getAgentData(agentId: Int): LiveData<AgentEntity> {
        return repository.getAgentData(agentId).asLiveData()
    }
    // Insert agent
    suspend fun insertAgent(agent: AgentEntity) {
        repository.insert(agent)
    }

}