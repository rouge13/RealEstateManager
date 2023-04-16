package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Entity(
    tableName = "address", foreignKeys = [
        ForeignKey(
            entity = PropertyEntity::class,
            parentColumns = ["id"],
            childColumns = ["property_id"]
        )
    ]
)
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val streetNumber: String,
    val streetName: String,
    val city: String,
    val boroughs: String? = "",
    val zipCode: String,
    val country: String,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0
) {
    // Make property_id equal to the id of the address automatically
    val propertyId: Int?
        get() = id
}