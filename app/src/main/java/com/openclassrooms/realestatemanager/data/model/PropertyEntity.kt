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
    ]
)
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var price: Int? = 0,
    var squareFeet: Int? = 0,
    var roomsCount: Int? = 0,
    var bedroomsCount: Int? = 0,
    var bathroomsCount: Int? = 0,
    var description: String? = "Must add description for this property in later time",
    // Photo and video will be in another entity
    // Proximity in another entity
    var typeOfHouse: String,
    var isSold: Boolean? = false,
    var dateStartSelling: String? = SimpleDateFormat(
        "yyyy/MM/dd",
        Locale.US
    ).format(Calendar.getInstance().time),
    var dateSold: String,
    var agentId: Int,
    var primaryPhoto: String,
    var lastUpdate: Long = System.currentTimeMillis()
) {

}