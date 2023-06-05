package com.openclassrooms.realestatemanager.ui.loan

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.DialogRealEstateLoanSimulatorBinding
import java.text.DecimalFormat
import kotlin.math.pow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class LoanSimulatorAlertDialog(private val context: Context) {

    private var alertDialog: AlertDialog? = null
    private lateinit var binding: DialogRealEstateLoanSimulatorBinding

    fun showLoanSimulatorAlertDialog(priceOfProperty : Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.real_estate_loan_simulator)

        val inflater = LayoutInflater.from(context)
        binding = DialogRealEstateLoanSimulatorBinding.inflate(inflater)

        builder.setView(binding.root)
        alertDialog = builder.create()
        alertDialog?.show()

        setupListeners(priceOfProperty)

    }

    private fun setupListeners(priceOfProperty: Int) {
        val decimalFormat = DecimalFormat("#,###")
        val formattedLoanAmount = decimalFormat.format(priceOfProperty)
        binding.loanAmountValue.setText(formattedLoanAmount)

        binding.calculateButton.setOnClickListener {
            val loanAmountText = binding.loanAmountValue.text.toString().replace(",", "") // Remove commas (thousands separator)
            val loanAmount = loanAmountText.toDoubleOrNull()
            val interestRate = binding.interestRateValue.text.toString().toDoubleOrNull()
            val loanDuration = binding.loanDurationValue.text.toString().toIntOrNull()

            if (loanAmount != null && interestRate != null && loanDuration != null) {
                val monthlyPayment = calculateMonthlyPayment(loanAmount, interestRate, loanDuration)
                val formattedMonthlyPayment = decimalFormat.format(monthlyPayment)
                binding.monthlyPaymentTextView.text = "Monthly payment: $formattedMonthlyPayment"
            } else {
                Toast.makeText(context, "Make sure all values entered are valid!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateMonthlyPayment(loanAmount: Double, interestRate: Double, loanDuration: Int): Double {
        val monthlyInterestRate = interestRate / 100 / 12
        val numberOfPayments = loanDuration * 12
        val denominator = (1 + monthlyInterestRate).pow(numberOfPayments.toDouble()) - 1
        return loanAmount * monthlyInterestRate * (1 + monthlyInterestRate).pow(numberOfPayments.toDouble()) / denominator
    }

}