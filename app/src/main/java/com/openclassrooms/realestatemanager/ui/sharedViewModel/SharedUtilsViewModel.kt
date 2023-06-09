package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.model.ConvertMoneyEntity
import com.openclassrooms.realestatemanager.data.repository.ConvertMoneyRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedUtilsViewModel(private val convertMoneyRepository: ConvertMoneyRepository) :
    ViewModel() {

    // Update a moneyRate in the database
    suspend fun updateMoneyRate(convertMoneyEntity: ConvertMoneyEntity) {
        convertMoneyRepository.update(convertMoneyEntity)
    }

    // Set the active selection of the moneyRate in the database
    suspend fun setActiveSelectionMoneyRate(nameOfMoney: String, activeSelection: Boolean) {
        convertMoneyRepository.setActiveSelectionMoneyRate(nameOfMoney, activeSelection)
    }

    // Get the money rate selected by the agent
    val getMoneyRateSelected: Flow<ConvertMoneyEntity> get(){
        return convertMoneyRepository.getMoneyRateSelected()
    }


    // Date format selected by the agent in the settings with the getter and the setter
    private val _dateFormatSelected = MutableLiveData<SimpleDateFormat>()
    val getDateFormatSelected: LiveData<SimpleDateFormat> get() = _dateFormatSelected
    fun setDateFormatSelected(dateFormatSelected: SimpleDateFormat) {
        _dateFormatSelected.value = dateFormatSelected
    }
}

