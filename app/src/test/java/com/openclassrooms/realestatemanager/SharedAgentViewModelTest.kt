package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.LocationDetails
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.ui.LocationLiveData
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class SharedAgentViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var locationLiveData: LocationLiveData
    private lateinit var viewModel: SharedAgentViewModel
    private lateinit var repository: AgentRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        val applicationMock = mockk<MainApplication>()
        every { applicationMock.applicationContext } returns mockk()
        repository = mockk()

        // Initialize the ViewModel with the mocked LocationLiveData
        viewModel = SharedAgentViewModel(repository, applicationMock)

    }

    @Test
    fun testGetLocationLiveData() {
        // Create a mock instance of LiveData
        val expectedLiveData = mockk<LiveData<LocationDetails>>()

        // Assign the mocked LiveData to locationLiveData
        every { locationLiveData.value } returns expectedLiveData.value

        // Call the method in the ViewModel
        val resultLiveData = viewModel.getLocationLiveData()

        // Verify that the expected LiveData is returned
        assertEquals(expectedLiveData, resultLiveData)
    }

    //    @Test
//    fun testStartLocationUpdates() {
//        // Call the method in the ViewModel
//        viewModel.startLocationUpdates()
//
//        // Verify that the method in LocationLiveData is called
//        verify { locationLiveData.startLocationUpdates() }
//    }

//    @Test
//    fun testGetAgentData() {
//        val agentId = 1
//        val agent = AgentEntity(agentId, "John Doe")
//
//        coEvery { repository.getAgentData(agentId) } returns flowOf(agent)
//
//        val observer = Observer<AgentEntity> {}
//        viewModel.getAgentData(agentId).observeForever(observer)
//
//        runBlocking {
//            val result = viewModel.getAgentData(agentId).value
//            assertEquals(agent, result)
//        }
//    }

//    @Test
//    fun testInsertAgent() {
//        val agent = AgentEntity(1, "John Doe")
//        val expectedId = 1L
//
//        coEvery { runBlocking { repository.insert(agent) } } returns expectedId
//
//        runBlocking {
//            val result = viewModel.insertAgent(agent)
//            assertEquals(expectedId, result)
//        }
//    }
//
//    @Test
//    fun testGetAgentByName() = runBlockingTest {
//        // Create mock objects
//        val agentName = "John Doe"
//        val agent = AgentEntity(1, agentName)
//        val repository = mockk<AgentRepository>()
//
//        // Mock repository response
//        coEvery { repository.getAgentByName(agentName) } returns flow { emit(agent) }
//
//        // Create the view model
//        val viewModel = SharedAgentViewModel(repository, mockk())
//
//        // Collect the flow using launch
//        val result = mutableListOf<AgentEntity?>()
//        val job = launch {
//            viewModel.getAgentByName(agentName).collect {
//                result.add(it)
//            }
//        }
//
//        // Cancel the job after collecting the flow
//        job.cancel()
//
//        // Verify the result
//        assertEquals(listOf(agent), result)
//    }



}

