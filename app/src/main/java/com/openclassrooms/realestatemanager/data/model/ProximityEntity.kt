package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Entity(
    tableName = "proximity",
    foreignKeys = [ForeignKey(
        entity = PropertyEntity::class,
        parentColumns = ["id"],
        childColumns = ["propertyId"],
    )]
)
data class ProximityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val schoolProximity: Boolean? = false,
    val parkProximity: Boolean? = false,
    val shoppingProximity: Boolean? = false,
    val restaurantProximity: Boolean? = false,
    val publicTransportProximity: Boolean? = false,
    @ColumnInfo(name = "propertyId", index = true)
    val propertyId: Int? = null
) {
}