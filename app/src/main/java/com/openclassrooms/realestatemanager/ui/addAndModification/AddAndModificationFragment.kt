package com.openclassrooms.realestatemanager.ui.addAndModification

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.converter.Converters
import com.openclassrooms.realestatemanager.data.di.ViewModelFactory
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.notification.NotificationHelper
import com.openclassrooms.realestatemanager.databinding.FragmentAddAndModifyPropertyBinding
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.alertDialog.PhotoOptionsAndSaveAlertDialog
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedNavigationViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import com.openclassrooms.realestatemanager.ui.utils.Utils
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationFragment : Fragment() {
    private lateinit var photoOptionsAndSaveAlertDialog: PhotoOptionsAndSaveAlertDialog
    private lateinit var adapter: AddAndModificationAdapter
    private lateinit var binding: FragmentAddAndModifyPropertyBinding
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var geocoder: Geocoder
    private lateinit var addressString: String
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
    private val sharedUtilsViewModel: SharedUtilsViewModel by activityViewModels {
        ViewModelFactory(
            requireActivity().application as MainApplication
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAndModifyPropertyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationHelper = NotificationHelper(requireContext())
        sharedNavigationViewModel.getAddOrModifyClicked.observe(viewLifecycleOwner) { isModify ->
            if (isModify) {
                binding.propertySwitchSold.visibility = View.VISIBLE
                displayPropertyDetails()
            } else {
                binding.propertySwitchSold.visibility = View.GONE
                sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) { dateFormat ->
                    binding.propertyDateText.text = dateFormat.format(System.currentTimeMillis())
                }
                initAllAutoCompleteTextView()
                initSelectDate()
                initInsertButton()
                initCancelButton()
            }
        }
    }

    private fun initPhotoOptionsAndSaveAlertDialog() {
        photoOptionsAndSaveAlertDialog = PhotoOptionsAndSaveAlertDialog(
            context = requireContext(),
            fragment = this,
            sharedPropertyViewModel = sharedPropertyViewModel,
            binding = binding,
            adapter = adapter
        )
    }

    private fun initAllAutoCompleteTextView() {
        initAllEditTextRequiredValues()
    }

    private fun displayPropertyDetails() {
        initAllEditTextRequiredValues()
        sharedPropertyViewModel.getSelectedProperty.observe(viewLifecycleOwner) { propertyWithDetails ->
            if (binding.propertySwitchSold.isChecked) {
                binding.dateSaleIfSold.visibility = View.VISIBLE
                binding.dateSale.visibility = View.VISIBLE
            } else {
                binding.dateSaleIfSold.visibility = View.GONE
                binding.dateSale.visibility = View.GONE
            }
            propertyWithDetails?.let {
                sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) { dateFormat ->
                    dateFormat?.let {
                        binding.dateSale.text =
                            it.format(propertyWithDetails.property.dateStartSelling)
                        initDate(propertyWithDetails, it)
                    }
                }
                initAllEditText(propertyWithDetails)
                initAllSwitch(propertyWithDetails)
                setupRecyclerView(propertyWithDetails.photos)
            }
        }
        initCancelButton()
        initUpdateButton()
        initSelectDate()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private suspend fun createAgentAndWait(agentToAdd: String): Int? {
        return suspendCancellableCoroutine { continuation ->
            val inputEditTextField = EditText(requireContext())
            inputEditTextField.setText(agentToAdd)
            val builder = AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.create_agent))
                .setMessage(getString(R.string.create_agent_message))
                .setView(inputEditTextField)
                .setPositiveButton(getString(R.string.create)) { _, _ ->
                    val agentName = inputEditTextField.text.toString()
                    if (agentName.isNotEmpty()) {
                        val agentEntity = AgentEntity(name = agentName, id = null)
                        lifecycleScope.launch {
                            val generatedId = sharedAgentViewModel.insertAgent(agentEntity)
                            continuation.resume(generatedId?.toInt()) {}
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.agent_name_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                        continuation.resume(null) {}
                    }
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                    continuation.resume(null) {}
                }
                .create()
            builder.show()

            continuation.invokeOnCancellation {
                builder.dismiss()
            }
        }
    }

    private fun initAllEditTextRequiredValues() {
        sharedAgentViewModel.allAgents.observe(viewLifecycleOwner) { agents ->
            val agentsNames = agents.map { it.name }.distinct()
            initAgentNames(agentsNames)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedPropertyViewModel.getPropertiesWithDetails.collect { propertiesWithDetails ->
                val typesOfHouse =
                    propertiesWithDetails.mapNotNull { it.property?.typeOfHouse }.distinct()
                val boroughs = propertiesWithDetails.mapNotNull { it.address?.boroughs }.distinct()
                val cities = propertiesWithDetails.mapNotNull { it.address?.city }.distinct()
                val zipCode = propertiesWithDetails.mapNotNull { it.address?.zipCode }.distinct()
                val countries = propertiesWithDetails.mapNotNull { it.address?.country }.distinct()
                initTypesOfHouse(typesOfHouse)
                initBoroughs(boroughs)
                initCities(cities)
                initZipCode(zipCode)
                initCountries(countries)
            }
        }

    }

    private fun initCountries(countries: List<String>) {
        val autoCompleteTextView = binding.addressCountry
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, countries)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initZipCode(zipCode: List<String>) {
        val autoCompleteTextView = binding.addressZipCode
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, zipCode)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initCities(cities: List<String>) {
        val autoCompleteTextView = binding.addressCity
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initBoroughs(boroughs: List<String>) {
        val autoCompleteTextView = binding.addressBoroughs
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, boroughs)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initTypesOfHouse(typesOfHouse: List<String>) {
        val autoCompleteTextView = binding.propertyType
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, typesOfHouse)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initAgentNames(agentsNames: List<String>) {
        val autoCompleteTextView = binding.agentName
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, agentsNames)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initInsertButton() {
        binding.btnValidate.setOnClickListener {
            lifecycleScope.launch {
                if (requiredAllValidateInputsOk() && isPrimaryPhotoSelected(null)) {
                    val insertedPropertyId = insertPropertyEntity()
                    if (insertedPropertyId != null) {
                        notificationHelper.showPropertyInsertedNotification()
                        findNavController().navigate(R.id.propertyListFragment)
                        Toast.makeText(requireContext(), "Property inserted", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill all required fields. And check if you have a photo and primary one selected.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initCancelButton() {
        binding.btnCancel.setOnClickListener {
            lifecycleScope.launch {
                sharedPropertyViewModel.deletePhotosWithNullPropertyId()
            }
            findNavController().popBackStack()
        }
    }

    private fun initSelectDate() {
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
                    sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) {
                        binding.propertyDateText.text = it.format(selectedDate)
                    }

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
                sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) { dateFormat ->
                    binding.propertyDateText.text = dateFormat.format(Date(calendar.timeInMillis))
                }
            }
            // Show the date picker dialog
            datePickerDialog.show()
        }
    }

    private fun initUpdateButton() {
        binding.btnValidate.setOnClickListener {
            sharedPropertyViewModel.getSelectedProperty.value?.let { propertyWithDetails ->
                lifecycleScope.launch {
                    if (requiredAllValidateInputsOk() && isPrimaryPhotoSelected(propertyWithDetails.property.id)) {
                        updatePhotosWithPropertyId(propertyWithDetails.property.id)
                        updatePropertyEntity(propertyWithDetails)
                        updateAddressEntity(propertyWithDetails)
                        findNavController().navigate(R.id.propertyListFragment)
                        Toast.makeText(requireContext(), "Property updated.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please select a primary photo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun updateAddressEntity(propertyWithDetails: PropertyWithDetails) {
        val addressEntity = propertyWithDetails.address
        addressEntity?.apply {
            addressToUpdate(propertyWithDetails)
        }
        lifecycleScope.launch {
            if (sharedNavigationViewModel.getOnlineClicked.value == true) {
                val location = getLocationFromAddress(addressString)
                if (location != null) {
                    addressEntity?.latitude = location.latitude
                    addressEntity?.longitude = location.longitude
                    addressEntity?.let { sharedPropertyViewModel.updateAddress(it) }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Address not found, please check the address if correct !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                addressEntity?.let { sharedPropertyViewModel.updateAddress(it) }
            }
        }
    }

    private fun insertAddressEntity(insertPropertyId: Long) {
        val addressEntity = AddressEntity()
        addressEntity.apply {
            addressToInsert(insertPropertyId)
        }
        lifecycleScope.launch {
            if (sharedNavigationViewModel.getOnlineClicked.value == true) {
                val location = getLocationFromAddress(addressString)
                if (location != null) {
                    addressEntity.latitude = location.latitude
                    addressEntity.longitude = location.longitude
                    addressEntity.let { sharedPropertyViewModel.insertAddress(it) }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Address not found, please check the address if correct !",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                addressEntity.let { sharedPropertyViewModel.insertAddress(it) }
            }
        }
    }

    private suspend fun updatePropertyEntity(propertyWithDetails: PropertyWithDetails) {
        val propertyEntity = propertyWithDetails.property
        propertyEntity.apply {
            propertyToUpdate(propertyWithDetails)
        }
        val agentName = binding.agentName.text.toString()
        val agent = sharedAgentViewModel.getAgentByName(agentName).firstOrNull()
        if (agent != null) {
            propertyEntity.agentId = agent.id!!
            sharedPropertyViewModel.updateProperty(propertyEntity)

        } else {
            val createdAgentId = createAgentAndWait(agentName)
            if (createdAgentId != null) {
                propertyEntity.agentId = createdAgentId
                sharedPropertyViewModel.updateProperty(propertyEntity)
            } else {
                // Agent creation was canceled, perform cancel actions here
                Toast.makeText(requireContext(), "Agent creation canceled", Toast.LENGTH_SHORT)
                    .show()
                // Cancel any other actions related to property update
                return
            }
        }
        updatePhotosWithPropertyId(propertyId = propertyEntity.id!!)
    }

    private suspend fun insertPropertyEntity(): Long? {
        var insertedPropertyId: Long? = null
        val propertyEntity = PropertyEntity()
        propertyEntity.apply {
            propertyToInsert()
        }
        val agentName = binding.agentName.text.toString()
        val agent = sharedAgentViewModel.getAgentByName(agentName).firstOrNull()
        if (agent != null) {
            propertyEntity.agentId = agent.id!!
            val insertedId = sharedPropertyViewModel.insertProperty(propertyEntity)
            if (insertedId != null) {
                insertedPropertyId = insertedId
                insertAddressEntity(insertedPropertyId)
            }
        } else {
            val createdAgentId = createAgentAndWait(agentName)
            if (createdAgentId != null) {
                propertyEntity.agentId = createdAgentId
                val insertPropertyId = sharedPropertyViewModel.insertProperty(propertyEntity)
                if (insertPropertyId != null) {
                    insertedPropertyId = insertPropertyId
                    insertAddressEntity(insertedPropertyId)
                } else {
                    // Agent creation was canceled, perform cancel actions here
                    Toast.makeText(requireContext(), "Agent creation canceled", Toast.LENGTH_SHORT)
                        .show()
                    // Cancel any other actions related to property update
                    return null
                }
            }
        }
        updatePhotosWithPropertyId(propertyId = propertyEntity.id!!)
        return insertedPropertyId
    }

    private suspend fun updatePhotosWithPropertyId(propertyId: Int?) {
        val photosWithNullPropertyId = sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(null)
        photosWithNullPropertyId?.let { photos ->
            var isPrimaryPhotoSet = false
            var primaryPhotoUri: String? = null
            for (photo in photos) {
                photo.id?.let {
                    if (propertyId != null) {
                        sharedPropertyViewModel.updatePhotosWithPropertyId(it, propertyId)
                    }
                }
            }

            val photosWithPropertyId = sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(propertyId)
            photosWithPropertyId?.let { photosWithPropertyId ->
                for (photo in photosWithPropertyId) {
                    if (photo.isPrimaryPhoto && photo.propertyId == propertyId) {
                        photo.photoURI?.let {
                            // Set the primary photo URI
                            primaryPhotoUri = it
                            isPrimaryPhotoSet = true
                        }
                    }
                }
            }

            // Update the primary photo URI for the property
            if (isPrimaryPhotoSet && primaryPhotoUri != null) { sharedPropertyViewModel.updatePrimaryPhoto(propertyId, primaryPhotoUri!!)
            } else {
                // Display a message to ask the agent to select a primary photo
                Toast.makeText(requireContext(), "Please select a primary photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun PropertyEntity.propertyToUpdate(
        propertyWithDetails: PropertyWithDetails
    ) {
        id = propertyWithDetails.property.id
        if (binding.propertySwitchSold.isChecked) {
            sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) { dateFormat ->
                dateSold =
                    converters.dateToTimestamp(dateFormat.parse(binding.propertyDateText.text.toString()))
            }

        } else if (!binding.propertySwitchSold.isChecked && propertyWithDetails.property.dateSold != null) {
            dateSold = null
        }
        isSold = binding.propertySwitchSold.isChecked
        primaryPropertyElement()
    }

    private fun PropertyEntity.propertyToInsert() {
        id = null
        sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) { dateFormat ->
            dateStartSelling =
                converters.dateToTimestamp(dateFormat.parse(binding.propertyDateText.text.toString()))
        }
        primaryPropertyElement()
    }

    private fun PropertyEntity.primaryPropertyElement() {
        sharedUtilsViewModel.getMoneyRateSelected.observe(viewLifecycleOwner) { isEuroSelected ->
            price = if (isEuroSelected) {
                sharedPropertyViewModel.convertPropertyPrice(
                    binding.propertyPrice.text.toString().toInt(), !isEuroSelected
                )
            } else {
                binding.propertyPrice.text.toString().toInt()
            }
        }
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
        lastUpdate = System.currentTimeMillis()

    }

    private fun AddressEntity.addressToUpdate(
        propertyWithDetails: PropertyWithDetails
    ) {
        id = propertyWithDetails.address?.id
        primaryAddressElement()
    }

    private fun AddressEntity.addressToInsert(insertPropertyId: Long) {
        propertyId = insertPropertyId.toInt()
        id = null
        primaryAddressElement()
    }

    private fun AddressEntity.primaryAddressElement() {
        streetNumber = binding.addressStreetNumber.text.toString()
        streetName = binding.addressStreetName.text.toString()
        city = binding.addressCity.text.toString()
        boroughs = binding.addressBoroughs.text.toString()
        zipCode = binding.addressZipCode.text.toString()
        country = binding.addressCountry.text.toString()
        apartmentDetails = binding.apartmentDetails.text.toString()
        addressString = "$streetNumber $streetName $city $zipCode $country"
    }

    private fun initDate(
        propertyWithDetails: PropertyWithDetails,
        dateFormat: SimpleDateFormat
    ) {
        if (propertyWithDetails.property.isSold == true) {
            val dateSold = propertyWithDetails.property.dateSold?.let { Date(it) }
            if (propertyWithDetails.property.dateSold == null) {
                binding.propertyDateText.text = dateFormat.format(System.currentTimeMillis())
            } else {
                binding.propertyDateText.text = "${dateSold?.let { dateFormat.format(it) }}"
            }
        } else {
            val dateSale = propertyWithDetails.property.dateStartSelling?.let { Date(it) }
            if (propertyWithDetails.property.dateStartSelling == null) {
                binding.propertyDateText.text = dateFormat.format(System.currentTimeMillis())
            } else {
                binding.propertyDateText.text = "${dateSale?.let { dateFormat.format(it) }}"
            }
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

    private fun setupRecyclerView(photoList: List<PhotoEntity>?) {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.fragmentPropertySelectedPhotosRecyclerView.layoutManager = layoutManager

        val mutablePhotoList: MutableList<PhotoEntity> =
            photoList?.toMutableList() ?: mutableListOf()

        adapter = AddAndModificationAdapter(mutablePhotoList,
            onDeletePhoto = { position ->
                // Delete photo
                lifecycleScope.launch {
                    deletePhoto(mutablePhotoList, position)
                }
            },
            onSetPrimaryPhoto = { position ->
                // Set primary photo
                lifecycleScope.launch {
                    setPrimaryPhoto(mutablePhotoList, position)
                    Toast.makeText(
                        requireContext(),
                        R.string.primary_photo_set,
                        Toast.LENGTH_SHORT
                    ).show()
                    updatePrimaryPhotoIcons(position)
                }
            }
        )
        binding.fragmentPropertySelectedPhotosRecyclerView.adapter = adapter
        binding.addPhotos.setOnClickListener {
            // Open gallery or camera to select or capture photos
            // After obtaining the selected or captured photo, add it to the adapter
            photoOptionsAndSaveAlertDialog.showPhotoOptionsDialog()

        }

        // Convert the List<PhotoEntity> to List<Drawable?>
        val drawableList: MutableList<Drawable?> = mutablePhotoList.map { photoEntity ->
            adapter.getDrawableFromPhotoEntity(requireContext(), photoEntity, isPrimary = false)
        }.toMutableList()

        // Update the adapter with the drawableList
        adapter.updatePhotos(drawableList)

        initPhotoOptionsAndSaveAlertDialog()
    }

    private fun updatePrimaryPhotoIcons(selectedPosition: Int) {
        adapter.updatePrimaryPhotoIcons(selectedPosition)
    }

    private suspend fun setPrimaryPhoto(photoList: MutableList<PhotoEntity>, position: Int) {
        if (position >= 0 && position < photoList.size) {
            val photoEntity = photoList[position]
            // Remove isPrimaryPhoto flag from other photos
            for (photo in photoList) {
                if (photo != photoEntity) {
                    sharedPropertyViewModel.updateIsPrimaryPhoto(false, photo.id ?: 0)
                }
            }
            // Update the selected photo as the primary photo
            sharedPropertyViewModel.updateIsPrimaryPhoto(true, photoEntity.id ?: 0)

            // Update the adapter with the updated list
            val drawableList = photoList.mapIndexed { index, photoEntity ->
                adapter.getDrawableFromPhotoEntity(
                    requireContext(),
                    photoEntity,
                    index == position
                )
            }
            adapter.updatePhotos(drawableList)
        } else {
            // Handle an invalid position
            Toast.makeText(requireContext(), "Invalid position", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun deletePhoto(photoList: MutableList<PhotoEntity>, position: Int) {
        val photoEntity = photoList[position]
        // Delete the photo from the database
        sharedPropertyViewModel.deletePhoto(photoEntity.id ?: 0)
        // Remove the photo from the list
        photoList.removeAt(position)
        // Update the adapter with the updated list
        adapter.updatePhotos(photoList.map {
            adapter.getDrawableFromPhotoEntity(requireContext(), it, isPrimary = false)
        })

    }


    private fun initAllEditText(propertyWithDetails: PropertyWithDetails) {
        sharedUtilsViewModel.getMoneyRateSelected.observe(viewLifecycleOwner) { isEuroSelected ->
            if (isEuroSelected == true) {
                binding.propertyPriceText.setText(R.string.price_in_euros)
                binding.propertyPrice.setText(
                    propertyWithDetails.property.price?.let { it1 ->
                        Utils.convertDollarsToEuros(it1).toString()
                    }
                )
            } else {
                binding.propertyPriceText.setText(R.string.price_in_dollars)
                propertyWithDetails.property.price?.let { binding.propertyPrice.setText(it.toString()) }
            }
        }
        propertyWithDetails.property.agentId?.let {
            sharedAgentViewModel.getAgentData(it).observe(viewLifecycleOwner) { agent ->
                agent?.let { binding.agentName.setText(agent.name) }
            }
        }
        initAllOtherPropertyEditText(propertyWithDetails)
        initAllAddressEditText(propertyWithDetails)
    }

    private fun initAllOtherPropertyEditText(propertyWithDetails: PropertyWithDetails) {
        propertyWithDetails.property.squareFeet?.let { binding.propertySquareFeet.setText(it.toString()) }
        propertyWithDetails.property.roomsCount?.let { binding.propertyRoomsCount.setText(it.toString()) }
        propertyWithDetails.property.bedroomsCount?.let { binding.propertyBedroomsCount.setText(it.toString()) }
        propertyWithDetails.property.bathroomsCount?.let { binding.propertyBathroomsCount.setText(it.toString()) }
        binding.propertyDescription.setText(propertyWithDetails.property.description)
        propertyWithDetails.property.typeOfHouse.let { binding.propertyType.setText(it) }
    }

    private fun initAllAddressEditText(propertyWithDetails: PropertyWithDetails) {
        propertyWithDetails.address?.streetNumber.let { binding.addressStreetNumber.setText(it) }
        propertyWithDetails.address?.streetName.let { binding.addressStreetName.setText(it) }
        propertyWithDetails.address?.city.let { binding.addressCity.setText(it) }
        propertyWithDetails.address?.boroughs.let { binding.addressBoroughs.setText(it) }
        propertyWithDetails.address?.zipCode.let { binding.addressZipCode.setText(it) }
        propertyWithDetails.address?.country.let { binding.addressCountry.setText(it) }
        propertyWithDetails.address?.apartmentDetails.let { binding.apartmentDetails.setText(it) }
    }

    private fun getLocationFromAddress(address: String): LatLng? {
        geocoder = Geocoder(requireContext())
        val addressList = geocoder.getFromLocationName(address, 1)
        if (addressList != null) {
            if (addressList.isNotEmpty()) {
                val latitude = addressList[0].latitude
                val longitude = addressList[0].longitude
                return LatLng(latitude, longitude)
            }
        }
        return null
    }

    private fun requiredAllValidateInputsOk(): Boolean {
        // Define a list of input field names and corresponding input values
        val inputsRequiredToValidate = listOf(
            "type of house" to binding.propertyType.text.toString(),
            "price" to binding.propertyPrice.text.toString(),
            "square feet" to binding.propertySquareFeet.text.toString(),
            "number of rooms" to binding.propertyRoomsCount.text.toString(),
            "description" to binding.propertyDescription.text.toString(),
            "street number" to binding.addressStreetNumber.text.toString(),
            "street name" to binding.addressStreetName.text.toString(),
            "city" to binding.addressCity.text.toString(),
            "zip code" to binding.addressZipCode.text.toString(),
            "country" to binding.addressCountry.text.toString(),
            "agent name" to binding.agentName.text.toString(),
            "sold date" to binding.propertyDateText.text.toString(),
            "date selling if sold" to binding.dateSale.text.toString()
        )

        // Iterate over the list of inputs
        for (input in inputsRequiredToValidate) {
            // Check if the input value is blank
            if (input.second.isBlank()) {
                // Display a toast message indicating the missing input field
                Toast.makeText(
                    requireContext(),
                    "Please enter the ${input.first}",
                    Toast.LENGTH_SHORT
                ).show()

                // Return false as at least one input is missing
                return false
            }
        }
        // If all inputs are filled, return true
        return true
    }

    private suspend fun isPrimaryPhotoSelected(propertyId: Int?): Boolean {
        val photos: List<PhotoEntity>? = if (propertyId == null) {
            sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(null)
        } else {
            sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(propertyId)
        }
        return photos?.any { it.isPrimaryPhoto } ?: false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Photo captured from the camera
                    photoOptionsAndSaveAlertDialog.initImageCapture(data)
                }
                REQUEST_IMAGE_PICK -> {
                    // Photo selected from the gallery
                    photoOptionsAndSaveAlertDialog.initImagePick(data)
                }
            }
        }
    }

    private fun isDualPanel(): Boolean {
        return activity?.resources?.getBoolean(R.bool.isTwoPanel) == true
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }

}