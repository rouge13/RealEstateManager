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
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AddAndModificationFragment : Fragment() {
    private var currentDescription: String? = null
    private var photoList: List<PhotoEntity>? = null
    private lateinit var adapter: AddAndModificationAdapter
    private lateinit var binding: FragmentAddAndModifyPropertyBinding
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var geocoder: Geocoder
    private lateinit var addressString: String
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
                binding.propertyDateText.text = dateFormat.format(System.currentTimeMillis())
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
                binding.dateSale.text =
                    dateFormat.format(propertyWithDetails.property.dateStartSelling)
                initDate(propertyWithDetails)
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

        sharedPropertyViewModel.getPropertiesWithDetails.observe(viewLifecycleOwner) { propertiesWithDetails ->
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
            if (requiredAllValidateInputsOk()) {
                lifecycleScope.launch {
                    val insertedPropertyId = insertPropertyEntity()
                    if (insertedPropertyId != null) {
                        notificationHelper.showPropertyInsertedNotification()
                        findNavController().navigate(R.id.propertyListFragment)
                        Toast.makeText(requireContext(), "Property inserted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun initCancelButton() {
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressed()
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
                if (requiredAllValidateInputsOk()) {
                    lifecycleScope.launch {
                        updatePropertyEntity(propertyWithDetails)
                        updateAddressEntity(propertyWithDetails)
                        findNavController().navigate(R.id.propertyListFragment)
                        Toast.makeText(requireContext(), "Property updated", Toast.LENGTH_SHORT)
                            .show()
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
            val location = getLocationFromAddress(addressString)
            if (location != null) {
                addressEntity?.let { sharedPropertyViewModel.updateAddress(it) }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Address not found, please check the address if correct !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun insertAddressEntity(insertPropertyId: Long) {
        val addressEntity = AddressEntity()
        addressEntity.apply {
            addressToInsert(insertPropertyId)
        }
        lifecycleScope.launch {
            val location = getLocationFromAddress(addressString)
            if (location != null) {
                addressEntity.let { sharedPropertyViewModel.insertAddress(it) }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Address not found, please check the address if correct !",
                    Toast.LENGTH_SHORT
                ).show()
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
            lifecycleScope.launch {
                sharedPropertyViewModel.updateProperty(propertyEntity)
                updatePhotosWithPropertyId(propertyId = propertyEntity.id!!)
            }
        } else {
            val createdAgentId = createAgentAndWait(agentName)
            if (createdAgentId != null) {
                propertyEntity.agentId = createdAgentId
                lifecycleScope.launch {
                    sharedPropertyViewModel.updateProperty(propertyEntity)
                    updatePhotosWithPropertyId(propertyId = propertyEntity.id!!)
                }
            } else {
                // Agent creation was canceled, perform cancel actions here
                Toast.makeText(requireContext(), "Agent creation canceled", Toast.LENGTH_SHORT)
                    .show()
                // Cancel any other actions related to property update
            }
        }
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
                updatePhotosWithPropertyId(propertyId = propertyEntity.id!!)
            }
        } else {
            val createdAgentId = createAgentAndWait(agentName)
            if (createdAgentId != null) {
                propertyEntity.agentId = createdAgentId
                val insertPropertyId = sharedPropertyViewModel.insertProperty(propertyEntity)
                if (insertPropertyId != null) {
                    insertedPropertyId = insertPropertyId
                    insertAddressEntity(insertedPropertyId)
                    updatePhotosWithPropertyId(propertyId = propertyEntity.id!!)
                } else {
                    // Agent creation was canceled, perform cancel actions here
                    Toast.makeText(requireContext(), "Agent creation canceled", Toast.LENGTH_SHORT)
                        .show()
                    // Cancel any other actions related to property update
                }
            }
        }
        return insertedPropertyId
    }

    private suspend fun updatePhotosWithPropertyId(propertyId: Int) {
        val currentPendingPhotoIds = sharedPropertyViewModel.getPendingPhotoIds.value ?: emptyList()
        for (photoId in currentPendingPhotoIds) {
            sharedPropertyViewModel.updatePhotosWithPropertyId(photoId, propertyId)
        }
        sharedPropertyViewModel.pendingPhotoIds.value = emptyList()
    }

    private fun PropertyEntity.propertyToUpdate(
        propertyWithDetails: PropertyWithDetails
    ) {
        id = propertyWithDetails.property.id
        if (binding.propertySwitchSold.isChecked) {
            dateSold =
                converters.dateToTimestamp(dateFormat.parse(binding.propertyDateText.text.toString()))
        } else if (!binding.propertySwitchSold.isChecked && propertyWithDetails.property.dateSold != null) {
            dateSold = null
        }
        isSold = binding.propertySwitchSold.isChecked
        primaryPropertyElement()
    }

    private fun PropertyEntity.propertyToInsert() {
        id = null
        dateStartSelling =
            converters.dateToTimestamp(dateFormat.parse(binding.propertyDateText.text.toString()))
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
        addressString =
            binding.addressStreetNumber.text.toString() + " " + binding.addressStreetName.text.toString() + " " + binding.addressCity.text.toString() + " " + binding.addressZipCode.text.toString() + " " + binding.addressCountry.text.toString()
    }

    private fun initDate(propertyWithDetails: PropertyWithDetails) {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        if (propertyWithDetails.property.isSold == true) {
            val dateSold = propertyWithDetails.property.dateSold?.let { Date(it) }
            if (propertyWithDetails.property.dateSold == null) {
                binding.propertyDateText.text = dateFormat.format(System.currentTimeMillis())
            } else {
                binding.propertyDateText.text = "${dateSold?.let { sdf.format(it) }}"
            }
        } else {
            val dateSale = propertyWithDetails.property.dateStartSelling?.let { Date(it) }
            if (propertyWithDetails.property.dateStartSelling == null) {
                binding.propertyDateText.text = dateFormat.format(System.currentTimeMillis())
            } else {
                binding.propertyDateText.text = "${dateSale?.let { sdf.format(it) }}"
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

        val mutablePhotoList: MutableList<PhotoEntity> = photoList?.toMutableList() ?: mutableListOf()

        adapter = AddAndModificationAdapter(mutablePhotoList) { position ->
            // Delete photo
            deletePhoto(mutablePhotoList, position)
        }
        binding.fragmentPropertySelectedPhotosRecyclerView.adapter = adapter
        binding.addPhotos.setOnClickListener {
            // Open gallery or camera to select or capture photos
            // After obtaining the selected or captured photo, add it to the adapter
            showPhotoOptionsDialog()
        }

        // Convert the List<PhotoEntity> to List<Drawable?>
        val drawableList: MutableList<Drawable?> = mutablePhotoList.map { photoEntity ->
            adapter.getDrawableFromPhotoEntity(requireContext(), photoEntity)
        }.toMutableList()

        // Update the adapter with the drawableList
        adapter.updatePhotos(drawableList)
    }

    private fun initPhotoFromURI(
        photoEntity: PhotoEntity,
        drawableList: MutableList<Drawable?>
    ) {
        // Photo from URI
        val uri = Uri.parse(photoEntity.photo)
        val drawable: Drawable? = try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            Drawable.createFromStream(inputStream, uri.toString())
        } catch (e: Exception) {
            null
        }
        drawableList.add(drawable)
    }

    private fun initPhotoFromDrawableRessources(
        photoEntity: PhotoEntity,
        drawableList: MutableList<Drawable?>
    ) {
        // Photo from resources
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
        drawableList.add(drawable)
    }

    private fun deletePhoto(
        mutablePhotoList: MutableList<PhotoEntity>?,
        position: Int
    ) {
        val photo = mutablePhotoList?.get(position)
        photo?.id?.let { photoId ->
            // Delete photo from database using the DAO
            lifecycleScope.launch {
                sharedPropertyViewModel.deletePhoto(photoId)
            }
            // Remove the photo from the list and update the adapter
            mutablePhotoList.removeAt(position)
            adapter.removePhoto(position)
        }
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
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

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
        val typeOfHouse = binding.propertyType.text.toString()
        val price = binding.propertyPrice.text.toString()
        val squareFeet = binding.propertySquareFeet.text.toString()
        val roomsCount = binding.propertyRoomsCount.text.toString()
        val description = binding.propertyDescription.text.toString()
        val streetNumber = binding.addressStreetNumber.text.toString()
        val streetName = binding.addressStreetName.text.toString()
        val city = binding.addressCity.text.toString()
        val zipCode = binding.addressZipCode.text.toString()
        val country = binding.addressCountry.text.toString()
        val agentName = binding.agentName.text.toString()
        val date = binding.propertyDateText.text.toString()
        val dateSellingIfSold = binding.dateSale.text.toString()
        return isAllInputsAdded(typeOfHouse, price, squareFeet, roomsCount, description, streetNumber, streetName, city, zipCode, country, agentName, date, dateSellingIfSold)
    }

    private fun isAllInputsAdded(typeOfHouse: String, price: String, squareFeet: String, roomsCount: String, description: String, streetNumber: String, streetName: String, city: String, zipCode: String, country: String, agentName: String, date: String, dateSellingIfSold: String): Boolean {
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
                    val description = currentDescription ?: ""
                    if (description.isNotBlank()) {
                        val photoUri = data?.data
                        photoUri?.let { it ->
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

                REQUEST_IMAGE_PICK -> {
                    // Photo selected from the gallery
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
            }
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
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
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
            val photoEntity = PhotoEntity(photo = uri, description = description)
            // Save the photo entity to the Room database using your DAO
            // Replace `photoDao` with your actual DAO instance
            val photoId = sharedPropertyViewModel.insertPhoto(photoEntity)?.toInt()
            // Update the adapter with the new photo entity
            drawable?.let {
                adapter.addPhoto(photoEntity, it)
                // Scroll to the newly added photo
                binding.fragmentPropertySelectedPhotosRecyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
            // Store the photo ID in the pending list
            if (photoId != null) {
                sharedPropertyViewModel.addPendingPhotoId(photoId)
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

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }
}