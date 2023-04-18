package com.openclassrooms.realestatemanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
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
    val propertyId: Int? = null
) {

}