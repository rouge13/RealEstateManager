package com.openclassrooms.realestatemanager.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentSearchPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import java.util.Calendar

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SearchFragment : Fragment(){
    private lateinit var binding: FragmentSearchPropertyBinding
    private val _startDateInMillis = MutableLiveData<Long?>()
    private val startDateInMillis: LiveData<Long?> get() = _startDateInMillis
    private val viewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory(
            (requireActivity().application as MainApplication).agentRepository,
            (requireActivity().application as MainApplication).propertyRepository,
            (requireActivity().application as MainApplication).addressRepository,
            (requireActivity().application as MainApplication).photoRepository,
            requireActivity().application as MainApplication
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAllButtons()
        observeStartDate()
    }

    private fun initAllButtons() {
        binding.cancelButton.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToPropertyListFragment()
            binding.root.findNavController().navigate(action)
        }
        binding.searchProperty.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToPropertyListFragment()
            binding.root.findNavController().navigate(action)
        }
        initStartDate()
        initEndDate()
    }

    private fun initEndDate() {
        binding.btnPropertyDateEnd.setOnClickListener {
            // Get the current date as a Calendar instance
            val calendar = Calendar.getInstance()
            // Create a DatePickerDialog with the current date as the default date
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // When a date is selected, update the EditText with the selected date
                    val selectedMonth = month + 1 // Add 1 to the month value
                    val selectedDate = "$selectedMonth/$dayOfMonth/$year"
                    binding.propertyDateEndText.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = startDateInMillis.value ?: 0L
            // Show the date picker dialog
            datePickerDialog.show()
        }
    }

    private fun initStartDate() {
        binding.btnPropertyDateStart.setOnClickListener {
            // Get the current date as a Calendar instance
            val calendar = Calendar.getInstance()
            // Create a DatePickerDialog with the current date as the default date
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedMonth = month + 1 // Add 1 to the month value
                    val selectedDate = "$selectedMonth/$dayOfMonth/$year"
                    // When a date is selected, update the EditText with the selected date
                    binding.propertyDateStartText.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.setOnDismissListener {
                calendar.apply {
                    set(Calendar.YEAR, datePickerDialog.datePicker.year)
                    set(Calendar.MONTH, datePickerDialog.datePicker.month)
                    set(Calendar.DAY_OF_MONTH, datePickerDialog.datePicker.dayOfMonth)
                }
                _startDateInMillis.value = calendar.timeInMillis
            }
            // Show the date picker dialog
            datePickerDialog.show()
        }
    }


    private fun observeStartDate() {
        startDateInMillis.observe(viewLifecycleOwner) { startDate ->
            binding.btnPropertyDateEnd.isEnabled = (startDate != null) && (startDate > 0L)
        }
    }
}