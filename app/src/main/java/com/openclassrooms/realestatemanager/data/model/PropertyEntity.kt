package com.openclassrooms.realestatemanager.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
// Make the PropertyEntity a Room entity
@Parcelize
@Entity(
    tableName = "property",
    foreignKeys = [
        ForeignKey(
            entity = AgentEntity::class,
            parentColumns = ["id"],
            childColumns = ["agentId"],
        )
    ],
    indices = [Index(value = ["agentId"])]
)
data class PropertyEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var price: Int? = 0,
    var squareFeet: Int? = 0,
    var roomsCount: Int? = 0,
    var bedroomsCount: Int? = 0,
    var bathroomsCount: Int? = 0,
    var description: String? = "Must add description for this property in later time",
    var typeOfHouse: String,
    var isSold: Boolean? = false,
    var dateStartSelling: Long? = null,
    var dateSold: Long? = null,
    var agentId: Int,
    var primaryPhoto: String,
    var schoolProximity: Boolean? = null,
    var parkProximity: Boolean? = null,
    var shoppingProximity: Boolean? = null,
    var restaurantProximity: Boolean? = null,
    var publicTransportProximity: Boolean? = null,
    var lastUpdate: Long = System.currentTimeMillis()
) : Parcelable {

}