package com.openclassrooms.realestatemanager.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Parcelize
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
    val propertyId: Int? = null,
    val apartmentDetails: String? = ""
) : Parcelable {
}