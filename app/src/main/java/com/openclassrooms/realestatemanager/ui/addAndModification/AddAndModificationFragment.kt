package com.openclassrooms.realestatemanager.ui.addAndModification

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.converter.Converters
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import java.util.TimeZone
import com.openclassrooms.realestatemanager.data.notification.NotificationHelper
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedUtilsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationFragment : Fragment() {
    private var currentDescription: String? = null
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
                        binding.dateSale.text = it.format(propertyWithDetails.property.dateStartSelling)
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
                        Toast.makeText(requireContext(), getString(R.string.agent_name_empty), Toast.LENGTH_SHORT).show()
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
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, zipCode)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initCities(cities: List<String>) {
        val autoCompleteTextView = binding.addressCity
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, cities)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initBoroughs(boroughs: List<String>) {
        val autoCompleteTextView = binding.addressBoroughs
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, boroughs)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initTypesOfHouse(typesOfHouse: List<String>) {
        val autoCompleteTextView = binding.propertyType
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                typesOfHouse
            )
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    private fun initAgentNames(agentsNames: List<String>) {
        val autoCompleteTextView = binding.agentName
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, agentsNames)
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
                        Toast.makeText(requireContext(), "Property updated.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Please select a primary photo.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Address not found, please check the address if correct !", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Address not found, please check the address if correct !", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Agent creation canceled", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Agent creation canceled", Toast.LENGTH_SHORT).show()
                    // Cancel any other actions related to property update
                    return null
                }
            }
        }
        updatePhotosWithPropertyId(propertyId = propertyEntity.id!!)
        return insertedPropertyId
    }

    private suspend fun updatePhotosWithPropertyId(propertyId: Int?) {
        val photosWithNullPropertyId =
            sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(null)
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

            val photosWithPropertyId =
                sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(propertyId)
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
            if (isPrimaryPhotoSet && primaryPhotoUri != null) {
                sharedPropertyViewModel.updatePrimaryPhoto(propertyId, primaryPhotoUri!!)
            } else {
                // Display a message to ask the agent to select a primary photo
                Toast.makeText(
                    requireContext(),
                    "Please select a primary photo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun PropertyEntity.propertyToUpdate(
        propertyWithDetails: PropertyWithDetails
    ) {
        id = propertyWithDetails.property.id
        if (binding.propertySwitchSold.isChecked) {
            sharedUtilsViewModel.getDateFormatSelected.observe(viewLifecycleOwner) { dateFormat ->
                dateSold = converters.dateToTimestamp(dateFormat.parse(binding.propertyDateText.text.toString()))
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
            dateStartSelling = converters.dateToTimestamp(dateFormat.parse(binding.propertyDateText.text.toString()))
        }
        primaryPropertyElement()
    }

    private fun PropertyEntity.primaryPropertyElement() {
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

    private fun initDate(propertyWithDetails: PropertyWithDetails, dateFormat: SimpleDateFormat) {
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
                    Toast.makeText(requireContext(), R.string.primary_photo_set, Toast.LENGTH_SHORT).show()
                    updatePrimaryPhotoIcons(position)
                }
            }
        )
        binding.fragmentPropertySelectedPhotosRecyclerView.adapter = adapter
        binding.addPhotos.setOnClickListener {
            // Open gallery or camera to select or capture photos
            // After obtaining the selected or captured photo, add it to the adapter
            showPhotoOptionsDialog()
        }

        // Convert the List<PhotoEntity> to List<Drawable?>
        val drawableList: MutableList<Drawable?> = mutablePhotoList.map { photoEntity ->
            adapter.getDrawableFromPhotoEntity(requireContext(), photoEntity, isPrimary = false)
        }.toMutableList()

        // Update the adapter with the drawableList
        adapter.updatePhotos(drawableList)
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
                adapter.getDrawableFromPhotoEntity(requireContext(), photoEntity, index == position)
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

    private suspend fun insertPhoto(photoEntity: PhotoEntity): Long? {
        return sharedPropertyViewModel.insertPhoto(photoEntity)
    }

    private fun showPhotoOptionsDialog() {
        val descriptionEditText = EditText(requireContext())
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle("Add Photo description")
            .setView(descriptionEditText)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val descriptionDialog = alertDialogBuilder.create()
        descriptionDialog.setOnShowListener {
            val positiveButton = descriptionDialog.getButton(DialogInterface.BUTTON_POSITIVE)
            positiveButton.isEnabled = false
            descriptionEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    positiveButton.isEnabled = !s.isNullOrBlank()
                }
            })
        }

        descriptionDialog.setButton(
            DialogInterface.BUTTON_POSITIVE, "Save"
        ) { dialog, _ ->
            val description = descriptionEditText.text.toString()
            if (!description.isBlank()) {
                savePhotoWithDescription(description)
            }
            dialog.dismiss()
        }

        descriptionDialog.show()
    }

    private fun savePhotoWithDescription(description: String) {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        currentDescription = description
        AlertDialog.Builder(requireContext())
            .setTitle("Add Photo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> takePhotoFromCamera()
                    1 -> choosePhotoFromGallery()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun takePhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK)
    }

    private fun initAllEditText(propertyWithDetails: PropertyWithDetails) {
        propertyWithDetails.property.price?.let { binding.propertyPrice.setText(it.toString()) }
        propertyWithDetails.property.squareFeet?.let { binding.propertySquareFeet.setText(it.toString()) }
        propertyWithDetails.property.roomsCount?.let { binding.propertyRoomsCount.setText(it.toString()) }
        propertyWithDetails.property.bedroomsCount?.let { binding.propertyBedroomsCount.setText(it.toString()) }
        propertyWithDetails.property.bathroomsCount?.let { binding.propertyBathroomsCount.setText(it.toString()) }
        binding.propertyDescription.setText(propertyWithDetails.property.description)
        propertyWithDetails.property.agentId?.let {
            sharedAgentViewModel.getAgentData(it).observe(viewLifecycleOwner) { agent ->
                agent?.let {
                    binding.agentName.setText(agent.name)
                }
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
        return isAllInputsAdded(
            typeOfHouse = binding.propertyType.text.toString(),
            price = binding.propertyPrice.text.toString(),
            squareFeet = binding.propertySquareFeet.text.toString(),
            roomsCount = binding.propertyRoomsCount.text.toString(),
            description = binding.propertyDescription.text.toString(),
            streetNumber = binding.addressStreetNumber.text.toString(),
            streetName = binding.addressStreetName.text.toString(),
            city = binding.addressCity.text.toString(),
            zipCode = binding.addressZipCode.text.toString(),
            country = binding.addressCountry.text.toString(),
            agentName = binding.agentName.text.toString(),
            date = binding.propertyDateText.text.toString(),
            dateSellingIfSold = binding.dateSale.text.toString()
        )
    }

    private fun isAllInputsAdded(
        typeOfHouse: String,
        price: String,
        squareFeet: String,
        roomsCount: String,
        description: String,
        streetNumber: String,
        streetName: String,
        city: String,
        zipCode: String,
        country: String,
        agentName: String,
        date: String,
        dateSellingIfSold: String
    ): Boolean {
        if (typeOfHouse.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the type of house", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (price.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the price", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (squareFeet.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the square feet", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (roomsCount.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the number of rooms", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (description.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the description", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (streetNumber.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the street number", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (streetName.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the street name", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (city.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the city", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (zipCode.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the zip code", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (country.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the country", Toast.LENGTH_SHORT)
                .show(); return false
        }
        if (date.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the sold date", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (agentName.isBlank()) {
            Toast.makeText(requireContext(), "Please enter the agent name", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        if (dateSellingIfSold.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Please enter the date selling if sold",
                Toast.LENGTH_SHORT
            )
                .show()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // Photo captured from the camera
                    initImageCapture(data)
                }

                REQUEST_IMAGE_PICK -> {
                    // Photo selected from the gallery
                    initImagePick(data)
                }
            }
        }
    }

    private fun initImageCapture(data: Intent?) {
        val description = currentDescription ?: ""
        if (description.isNotBlank()) {
            val photoBitmap = data?.extras?.get("data") as? Bitmap
            photoBitmap?.let { bitmap ->
                val uriString = saveImageToGallery(bitmap.toDrawable(resources))
                uriString?.let { saveUriToDatabase(it, description) }
            }
            currentDescription = null
        }
    }

    private fun initImagePick(data: Intent?) {
        val description = currentDescription ?: ""
        if (description.isNotBlank()) {
            val uri = data?.data
            uri?.let { it ->
                val drawable = Drawable.createFromStream(
                    requireContext().contentResolver.openInputStream(it),
                    it.toString()
                )
                val uriString = drawable?.let { it1 -> saveImageToGallery(it1) }
                uriString?.let { saveUriToDatabase(it, description) }
            }
            currentDescription = null
        }
    }

    private fun saveImageToGallery(drawable: Drawable): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val resolver = requireContext().contentResolver
        var imageUri: String? = null

        try {
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?.let { uri ->
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        drawable.toBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        imageUri = uri.toString()
                    }
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageUri
    }

    private fun saveUriToDatabase(uri: String, description: String) {
        lifecycleScope.launch {
            val drawable = getDrawableFromUri(uri)
            val photoEntity = PhotoEntity(photoURI = uri, description = description)
            val insertedId = insertPhoto(photoEntity)?.toInt()
            if (insertedId != null) {
                photoEntity.id = insertedId
                // Update the adapter with the new photo entity and drawable
                drawable?.let {
                    adapter.addPhoto(photoEntity, it)
                }
                // Scroll to the newly added photo
                binding.fragmentPropertySelectedPhotosRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            } else {
                // Handle the case where photo insertion fails
                Toast.makeText(requireContext(), "Failed to insert photo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getDrawableFromUri(uri: String): Drawable? {
        return withContext(Dispatchers.Main) {
            val context = requireContext()
            val inputStream = context.contentResolver.openInputStream(Uri.parse(uri))
            inputStream?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                BitmapDrawable(context.resources, bitmap)
            }
        }
    }

    private suspend fun isPrimaryPhotoSelected(propertyId: Int?): Boolean {
        val photos: List<PhotoEntity>? = if (propertyId == null) {
            sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(null)
        } else {
            sharedPropertyViewModel.getAllPhotosRelatedToSetThePropertyId(propertyId)
        }
        return photos?.any { it.isPrimaryPhoto } ?: false
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }
}