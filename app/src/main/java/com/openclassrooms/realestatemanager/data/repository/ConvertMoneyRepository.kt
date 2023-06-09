package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.data.dao.ConvertMoneyDao
import com.openclassrooms.realestatemanager.data.model.ConvertMoneyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class ConvertMoneyRepository(private val convertMoneyDao: ConvertMoneyDao) {
    // get all the moneyRates from the database
    val allMoneyRates: Flow<List<ConvertMoneyEntity>> = convertMoneyDao.getAllMoneyRates()

    // insert a moneyRate in the database
    suspend fun insert(moneyRate: ConvertMoneyEntity) {
        convertMoneyDao.insert(moneyRate)
    }

    // Update a moneyRate in the database
    suspend fun update(moneyRate: ConvertMoneyEntity) {
        moneyRate.nameOfMoney?.let {
            moneyRate.changeAmount?.let { it1 ->
                moneyRate.activeSelection?.let { it2 ->
                    convertMoneyDao.updateMoneyRates(
                        nameOfMoney = it,
                        changeAmount = it1,
                        activeSelection = it2
                    )
                }
            }
        }
    }

    // Set the active selection of the moneyRate in the database
    suspend fun setActiveSelectionMoneyRate(nameOfMoney: String, activeSelection: Boolean) {
        convertMoneyDao.setActiveSelectionMoneyRate(nameOfMoney, activeSelection)
    }

    // Get the money rate selected by the agent
    fun getMoneyRateSelected(): Flow<ConvertMoneyEntity> {
        return convertMoneyDao.getMoneyRateSelected()
    }
}