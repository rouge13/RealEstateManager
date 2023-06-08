package com.openclassrooms.realestatemanager.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.model.ConvertMoneyEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Dao
interface ConvertMoneyDao {

    // Get all moneyRates
    @Query("SELECT * FROM money ORDER BY id ASC")
    fun getAllMoneyRates(): Flow<List<ConvertMoneyEntity>>

    // Insert moneyRates
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(convertMoney: ConvertMoneyEntity)

    // Update moneyRates
    @Query(
        """
        UPDATE money 
        SET changeAmount = :changeAmount, activeSelection = :activeSelection
        WHERE nameOfMoney = :nameOfMoney
        """
    )
    suspend fun updateMoneyRates(
        changeAmount: Double,
        nameOfMoney: String,
        activeSelection: Boolean
    )
}