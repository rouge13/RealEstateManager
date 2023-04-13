package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.repository.AgentRepository

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedAgentViewModel(private val repository: AgentRepository) : ViewModel(){
    val allAgents: LiveData<List<AgentEntity>> = repository.allAgents.asLiveData()
    // Mutable livedata to get the agent by email and password
    private var logedAgentMutableLiveData = MutableLiveData<AgentEntity?>()
    val logedAgent: LiveData<AgentEntity?> get() =  logedAgentMutableLiveData
    // function to get agent by email and password
    fun agentData(email: String, password: String): LiveData<AgentEntity>{
        return repository.agentData(email, password).asLiveData()
    }
    fun setLogedAgent(agent: AgentEntity?){
        if (agent != null){
            logedAgentMutableLiveData.postValue(agent)
        } else {
            logedAgentMutableLiveData.postValue(null)
        }
    }

    suspend fun insertAgent(agent: AgentEntity){
        repository.insert(agent)
    }
}