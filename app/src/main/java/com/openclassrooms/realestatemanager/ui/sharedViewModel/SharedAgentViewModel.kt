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


//    // Function to get agent by email
//    suspend fun agentByEmailFiltered(email: String) {
//        viewModelScope.launch {
//            _agentByEmailFiltered.postValue(repository.agentByEmailFiltered(email).asLiveData().value)
//        }
//    }

    val allAgents: LiveData<List<AgentEntity>> = repository.allAgents.asLiveData()
    // Mutable LiveData to get the agent by email and password
    private var _loggedAgent = MutableLiveData<AgentEntity?>()
    val loggedAgent: LiveData<AgentEntity?> get() = _loggedAgent
    // Function to get agent by email and password
    fun agentData(email: String, password: String): LiveData<AgentEntity> {
        return repository.agentData(email, password).asLiveData()
    }
    fun setLogedAgent(agent: AgentEntity?) {
        viewModelScope.launch {
            if (agent != null) {
                _loggedAgent.postValue(agent)

            } else {
                _loggedAgent.postValue(null)
            }
        }
    }
    // Insert agent
    suspend fun insertAgent(agent: AgentEntity) {
        repository.insert(agent)
    }
    // Check if the agent exist by email
    suspend fun getAgentByEmail(email: String): AgentEntity? {
        return repository.getAgentByEmail(email)
    }
}