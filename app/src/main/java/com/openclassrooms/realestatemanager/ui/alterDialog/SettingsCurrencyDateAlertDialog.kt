package com.openclassrooms.realestatemanager.ui.alterDialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.ConvertMoneyEntity
import com.openclassrooms.realestatemanager.databinding.DialogSettingsUtilsBinding
import com.openclassrooms.realestatemanager.ui.MainActivity
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import com.openclassrooms.realestatemanager.ui.utils.Utils
import java.text.SimpleDateFormat
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SettingsCurrencyDateAlertDialog(
    private val context: Context,
    private val sharedUtilsViewModel: SharedUtilsViewModel,
    private val moneyRate: ConvertMoneyEntity
) {

    private var alertDialog: AlertDialog? = null
    private lateinit var binding: DialogSettingsUtilsBinding


    fun show() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.real_estate_loan_simulator)
        val inflater = LayoutInflater.from(context)
        binding = DialogSettingsUtilsBinding.inflate(inflater)
        builder.setView(binding.root)
        sharedUtilsViewModel.getDateFormatSelected.observe(context as MainActivity) {
            initDateFormatSelected(it)
        }
        initMoneyRateSelected(moneyRate)

        builder.setPositiveButton("Save") { dialog, _ ->
            (context as? LifecycleOwner)?.lifecycleScope?.launch {
                setMoneyRateSelected()
            }
            setSimpleDateFormat()
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        alertDialog = builder.create()
        alertDialog?.show()

        setupListeners()
    }

    private fun initMoneyRateSelected(moneyRate: ConvertMoneyEntity) {
        when (moneyRate.nameOfMoney) {
            "DollarToEuro" -> {
                binding.radioButtonDollars.isChecked = true
                binding.radioButtonEuros.isChecked = false
                binding.conversionRemoved.isChecked = false
            }
            "EuroToDollar" -> {
                binding.radioButtonDollars.isChecked = false
                binding.radioButtonEuros.isChecked = true
                binding.conversionRemoved.isChecked = false
            }
            else -> {
                binding.radioButtonDollars.isChecked = false
                binding.radioButtonEuros.isChecked = false
                binding.conversionRemoved.isChecked = true
            }
        }
    }

    private fun setSimpleDateFormat() {
        val dateFormatSelected = if (binding.usDateFormatButton.isSelected) {
            Utils.todayDateUsaFormat
        } else {
            Utils.todayDateFranceFormat
        }
        sharedUtilsViewModel.setDateFormatSelected(dateFormatSelected)
    }

    private suspend fun setMoneyRateSelected() {
        val moneyRateSelected = if (binding.radioButtonDollars.isSelected) {
            "DollarToEuro"
        } else if (binding.radioButtonEuros.isSelected) {
            "EuroToDollar"
        } else {
            "ConversionRemoved"
        }
        sharedUtilsViewModel.setActiveSelectionMoneyRate(moneyRateSelected, true)
    }

    private fun initDateFormatSelected(dateFormat: SimpleDateFormat?) {
        val dateFormatSelected = dateFormat?.toPattern()
        if (dateFormatSelected == "yyyy/MM/dd") {
            binding.usDateFormatButton.isChecked = true
        } else {
            binding.euDateFormatButton.isChecked = true
        }
    }

    private fun setupListeners() {
        // Setup the listener for the button to save the new currency

        // Update the date format selected by the agent in the settings
        setDateFormat()
        setConvertMoney()
    }

    private fun setConvertMoney() {
        binding.conversionRemoved.setOnClickListener {
            binding.conversionRemoved.isSelected = true
            binding.radioButtonDollars.isSelected = false
            binding.radioButtonEuros.isSelected = false
        }
        binding.radioButtonDollars.setOnClickListener {
            binding.radioButtonDollars.isSelected = true
            binding.radioButtonEuros.isSelected = false
            binding.conversionRemoved.isSelected = false
        }
        binding.radioButtonEuros.setOnClickListener {
            binding.radioButtonEuros.isSelected = true
            binding.radioButtonDollars.isSelected = false
            binding.conversionRemoved.isSelected = false
        }
    }

    private fun setDateFormat() {
        binding.usDateFormatButton.setOnClickListener {
            binding.usDateFormatButton.isSelected = true
            binding.euDateFormatButton.isSelected = false
        }
        binding.euDateFormatButton.setOnClickListener {
            binding.euDateFormatButton.isSelected = true
            binding.usDateFormatButton.isSelected = false
        }
    }
}