package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.data.converter.Converters
import com.openclassrooms.realestatemanager.data.gathering.PropertyWithDetails
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import com.openclassrooms.realestatemanager.data.model.SearchCriteria
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedPropertyViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@LooperMode(LooperMode.Mode.PAUSED)
class SharedPropertyViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private val propertyRepository: PropertyRepository = mockk(relaxed = true)
    private val addressRepository: AddressRepository = mockk(relaxed = true)
    private val photoRepository: PhotoRepository = mockk(relaxed = true)
    private lateinit var viewModel: SharedPropertyViewModel
    private lateinit var propertyObserver: Observer<PropertyWithDetails?>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(propertyRepository, addressRepository, photoRepository)
        viewModel = SharedPropertyViewModel(propertyRepository, addressRepository, photoRepository)
        propertyObserver = mockk(relaxed = true)
    }

    // @Test
    // fun `Given a property id, when get property, then return property`()

    @Test
    fun testSetSelectProperty() {
        val property = PropertyWithDetails(
            PropertyEntity(id = PROPERTY_ID),
            AddressEntity(),
            listOf(PhotoEntity())
        )
        viewModel.getSelectedProperty.observeForever(propertyObserver)
        viewModel.setSelectProperty(property)

        // Verify that LiveData is updated with the correct value
        verify { propertyObserver.onChanged(property) }

        // Verify that LiveData is updated with the correct value only once
        verify(exactly = 1) { propertyObserver.onChanged(property) }

        // Verify that LiveData is equals to the propertyId value
        assertEquals(viewModel.getSelectedProperty.value?.property?.id, PROPERTY_ID)
    }

    @Test
    fun testSetSearchCriteria() = runTest {
        val searchCriteria = SearchCriteria(
            selectedAgentsIdsForQuery = listOf(AGENT_ID),
            selectedTypeOfHouseForQuery = listOf(TYPE),
            selectedBoroughsForQuery = listOf(BOROUGHS),
            selectedCitiesForQuery = listOf(CITY),
            selectedMinPriceForQuery = MIN_PRICE,
            selectedMaxPriceForQuery = MAX_PRICE,
            selectedMinSquareFeetForQuery = MIN_SQUARE,
            selectedMaxSquareFeetForQuery = MAX_SQUARE,
            selectedMinCountRoomsForQuery = MIN_ROOMS,
            selectedMaxCountRoomsForQuery = MAX_ROOMS,
            selectedMinCountBedroomsForQuery = MIN_BEDROOMS,
            selectedMaxCountBedroomsForQuery = MAX_BEDROOMS,
            selectedMinCountBathroomsForQuery = MIN_BATHROOMS,
            selectedMaxCountBathroomsForQuery = MAX_BATHROOMS,
            selectedMinCountPhotosForQuery = MIN_PHOTOS,
            selectedMaxCountPhotosForQuery = MAX_PHOTOS,
            selectedStartDateForQuery = MIN_SALE_DATE,
            selectedEndDateForQuery = MAX_SALE_DATE,
            selectedIsSoldForQuery = IS_SOLD,
            selectedSchoolProximityQuery = SCHOOL_PROXIMITY,
            selectedShopProximityQuery = SHOP_PROXIMITY,
            selectedParkProximityQuery = PARK_PROXIMITY,
            selectedRestaurantProximityQuery = RESTAURANT_PROXIMITY,
            selectedPublicTransportProximityQuery = PUBLIC_TRANSPORT_PROXIMITY
        )

        // Set the search criteria
        viewModel.setSearchCriteria(searchCriteria)

        // After setting the new search criteria, the previous search criteria should be null
        // as it was initially null and no other search criteria has been set before this
        assertNull(viewModel.previousSearchCriteria.value)

        // Is every values correct to return a property with all informations
        isEveryValuesCorrespondingToSearchCriteria(searchCriteria)

        // The current search criteria should be the one that was just set
        assertEquals(searchCriteria, viewModel.searchCriteria.value)

        // As another test, we could set another search criteria and check that
        // previousSearchCriteria now contains the old search criteria
        val newSearchCriteria = SearchCriteria() // New criteria
        viewModel.setSearchCriteria(newSearchCriteria)

        assertEquals(searchCriteria, viewModel.previousSearchCriteria.value)
        assertEquals(newSearchCriteria, viewModel.searchCriteria.value)
    }

    private fun isEveryValuesCorrespondingToSearchCriteria(searchCriteria: SearchCriteria) {
        assertEquals(searchCriteria.selectedAgentsIdsForQuery, listOf(PROPERTY_AGENT_ID))
        assertEquals(searchCriteria.selectedTypeOfHouseForQuery, listOf(PROPERTY_TYPE))
        assertEquals(searchCriteria.selectedBoroughsForQuery, listOf(PROPERTY_BOROUGH))
        assertEquals(searchCriteria.selectedCitiesForQuery, listOf(PROPERTY_CITY))
        assertTrue(searchCriteria.selectedMinPriceForQuery!! < PROPERTY_PRICE && searchCriteria.selectedMaxPriceForQuery!! > PROPERTY_PRICE)
        assertTrue(searchCriteria.selectedMinSquareFeetForQuery!! < PROPERTY_SQUARE_FEET && searchCriteria.selectedMaxSquareFeetForQuery!! > PROPERTY_SQUARE_FEET)
        assertTrue(searchCriteria.selectedMinCountRoomsForQuery!! < PROPERTY_NUMBER_OF_ROOMS && searchCriteria.selectedMaxCountRoomsForQuery!! > PROPERTY_NUMBER_OF_ROOMS)
        assertTrue(searchCriteria.selectedMinCountBedroomsForQuery!! < PROPERTY_NUMBER_OF_BEDROOMS && searchCriteria.selectedMaxCountBedroomsForQuery!! > PROPERTY_NUMBER_OF_BEDROOMS)
        assertTrue(searchCriteria.selectedMinCountBathroomsForQuery!! < PROPERTY_NUMBER_OF_BATHROOMS && searchCriteria.selectedMaxCountBathroomsForQuery!! > PROPERTY_NUMBER_OF_BATHROOMS)
        assertTrue(searchCriteria.selectedMinCountPhotosForQuery!! < PROPERTY_NUMBER_OF_PHOTOS && searchCriteria.selectedMaxCountPhotosForQuery!! > PROPERTY_NUMBER_OF_PHOTOS)
        assertTrue((searchCriteria.selectedStartDateForQuery!! < PROPERTY_SALE_DATE!!) && (searchCriteria.selectedEndDateForQuery!! > PROPERTY_SALE_DATE))
        assertEquals(searchCriteria.selectedIsSoldForQuery, PROPERTY_IS_SOLD)
        assertEquals(searchCriteria.selectedSchoolProximityQuery, PROPERTY_SCHOOL_PROXIMITY)
        assertEquals(searchCriteria.selectedShopProximityQuery, PROPERTY_SHOP_PROXIMITY)
        assertEquals(searchCriteria.selectedParkProximityQuery, PROPERTY_PARK_PROXIMITY)
        assertEquals(searchCriteria.selectedRestaurantProximityQuery, PROPERTY_RESTAURANT_PROXIMITY)
        assertEquals(
            searchCriteria.selectedPublicTransportProximityQuery,
            PROPERTY_PUBLIC_TRANSPORT_PROXIMITY
        )
    }

    @Test
    fun testUpdateProperty() = runTest {
        val property = PropertyEntity(
            id = PROPERTY_ID,
            price = PROPERTY_PRICE,
            squareFeet = PROPERTY_SQUARE_FEET,
            roomsCount = PROPERTY_NUMBER_OF_ROOMS,
            bedroomsCount = PROPERTY_NUMBER_OF_BEDROOMS,
            bathroomsCount = PROPERTY_NUMBER_OF_BATHROOMS,
            description = PROPERTY_DESCRIPTION,
            typeOfHouse = PROPERTY_TYPE,
            isSold = PROPERTY_IS_SOLD,
            dateStartSelling = PROPERTY_SALE_DATE,
            dateSold = PROPERTY_SOLD_DATE,
            agentId = PROPERTY_AGENT_ID,
            primaryPhoto = PROPERTY_PRIMARY_PHOTO_VALUE,
            schoolProximity = PROPERTY_SCHOOL_PROXIMITY,
            parkProximity = PROPERTY_PARK_PROXIMITY,
            shoppingProximity = PROPERTY_SHOP_PROXIMITY,
            restaurantProximity = PROPERTY_RESTAURANT_PROXIMITY,
            publicTransportProximity = PROPERTY_PUBLIC_TRANSPORT_PROXIMITY,
            lastUpdate = PROPERTY_LAST_UPDATE,
        )
        coEvery { propertyRepository.update(property) } just runs

        viewModel.updateProperty(property)

        // Verify that the property is updated exactly one time
        coVerify(exactly = 1) { propertyRepository.update(property) }

//        // Verify if the property expected is the same as the one that was updated
//        assertEquals(property, viewModel.getPropertiesWithDetails  .value)

    }

    @Test
    fun testUpdateAddress() = runTest {
        val address = AddressEntity(
            id = PROPERTY_ID,
            streetNumber = PROPERTY_STREET_NUMBER,
            streetName = PROPERTY_STREET_NAME,
            city = PROPERTY_CITY,
            boroughs = PROPERTY_BOROUGH,
            zipCode = PROPERTY_POSTAL_CODE,
            country = PROPERTY_COUNTRY,
            latitude = PROPERTY_LATITUDE,
            longitude = PROPERTY_LONGITUDE,
            propertyId = PROPERTY_ID
        )
        coEvery { addressRepository.updateAddress(address) } just runs

        viewModel.updateAddress(address)

        // Verify that the address is updated exactly one time
        coVerify(exactly = 1) { addressRepository.updateAddress(address) }

//        // Verify if the address expected is the same as the one that was updated
//        assertEquals(address, viewModel.getAddressWithDetails.value)
    }


    companion object {
        // Convert date to Long
        private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        private val converters = Converters()

        // Property ID
        private const val PROPERTY_ID = 1

        // Property to find by SearchCriteria and all property values
        private const val PROPERTY_PRIMARY_PHOTO_VALUE = "ic_house_classic1"
        private const val PROPERTY_AGENT_ID = 0
        private const val PROPERTY_TYPE = "House"
        private const val PROPERTY_BOROUGH = "Queens"
        private const val PROPERTY_CITY = "NEW YORK"
        private const val PROPERTY_DESCRIPTION =
            "Must add description for this property in later time"
        private const val PROPERTY_PRICE = 100000
        private const val PROPERTY_SQUARE_FEET = 100
        private const val PROPERTY_NUMBER_OF_ROOMS = 8
        private const val PROPERTY_NUMBER_OF_BEDROOMS = 4
        private const val PROPERTY_NUMBER_OF_BATHROOMS = 2
        private const val PROPERTY_NUMBER_OF_PHOTOS = 2
        private val PROPERTY_SALE_DATE = converters.dateToTimestamp(dateFormat.parse("2023/04/11"))
        private val PROPERTY_SOLD_DATE = null
        private const val PROPERTY_IS_SOLD = false
        private const val PROPERTY_SCHOOL_PROXIMITY = true
        private const val PROPERTY_SHOP_PROXIMITY = true
        private const val PROPERTY_PARK_PROXIMITY = true
        private const val PROPERTY_RESTAURANT_PROXIMITY = false
        private const val PROPERTY_PUBLIC_TRANSPORT_PROXIMITY = false
        private val PROPERTY_LAST_UPDATE = System.currentTimeMillis()
        private const val PROPERTY_STREET_NUMBER = "123"
        private const val PROPERTY_STREET_NAME = "Main Street"
        private const val PROPERTY_POSTAL_CODE = "12345"
        private const val PROPERTY_COUNTRY = "USA"
        private const val PROPERTY_LATITUDE = 40.7128
        private const val PROPERTY_LONGITUDE = 74.0060


        // SearchCriteriaValues
        private const val MIN_PRICE = 0
        private const val MAX_PRICE = 1000000
        private const val MIN_SQUARE = 0
        private const val MAX_SQUARE = 1000
        private const val MIN_ROOMS = 0
        private const val MAX_ROOMS = 10
        private const val MIN_BATHROOMS = 0
        private const val MAX_BATHROOMS = 10
        private const val MIN_BEDROOMS = 0
        private const val MAX_BEDROOMS = 10
        private const val MIN_PHOTOS = 0
        private const val MAX_PHOTOS = 10
        private val MIN_SALE_DATE = converters.dateToTimestamp(dateFormat.parse("2023/04/10"))
        private val MAX_SALE_DATE = converters.dateToTimestamp(dateFormat.parse("2023/04/12"))
        private const val TYPE = "House"
        private const val CITY = "NEW YORK"
        private const val SCHOOL_PROXIMITY = true
        private const val PARK_PROXIMITY = true
        private const val SHOP_PROXIMITY = true
        private const val RESTAURANT_PROXIMITY = false
        private const val PUBLIC_TRANSPORT_PROXIMITY = false
        private const val IS_SOLD = false
        private const val AGENT_ID = 0
        private const val BOROUGHS = "Queens"


    }
}

