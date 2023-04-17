package com.openclassrooms.realestatemanager.data.gathering

import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.ProximityEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
data class PropertyWithDetails ( val property: PropertyEntity,
                            val address: AddressEntity,
                            val proximity: ProximityEntity)