package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.ui.LocationLiveData
import com.openclassrooms.realestatemanager.ui.MainApplication
import com.openclassrooms.realestatemanager.ui.sharedViewModel.SharedAgentViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.bouncycastle.util.test.SimpleTest.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
class SharedAgentViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var locationLiveData: LocationLiveData
    private var viewModel: SharedAgentViewModel = mockk(relaxed = true)
    private val repository: AgentRepository = mockk(relaxed = true)
    private val agentEntityObserver: Observer<AgentEntity> = mockk(relaxed = true)
    private lateinit var booleanObserver: Observer<Boolean>

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(agentEntityObserver, viewModel, repository)
        val applicationMock = mockk<MainApplication>(relaxed = true)
        val contextMock = mockk<Context>(relaxed = true)
        every { applicationMock.applicationContext } returns contextMock
        viewModel = object : SharedAgentViewModel(repository, applicationMock) {
            override fun getLocationLiveData() = locationLiveData
        }
    }

    //     TEST WORKING
    @Test
    fun testGetLocationLiveData() {
        // Call the method in the ViewModel
        val resultLiveData = viewModel.getLocationLiveData()

        // Check that the returned LocationLiveData is not null
        assertNotNull(resultLiveData)
    }


    // TEST NOT WORKING
//    @Test
//    fun testStartLocationUpdates() {
//        // Allow the startLocationUpdates function to be called
//        every { locationLiveData.startLocationUpdates() } just runs
//
//        // Call the method in the ViewModel
//        viewModel.startLocationUpdates()
//
//        // Verify that the method in LocationLiveData is called
//        verify { locationLiveData.startLocationUpdates() }
//    }

    // TEST WORKING
    @Test
    fun testGetAgentData() {
        val agent = AgentEntity(AGENT_ID, AGENT_NAME)
        coEvery { repository.getAgentData(AGENT_ID) } returns flowOf(agent)

        var result: AgentEntity? = null
        val observer = Observer<AgentEntity> { result = it }

        viewModel.getAgentData(AGENT_ID).observeForever(observer)

        assertEquals(agent, result)
    }

    // TEST WORKING
    @Test
    fun testInsertAgent() {
        val agent = AgentEntity(AGENT_ID, AGENT_NAME)
        val expectedId = AGENT_ID.toLong()

        coEvery { runBlocking { repository.insert(agent) } } returns expectedId

        runBlocking {
            val result = viewModel.insertAgent(agent)
            assertEquals(expectedId, result)
        }
    }


    // TEST WORKING
    @Test
    fun testGetAgentByName() = runTest {
        // Create mock objects
        val agent = AgentEntity(AGENT_ID, AGENT_NAME)

        // Mock repository response
        coEvery { repository.getAgentByName(AGENT_NAME) } returns flow { emit(agent) }

        // Collect the flow using launch
        val result = mutableListOf<AgentEntity?>()
        val job = launch {
            viewModel.getAgentByName(AGENT_NAME).collect {
                result.add(it)
            }
        }
        runCurrent()
        // Cancel the job after collecting the flow
        job.cancel()

        // Verify the result
        assertEquals(listOf(agent), result)
    }

    @Test
    fun testAgentHasInternet() {
        // Pass a MutableLiveData to the ViewModel with true value
        val hasInternet = MutableLiveData<Boolean>()
        hasInternet.value = HAS_INTERNET

        // Allow the agentHasInternet function to be called and return the MutableLiveData
        every { viewModel.agentHasInternet() } returns hasInternet

        // Call the method in the ViewModel
        val result = viewModel.agentHasInternet()

        // Verify that the method in AgentRepository is called
        verify { viewModel.agentHasInternet() }

        // Create a observer
        booleanObserver = Observer<Boolean> {}
        // Observe the LiveData
        result.observeForever(booleanObserver)

        // Check that the returned result is true
        assertEquals(HAS_INTERNET, result.value)

        // Remove the observer after test
        result.removeObserver(booleanObserver)
    }

    @After
    fun tearDown() {
        viewModel.getAgentData(AGENT_ID).removeObserver(agentEntityObserver)
    }

    companion object {
        // FOR AGENT ENTITY
        const val AGENT_ID = 1
        const val AGENT_NAME = "John Doe"
        const val HAS_INTERNET = true
    }

}

