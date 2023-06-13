package com.openclassrooms.realestatemanager.ui.alertDialog

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

    fun showLoanSimulator(priceOfProperty : Int, isEuroOrDollarSelected: Boolean) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.real_estate_loan_simulator)
        val inflater = LayoutInflater.from(context)
        binding = DialogRealEstateLoanSimulatorBinding.inflate(inflater)
        builder.setView(binding.root)
        // Add the cancel button
        builder.setNegativeButton(R.string.cancel) { _, _ ->
            alertDialog?.dismiss()
        }
        alertDialog = builder.create()
        alertDialog?.show()
        setupListeners(priceOfProperty, isEuroOrDollarSelected)
    }

    private fun setupListeners(priceOfProperty: Int, isEuroOrDollarSelected: Boolean) {
        val decimalFormat = DecimalFormat("#,###")
        binding.loanAmountValue.setText(priceOfProperty.toString())
        if (isEuroOrDollarSelected) {
            binding.loanAmountTextView.setText(R.string.loan_amount_in_euro) // Loan amount: €
        } else {
            binding.loanAmountTextView.setText(R.string.loan_amount_in_dollar) // Loan amount: $
        }
        binding.calculateButton.setOnClickListener {
            val loanAmountText = binding.loanAmountValue.text.toString()
            val loanAmount = loanAmountText.toIntOrNull()
            val interestRate = binding.interestRateValue.text.toString().toDoubleOrNull()
            val loanDuration = binding.loanDurationValue.text.toString().toIntOrNull()
            val personalContribution = binding.personalContributionValue.text.toString().toIntOrNull()

            if (loanAmount != null && interestRate != null && loanDuration != null && personalContribution != null) {
                val monthlyPayment = calculateMonthlyPayment(loanAmount, interestRate, loanDuration, personalContribution)
                val formattedMonthlyPayment = decimalFormat.format(monthlyPayment)
                if (isEuroOrDollarSelected) {
                    binding.monthlyPaymentTextView.text = "Monthly payment: $formattedMonthlyPayment€" // Loan amount: €
                } else {
                    binding.monthlyPaymentTextView.text = "Monthly payment: $$formattedMonthlyPayment" // Loan amount: $
                }
            } else {
                Toast.makeText(context, "Make sure all values entered are valid!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calculateMonthlyPayment(
        loanAmount: Int,
        interestRate: Double,
        loanDuration: Int,
        personalContribution: Int
    ): Double {
        val loanAmount = loanAmount - personalContribution
        val monthlyInterestRate = interestRate / 100 / 12
        val numberOfPayments = loanDuration * 12
        // Pow means power for mathematical use example of, so 2.pow(3) = 2^3 = 8 same as 2 * 2 * 2 = 8
        // Denominator equal to (1 + monthlyInterestRate)^numberOfPayments - 1 to get the result of the fraction in number of payments
        val denominator = (1 + monthlyInterestRate).pow(numberOfPayments.toDouble()) - 1
        // Return the monthly payment amount using the formula from https://www.thebalance.com/loan-payment-calculations-315564
        return loanAmount * monthlyInterestRate * (1 + monthlyInterestRate).pow(numberOfPayments.toDouble()) / denominator
    }

}