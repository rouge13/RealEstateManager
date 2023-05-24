package com.openclassrooms.realestatemanager.data.repository

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

    suspend fun insert(agent: AgentEntity): Long? {
        val id = agentDao.insert(agent)
        return if (id != -1L) id else null
    }
    // get agent by id
    fun getAgentData(agentId: Int): Flow<AgentEntity> {
        return agentDao.getAgentData(agentId)
    }
    // get agent by name
    fun getAgentByName(agentName: String): Flow<AgentEntity?> {
        return agentDao.getAgentByName(agentName)
    }

}


