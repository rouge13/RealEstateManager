package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
// Make the PropertyEntity a Room entity
@Entity(
    tableName = "property",
    foreignKeys = [
       ForeignKey(
            entity = AgentEntity::class,
            parentColumns = ["id"],
            childColumns = ["agentId"],
        )
    ],
)
data class PropertyEntity(@PrimaryKey(autoGenerate = true)
                          val id: Int,
                          var price: Int,
                          var squareFeet: Int? = 0,
                          var rooms: Int? = 0,
                          var bedrooms: Int? = 0,
                          var bathrooms: Int? = 0,
                          var description: String,
                          var address: String,
                          var boroughs: String,
                          var typeOfHouse: String,
                          var isSold: Boolean,
                          var dateStartSelling: String,
                          var dateSold: String,
                          var agentId: Int,
                          var primaryPhoto: String,
                          var lastUpdate: Long = System.currentTimeMillis()
)

{
    constructor() : this(0, 0, 0, 0, 0, 0, "", "", "", "", false, SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Calendar.getInstance().time), "", 0, "",0)
}