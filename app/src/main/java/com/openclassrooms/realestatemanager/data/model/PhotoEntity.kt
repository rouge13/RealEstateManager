package com.openclassrooms.realestatemanager.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Parcelize
@Entity(
    tableName = "photo",
    foreignKeys = [ForeignKey(
        entity = PropertyEntity::class,
        parentColumns = ["id"],
        childColumns = ["propertyId"],
    )]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val photo: String? = null,
    val description: String? = null,
    val propertyId: Int? = null
) : Parcelable {

}