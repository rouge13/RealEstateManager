package com.openclassrooms.realestatemanager.ui.sharedViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.openclassrooms.realestatemanager.data.model.ConvertMoneyEntity
import com.openclassrooms.realestatemanager.data.repository.ConvertMoneyRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedUtilsViewModel(private val convertMoneyRepository: ConvertMoneyRepository) :
    ViewModel() {

    // get all the moneyRates from the database
    val getAllMoneyRate = convertMoneyRepository.allMoneyRates.asLiveData()

    // Update a moneyRate in the database
    suspend fun updateMoneyRate(convertMoneyEntity: ConvertMoneyEntity) {
        convertMoneyRepository.update(convertMoneyEntity)
    }

    // Date format selected by the agent in the settings with the getter and the setter
    private val _dateFormatSelected = MutableLiveData<SimpleDateFormat>()
    val getDateFormatSelected: LiveData<SimpleDateFormat> get() = _dateFormatSelected
    fun setDateFormatSelected(dateFormatSelected: SimpleDateFormat) {
        _dateFormatSelected.value = dateFormatSelected
    }
}