package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
// Make the AgentEntity a Room entity
@Entity(tableName = "agent")
data class AgentEntity(@PrimaryKey(autoGenerate = true)
                       val id: Int = 0,
                       val firstName: String,
                       val lastName: String,
                       val email: String,
                       val password: String) {
    constructor() : this(0, "", "", "", "")

}