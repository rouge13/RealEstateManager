package com.openclassrooms.realestatemanager.data.repository

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AgentRepository(private val agentDao: AgentDao) {
    // get all the agents from the database
    val allAgents: Flow<List<AgentEntity>> = agentDao.getAllAgents()
    // insert an agent in the database
    suspend fun insert(agent: AgentEntity) {
        agentDao.insert(agent)
    }
    // get agent by email and password
    fun agentData(email: String, password: String): Flow<AgentEntity> {
        return agentDao.getAgentDataToConnect(email, password)
    }

}