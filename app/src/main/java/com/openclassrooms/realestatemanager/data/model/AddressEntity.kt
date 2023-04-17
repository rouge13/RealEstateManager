package com.openclassrooms.realestatemanager.data.model

import androidx.room.ColumnInfo
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
            childColumns = ["propertyId"]
        )
    ]
)
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val streetNumber: String = "",
    val streetName: String = "",
    val city: String = "",
    val boroughs: String? = "",
    val zipCode: String = "",
    val country: String = "",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    @ColumnInfo(name = "propertyId", index = true)
    val propertyId: Int? = null
) {
}