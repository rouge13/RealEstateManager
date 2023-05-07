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
    // Check if the email is already used
    suspend fun getAgentByEmail(email: String): AgentEntity? {
        return agentDao.getAgentByEmail(email)
    }

    suspend fun agentByEmailFiltered(email: String): Flow<AgentEntity> {
        return agentDao.agentByEmailFiltered(email)
    }

}