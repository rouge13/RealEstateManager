package com.openclassrooms.realestatemanager.ui.addAndModification

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.databinding.FragmentAddAndModifyPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.converter.Converters
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationFragment : Fragment() {
    private lateinit var adapter: AddAndModificationAdapter
    private lateinit var binding: FragmentAddAndModifyPropertyBinding
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private val converters = Converters()
    private val sharedPropertyViewModel: SharedPropertyViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val sharedNavigationViewModel: SharedNavigationViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }
    private val sharedAgentViewModel: SharedAgentViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddAndModifyPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedNavigationViewModel.getAddOrModifyClicked.observe(viewLifecycleOwner) { isModify ->
            if (isModify) {
                binding.propertySwitchSold.visibility = View.VISIBLE
                displayPropertyDetails(view)
            } else {
                binding.propertySwitchSold.visibility = View.GONE
            }
        }
    }

    private fun displayPropertyDetails(view: View) {
        sharedPropertyViewModel.getSelectedProperty.observe(viewLifecycleOwner) { propertyWithDetails ->
            propertyWithDetails?.let {
                initDate(propertyWithDetails)
                initAllEditText(propertyWithDetails)
                initAllSwitch(propertyWithDetails)
                setupRecyclerView(propertyWithDetails.photos)
                initUpdateButton()
                selectDate()

            }
        }
    }

    private fun selectDate() {
        binding.btnPropertyDate.setOnClickListener {
            // Get the current date as a Calendar instance
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("UTC")
            // Create a DatePickerDialog with the current date as the default date
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    // When a date is selected, update the EditText with the selected date
                    val selectedMonth = month + 1 // Add 1 to the month value
                    val selectedDate = "$selectedMonth/$dayOfMonth/$year"
                    binding.propertyDateText.text = selectedDate
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
                binding.propertyDateText.text = SimpleDateFormat(
                    "MM/dd/yyyy",
                    Locale.getDefault()
                ).format(Date(calendar.timeInMillis))
            }
            // Show the date picker dialog
            datePickerDialog.show()
        }
    }

    private fun initUpdateButton() {
        binding.btnValidate.setOnClickListener {
            sharedPropertyViewModel.getSelectedProperty.value?.let { propertyWithDetails ->
                updatePropertyEntity(propertyWithDetails)
                updateAddressEntity(propertyWithDetails)
                updateAgentEntity(propertyWithDetails)

                findNavController().navigate(R.id.propertyListFragment)
                Toast.makeText(requireContext(), "Property updated", Toast.LENGTH_SHORT).show()
//                sharedNavigationViewModel.setAddOrModifyClicked(false)
            }
        }
    }

    private fun updateAgentEntity(propertyWithDetails: PropertyWithDetails) {

    }

    private fun updateAddressEntity(propertyWithDetails: PropertyWithDetails) {
        val addressEntity = propertyWithDetails.address
        addressEntity?.apply {
            addressToUpdate(propertyWithDetails)
        }
        lifecycleScope.launch {
            addressEntity?.let { sharedPropertyViewModel.updateAddress(it) }
        }
    }

    private fun updatePropertyEntity(propertyWithDetails: PropertyWithDetails) {
        val propertyEntity = propertyWithDetails.property
        propertyEntity.apply {
            propertyToUpdate(propertyWithDetails)
        }
        lifecycleScope.launch {
            sharedPropertyViewModel.updateProperty(propertyEntity)
        }
    }

    private fun PropertyEntity.propertyToUpdate(
        propertyWithDetails: PropertyWithDetails
    ) {
        id = propertyWithDetails.property.id
        if (binding.propertySwitchSold.isChecked) {
            dateSold = converters.dateToTimestamp(dateFormat.parse(binding.propertyDateText.text.toString()))
        } else if (!binding.propertySwitchSold.isChecked && propertyWithDetails.property.dateSold != null) {
            dateSold = null
        }
        price = binding.propertyPrice.text.toString().toInt()
        squareFeet = binding.propertySquareFeet.text.toString().toInt()
        roomsCount = binding.propertyRoomsCount.text.toString().toInt()
        bedroomsCount = binding.propertyBedroomsCount.text.toString().toInt()
        bathroomsCount = binding.propertyBathroomsCount.text.toString().toInt()
        description = binding.propertyDescription.text.toString()
        typeOfHouse = binding.propertyType.text.toString()
        schoolProximity = binding.propertySwitchSchool.isChecked
        parkProximity = binding.propertySwitchPark.isChecked
        shoppingProximity = binding.propertySwitchShopping.isChecked
        restaurantProximity = binding.propertySwitchRestaurant.isChecked
        publicTransportProximity = binding.propertySwitchPublicTransport.isChecked
        isSold = binding.propertySwitchSold.isChecked
        lastUpdate = System.currentTimeMillis()
    }

    private fun AddressEntity.addressToUpdate(
        propertyWithDetails: PropertyWithDetails
    ) {
        id = propertyWithDetails.address?.id
        streetNumber = binding.addressStreetNumber.text.toString()
        streetName = binding.addressStreetName.text.toString()
        city = binding.addressCity.text.toString()
        boroughs = binding.addressBoroughs.text.toString()
        zipCode = binding.addressZipCode.text.toString()
        country = binding.addressCountry.text.toString()
        apartmentDetails = binding.apartmentDetails.text.toString()
    }


    private fun initDate(propertyWithDetails: PropertyWithDetails) {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        if (propertyWithDetails.property.isSold == true) {
            val dateSold = propertyWithDetails.property.dateSold?.let { Date(it) }
            binding.propertyDateText.text = "Sold on : ${dateSold?.let { sdf.format(it) }}"
        } else {
            val dateSale = propertyWithDetails.property.dateStartSelling?.let { Date(it) }
            binding.propertyDateText.text = "Sale date : ${dateSale?.let { sdf.format(it) }}"
        }
    }

    private fun initAllSwitch(propertyWithDetails: PropertyWithDetails) {
        propertyWithDetails.property.isSold?.let {
            binding.propertySwitchSold.isChecked = it
        }
        propertyWithDetails.property.schoolProximity?.let {
            binding.propertySwitchSchool.isChecked = it
        }
        propertyWithDetails.property.parkProximity?.let {
            binding.propertySwitchPark.isChecked = it
        }
        propertyWithDetails.property.shoppingProximity?.let {
            binding.propertySwitchShopping.isChecked = it
        }
        propertyWithDetails.property.restaurantProximity?.let {
            binding.propertySwitchRestaurant.isChecked = it
        }
        propertyWithDetails.property.publicTransportProximity?.let {
            binding.propertySwitchPublicTransport.isChecked = it
        }
    }

    //    private fun setupRecyclerView(photoList: List<PhotoEntity>?) {
//        val drawableList = photoList?.map { photoEntity ->
//            // Convert PhotoEntity to Drawable
//            val drawable: Drawable? = try {
//                val packageName = requireActivity().packageName
//                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(Uri.parse("android.resource://$packageName/drawable/${photoEntity.photo}"))
//                Drawable.createFromStream(inputStream, null)
//            } catch (e: IOException) {
//                null
//            }
//            drawable
//        }
//        adapter = drawableList?.let { AddAndModificationAdapter(it) }!!
//        binding.photoAddRemoveViewPager.adapter = adapter
//    }
    private fun setupRecyclerView(photoList: List<PhotoEntity>?) {
        val drawableList = photoList?.map { photoEntity ->
            // Convert PhotoEntity to Drawable
            val resourceId = resources.getIdentifier(
                photoEntity.photo,
                "drawable",
                requireActivity().packageName
            )
            val drawable: Drawable? = try {
                ContextCompat.getDrawable(requireContext(), resourceId)
            } catch (e: Exception) {
                null
            }
            drawable
        }
        adapter = drawableList?.let { AddAndModificationAdapter(it) }!!
        binding.photoAddRemoveViewPager.adapter = adapter
    }

    private fun initAllEditText(propertyWithDetails: PropertyWithDetails) {
        propertyWithDetails.property.price?.let { binding.propertyPrice.setText(it.toString()) }
        propertyWithDetails.property.squareFeet?.let { binding.propertySquareFeet.setText(it.toString()) }
        propertyWithDetails.property.roomsCount?.let { binding.propertyRoomsCount.setText(it.toString()) }
        propertyWithDetails.property.bedroomsCount?.let { binding.propertyBedroomsCount.setText(it.toString()) }
        propertyWithDetails.property.bathroomsCount?.let { binding.propertyBathroomsCount.setText(it.toString()) }
        binding.propertyDescription.setText(propertyWithDetails.property.description)
        sharedAgentViewModel.getAgentData(propertyWithDetails.property.agentId)
            .observe(viewLifecycleOwner) { agent ->
                agent?.let {
                    binding.agentLastFirstName.setText("${agent.lastName} ${agent.firstName}")
                }
            }
        propertyWithDetails.property.typeOfHouse.let { binding.propertyType.setText(it) }
        propertyWithDetails.address?.streetNumber.let { binding.addressStreetNumber.setText(it) }
        propertyWithDetails.address?.streetName.let { binding.addressStreetName.setText(it) }
        propertyWithDetails.address?.city.let { binding.addressCity.setText(it) }
        propertyWithDetails.address?.boroughs.let { binding.addressBoroughs.setText(it) }
        propertyWithDetails.address?.zipCode.let { binding.addressZipCode.setText(it) }
        propertyWithDetails.address?.country.let { binding.addressCountry.setText(it) }
        propertyWithDetails.address?.apartmentDetails.let { binding.apartmentDetails.setText(it) }
    }


}