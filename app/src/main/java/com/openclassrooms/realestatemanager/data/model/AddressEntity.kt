package com.openclassrooms.realestatemanager.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    ],
    indices = [Index(value = ["propertyId"])]
)
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var streetNumber: String = "",
    var streetName: String = "",
    var city: String = "",
    var boroughs: String? = "",
    var zipCode: String = "",
    var country: String = "",
    val propertyId: Int? = null,
    var apartmentDetails: String? = ""
) : Parcelable {
}