package com.openclassrooms.realestatemanager.data.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AgentRepository(private val agentDao: AgentDao) {
    private val ioScope = CoroutineScope(Dispatchers.IO)

    // get all the agents from the database
    val allAgents: Flow<List<AgentEntity>> = agentDao.getAllAgents()
    // insert an agent in the database
    fun insert(agent: AgentEntity) {
        ioScope.launch {
            agentDao.insert(agent)
        }
    }
    // get agent by email and password
    fun agentData(email: String, password: String): Flow<AgentEntity> {
        return agentDao.getAgentDataToConnect(email, password)
    }

}