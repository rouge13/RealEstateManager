package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface AgentDao {
    // Get all Agents
    @Query("SELECT * FROM agent ORDER BY id ASC")
    fun getAllAgents(): Flow<List<AgentEntity>>
    // Insert agent
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(agent: AgentEntity)
    // Get agent data to connect
    @Query("SELECT * FROM agent WHERE id = :agentId")
    fun getAgentData(agentId: Int): Flow<AgentEntity>


}