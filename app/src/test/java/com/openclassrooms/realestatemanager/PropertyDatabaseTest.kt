package com.openclassrooms.realestatemanager

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import assertk.assertThat
import assertk.assertions.contains
import com.openclassrooms.realestatemanager.data.dao.AddressDao
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PhotoEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(AndroidJUnit4::class)
@Config(manifest= Config.NONE)
class PropertyDatabaseTest : TestCase() {
    private lateinit var database: PropertyDatabase
    private lateinit var propertyDao: PropertyDao
    private lateinit var addressDao: AddressDao
    private lateinit var photoDao: PhotoDao
    private lateinit var agentDao: AgentDao
    private lateinit var context: Context

    // Override function setUp() to create an in-memory database
    @Before
    public override fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, PropertyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        propertyDao = database.propertyDao()
        addressDao = database.addressDao()
        photoDao = database.photoDao()
        agentDao = database.agentDao()
    }

    // Override function closeDB() to close the database when all tests are done
    @After
    fun closeDB() {
        database.close()
    }

    // create a test function and annotate it with @Test
    // here we are first adding an item to the db and then checking if that item
    // is present in the db -- if the item is present then our test cases pass with the contains assertion else it fails for each dao's
    @Test
    fun writeAndReadDao() = runBlocking {
        // For property
        val property = PropertyEntity(0, 1000)
        // insert property
        propertyDao.insert(property)
        // get all properties as list of property
        val properties = propertyDao.getAllProperties().first()
        // check if properties contains property
        assertThat(properties).contains(property)

        // For address
        val address = AddressEntity(0, "streetNumber", "streetName", "city", "borough", "postalCode", "USA", 0, "", 0.0, 0.0)
        // insert address
        addressDao.insert(address)
        // get all addresses as list of address
        val addresses = addressDao.getAllAddress().first()
        // check if addresses contains address
        assertThat(addresses).contains(address)

        // For photo
        val photo = PhotoEntity(0, "123", "description", 0, true)
        // insert photo
        photoDao.insert(photo)
        // get all photos as list of photo
        val photos = photoDao.getAllPhotos().first()
        // check if photos contains photo
        assertThat(photos).contains(photo)

        // For agent
        val agent = AgentEntity(0, "name")
        // insert agent
        agentDao.insert(agent)
        // get all agents as list of agent
        val agents = agentDao.getAllAgents().first()
        // check if agents contains agent
        assertThat(agents).contains(agent)
    }


}

