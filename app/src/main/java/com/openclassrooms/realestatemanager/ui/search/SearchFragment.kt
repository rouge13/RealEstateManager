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
import androidx.navigation.findNavController
import com.bumptech.glide.load.model.ByteArrayLoader.Converter
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import com.openclassrooms.realestatemanager.databinding.FragmentSearchPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchPropertyBinding
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
    private val searchCriteria = SearchCriteria()

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
        binding.btnPropertyDateEnd.isEnabled = false
        getAllValuesEdited()
        initTypeOfHouseBoroughsAndCities()
        initAgentsNames()
        initAllSwitches()
        initSearchButton()

    }

    private fun initSearchButton() {
        binding.searchProperty.setOnClickListener {
            sharedPropertyViewModel.setFilteredProperties(searchCriteria)
            view?.findNavController()
                ?.navigate(SearchFragmentDirections.actionSearchFragmentToPropertyListFragment())
        }
    }

    private fun getAllValuesEdited() {
        startPriceValue()
        endPriceValue()
        startSquareFeetValue()
        endSquareFeetValue()
        startRoomsNumberValue()
        endRoomsNumberValue()
        startBedroomsNumberValue()
        endBedroomsNumberValue()
        startBathroomsNumberValue()
        endBathroomsNumberValue()
        startPhotoNumberValue()
        endPhotoNumberValue()
    }

    private fun endPhotoNumberValue() {
        binding.propertyPhotosNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyPhotosNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountPhotosForQuery =
                        binding.propertyPhotosNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startPhotoNumberValue() {
        binding.propertyPhotosNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyPhotosNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountPhotosForQuery =
                        binding.propertyPhotosNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endBathroomsNumberValue() {
        binding.propertyBathroomsNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBathroomsNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountBathroomsForQuery =
                        binding.propertyBathroomsNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startBathroomsNumberValue() {
        binding.propertyBathroomsNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBathroomsNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountBathroomsForQuery =
                        binding.propertyBathroomsNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endBedroomsNumberValue() {
        binding.propertyBedroomsNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBedroomsNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountBedroomsForQuery =
                        binding.propertyBedroomsNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startBedroomsNumberValue() {
        binding.propertyBedroomsNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyBedroomsNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountBedroomsForQuery =
                        binding.propertyBedroomsNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endRoomsNumberValue() {
        binding.propertyRoomsNumberEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyRoomsNumberEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxCountRoomsForQuery =
                        binding.propertyRoomsNumberEndValue.text.toString().toInt()
                }
            }
    }

    private fun startRoomsNumberValue() {
        binding.propertyRoomsNumberStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyRoomsNumberStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinCountRoomsForQuery =
                        binding.propertyRoomsNumberStartValue.text.toString().toInt()
                }
            }
    }

    private fun endSquareFeetValue() {
        binding.propertySquareFeetEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertySquareFeetEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxSquareFeetForQuery =
                        binding.propertySquareFeetEndValue.text.toString().toInt()
                }
            }
    }

    private fun startSquareFeetValue() {
        binding.propertySquareFeetStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertySquareFeetStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinSquareFeetForQuery =
                        binding.propertySquareFeetStartValue.text.toString().toInt()
                }
            }
    }

    private fun endPriceValue() {
        binding.propertyPriceEndValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyPriceEndValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMaxPriceForQuery =
                        binding.propertyPriceEndValue.text.toString().toInt()
                }
            }
    }

    private fun startPriceValue() {
        binding.propertyPriceStartValue.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus && binding.propertyPriceStartValue.text!!.isNotEmpty()) {
                    searchCriteria.selectedMinPriceForQuery =
                        binding.propertyPriceStartValue.text.toString().toInt()
                }
            }
    }


    private fun initAllSwitches() {
        initSchoolProximitySwitch()
        initShopProximitySwitch()
        initParkProximitySwitch()
        initRestaurantProximitySwitch()
        initPublicTransportProximitySwitch()
        initSoldSwitch()
    }

    private fun initSoldSwitch() {
        binding.switchSold.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedIsSoldForQuery = isChecked
        }
    }

    private fun initPublicTransportProximitySwitch() {
        binding.switchTransport.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedPublicTransportProximityQuery = isChecked
        }
    }

    private fun initRestaurantProximitySwitch() {
        binding.switchRestaurant.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedRestaurantProximityQuery = isChecked
        }
    }

    private fun initParkProximitySwitch() {
        binding.switchPark.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedParkProximityQuery = isChecked
        }
    }

    private fun initShopProximitySwitch() {
        binding.switchShopping.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedShopProximityQuery = isChecked
        }
    }

    private fun initSchoolProximitySwitch() {
        binding.switchSchool.setOnCheckedChangeListener { _, isChecked ->
            searchCriteria.selectedSchoolProximityQuery = isChecked
        }
    }

    private fun updateSelectedAgentsIds(selectedAgentsEmails: List<String>) {
        sharedAgentViewModel.allAgents.observe(viewLifecycleOwner) { agents ->
            val selectedAgentsIds = selectedAgentsEmails.mapNotNull { selectedEmail ->
                agents.firstOrNull { it.email == selectedEmail }?.id
            }
            searchCriteria.selectedAgentsIdsForQuery = selectedAgentsIds
        }
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
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedAgentsEmails = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            updateSelectedAgentsIds(selectedAgentsEmails)
        }
    }

    private fun initTypeOfHouseBoroughsAndCities() {
        sharedPropertyViewModel.getPropertiesWithDetails.observe(viewLifecycleOwner) { propertiesWithDetails ->
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
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedCities = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            searchCriteria.selectedCitiesForQuery = selectedCities
        }
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
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedBoroughs = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            searchCriteria.selectedBoroughsForQuery = selectedBoroughs
        }
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
        multiAutoCompleteTextView.setOnItemClickListener { _, _, _, _ ->
            val selectedTypeOfHouse = multiAutoCompleteTextView.text
                .split(",").map { it.trim() }.filter { it.isNotEmpty() }
            searchCriteria.selectedTypeOfHouseForQuery = selectedTypeOfHouse
        }
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
            val dateInMillis: Long? = try {
                searchCriteria.selectedStartDateForQuery?.let { it1 ->
                    SimpleDateFormat("MM/dd/yyyy", Locale.US).parse(
                        it1
                    )?.time
                }
            } catch (e: ParseException) {
                e.printStackTrace()
                null
            }
            binding.btnPropertyDateEnd.isEnabled = (dateInMillis!! > 0L)
            datePickerDialog.datePicker.minDate = dateInMillis
            datePickerDialog.setOnDismissListener {
                calendar.apply {
                    set(Calendar.YEAR, datePickerDialog.datePicker.year)
                    set(Calendar.MONTH, datePickerDialog.datePicker.month)
                    set(Calendar.DAY_OF_MONTH, datePickerDialog.datePicker.dayOfMonth)
                }
                searchCriteria.selectedEndDateForQuery =
                    SimpleDateFormat("MM/dd/yyyy", Locale.US).format(calendar.timeInMillis)
            }
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
                searchCriteria.selectedStartDateForQuery =
                    SimpleDateFormat("MM/dd/yyyy", Locale.US).format(calendar.timeInMillis)
            }
            // Show the date picker dialog
            datePickerDialog.show()
        }
    }
}