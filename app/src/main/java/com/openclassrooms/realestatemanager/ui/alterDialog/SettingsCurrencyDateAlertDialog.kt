package com.openclassrooms.realestatemanager.ui.alterDialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.DialogSettingsUtilsBinding
import com.openclassrooms.realestatemanager.ui.MainActivity
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import com.openclassrooms.realestatemanager.ui.utils.Utils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SettingsCurrencyDateAlertDialog(
    private val context: Context,
    private val sharedUtilsViewModel: SharedUtilsViewModel
) {

    private var alertDialog: AlertDialog? = null
    private lateinit var binding: DialogSettingsUtilsBinding

    fun showSettings() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.settings)
        val inflater = LayoutInflater.from(context)
        binding = DialogSettingsUtilsBinding.inflate(inflater)
        builder.setView(binding.root)
        sharedUtilsViewModel.getDateFormatSelected.observe(context as MainActivity) {
            initDateFormatSelected(it)
        }
        initMoneyRateSelected()

        builder.setPositiveButton("Save") { dialog, _ ->
            setConvertMoneyValidate()
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


    private fun initMoneyRateSelected() {
        if (sharedUtilsViewModel.getMoneyRateSelected.value == true) {
            binding.radioButtonDollars.isChecked = false
            binding.radioButtonEuros.isChecked = true
        } else {
            binding.radioButtonEuros.isChecked = false
            binding.radioButtonDollars.isChecked = true
        }
    }

    private fun setSimpleDateFormat() {
        val dateFormatSelected = if (binding.usDateFormatButton.isChecked) {
            Utils.todayDateUsaFormat
        } else {
            Utils.todayDateFranceFormat
        }
        sharedUtilsViewModel.setDateFormatSelected(dateFormatSelected)
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
        binding.radioButtonDollars.setOnClickListener {
            binding.radioButtonEuros.isSelected = false
            binding.radioButtonDollars.isSelected = true
        }
        binding.radioButtonEuros.setOnClickListener {
            binding.radioButtonEuros.isSelected = true
            binding.radioButtonDollars.isSelected = false
        }
    }

    private fun setConvertMoneyValidate() {
        val isEurosSelected = binding.radioButtonEuros.isChecked
        sharedUtilsViewModel.setActiveSelectionMoneyRate(isEurosSelected)
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