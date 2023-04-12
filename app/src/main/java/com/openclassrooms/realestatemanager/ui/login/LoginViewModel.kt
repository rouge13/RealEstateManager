package com.openclassrooms.realestatemanager.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.ui.property_list.PropertyListViewModel

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class LoginViewModel(private val repository: AgentRepository) : ViewModel(){
    // Mutable livedata to get the agent by email and password
    private var logedAgentMutableLiveData = MutableLiveData<AgentEntity>()
    val logedAgent: LiveData<AgentEntity> = logedAgentMutableLiveData
    // function to get agent by email and password
    fun agentData(email: String, password: String): LiveData<AgentEntity>{
        return repository.agentData(email, password).asLiveData()
    }

    fun setLogedAgent(agent: AgentEntity){
        logedAgentMutableLiveData.value = agent
    }
}

class LoginViewModelFactory(private val repository: AgentRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}