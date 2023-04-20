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
    @Query("SELECT * FROM agent WHERE email = :email AND password = :password")
    fun getAgentDataToConnect(email: String, password: String): Flow<AgentEntity>
    // See if the email is already used
    @Query("SELECT * FROM agent WHERE email = :email")
    suspend fun getAgentByEmail(email: String): AgentEntity?
}