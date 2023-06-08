package com.openclassrooms.realestatemanager.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Parcelize
@Entity(
    tableName = "money"
)
data class ConvertMoneyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    var nameOfMoney: String? = null,
    var changeAmount: Double? = null,
    var activeSelection: Boolean? = null
) : Parcelable {

}