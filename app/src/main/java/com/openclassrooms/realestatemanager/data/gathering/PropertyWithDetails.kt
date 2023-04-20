package com.openclassrooms.realestatemanager.data.gathering

import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
data class PropertyWithDetails(
    val property: PropertyEntity,
    val address: AddressEntity,
    val photo: PhotoEntity
)