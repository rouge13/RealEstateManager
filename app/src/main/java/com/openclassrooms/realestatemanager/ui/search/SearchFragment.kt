package com.openclassrooms.realestatemanager.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.databinding.FragmentSearchPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import java.util.Calendar

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SearchFragment : Fragment(){
    private lateinit var binding: FragmentSearchPropertyBinding
    private val _agentsId = MutableLiveData<List<Int>>()
    private val agentsId: LiveData<List<Int>> get() = _agentsId
    private val _startDateInMillis = MutableLiveData<Long?>()
    private val startDateInMillis: LiveData<Long?> get() = _startDateInMillis
    private val sharedPropertyViewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory(
            (requireActivity().application as MainApplication).agentRepository,
            (requireActivity().application as MainApplication).propertyRepository,
            (requireActivity().application as MainApplication).addressRepository,
            (requireActivity().application as MainApplication).photoRepository,
            requireActivity().application as MainApplication
        )
    }
    private val sharedAgentViewModel: SharedAgentViewModel by activityViewModels {
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
        initTypeOfHouseBoroughsAndCities()
        initAgentsNames()

    }

    private fun initAgentsNames() {
        sharedAgentViewModel.allAgents.observe(viewLifecycleOwner) { agents ->
            val agentsEmails = agents.map { it.email }.distinct()
            initAgentsEmails(agentsEmails)
        }
    }

    private fun initAgentsEmails(agentsEmails: List<String>) {
        val multiAutoCompleteTextView = binding.propertyAgentSellerMultiAutoComplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            agentsEmails
        )
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        multiAutoCompleteTextView.threshold = 2 // Start suggesting after typing one character
    }

    private fun initTypeOfHouseBoroughsAndCities() {
        sharedPropertyViewModel.propertiesWithDetails.observe(viewLifecycleOwner) { propertiesWithDetails ->
            val typesOfHouse = propertiesWithDetails.map { it.property.typeOfHouse }.distinct()
            val boroughs = propertiesWithDetails.mapNotNull { it.address?.boroughs }.distinct()
            val cities = propertiesWithDetails.mapNotNull { it.address?.city }.distinct()
            initTypesOfHouse(typesOfHouse)
            initBoroughs(boroughs)
            initCities(cities)
        }
    }

    private fun initCities(cities: List<String>) {
        val multiAutoCompleteTextView = binding.propertyCityMultiAutoComplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            cities
        )
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        multiAutoCompleteTextView.threshold = 2 // Start suggesting after typing one character
    }

    private fun initBoroughs(boroughs: List<String>) {
        val multiAutoCompleteTextView = binding.propertyBoroughsMultiAutoComplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            boroughs
        )
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        multiAutoCompleteTextView.threshold = 2 // Start suggesting after typing one character
    }

    private fun initTypesOfHouse(typesOfHouse: List<String>) {
        val multiAutoCompleteTextView = binding.propertyTypeOfHouseMultiAutoComplete
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            typesOfHouse
        )
        multiAutoCompleteTextView.setAdapter(adapter)
        multiAutoCompleteTextView.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        multiAutoCompleteTextView.threshold = 2 // Start suggesting after typing one character
    }

    private fun initCommaTokenizer() {

        binding.propertyBoroughsMultiAutoComplete.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        binding.propertyCityMultiAutoComplete.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
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