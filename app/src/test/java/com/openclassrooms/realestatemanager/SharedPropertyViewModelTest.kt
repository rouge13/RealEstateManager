package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
import io.mockk.MockKMatcherScope
import io.mockk.MockKStubScope
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

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
    fun testSetSearchCriteria(){
        val searchCriteria = SearchCriteria()
        viewModel.setSearchCriteria(searchCriteria)

        // Verify that the LiveData is updated with the correct value
        assertNull(viewModel.previousSearchCriteria.value)

        // Verify that the LiveData is updated with the correct value only once
        verify { propertyObserver.onChanged(searchCriteria) }
    }








    companion object {
        private const val PROPERTY_ID = 1

    }
}

