package com.openclassrooms.realestatemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.data.dao.AddressDao
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.dao.PhotoDao
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.AddressEntity
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import java.util.concurrent.Executors

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Database(entities = [PropertyEntity::class, AgentEntity::class, AddressEntity::class, ProximityEntity::class], version = 9, exportSchema = false)
abstract class PropertyDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun agentDao(): AgentDao
    abstract fun addressDao(): AddressDao
    abstract fun proximityDao(): ProximityDao
    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        fun getDatabase(context: Context): PropertyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PropertyDatabase::class.java,
                    "property_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                // Prepopulate the database directly after building the instance
                Executors.newSingleThreadExecutor().execute {
                    prepopulateDatabase(instance.propertyDao(), instance.agentDao(), instance.addressDao(), instance.proximityDao(), instance.photoDao())
                }
                instance
            }
        }

        private fun prepopulateDatabase(propertyDao: PropertyDao, agentDao: AgentDao, addressDao: AddressDao, proximityDao: ProximityDao, photoDao: PhotoDao) {
            // Add agents
            FixturesDatas.AGENT_LIST.forEach { agent ->
                agentDao.insert(agent)
            }
            // Add properties
            FixturesDatas.PROPERTY_LIST.forEach { property ->
                propertyDao.insert(property)
            }
            // Add addresses
            FixturesDatas.PROPERTY_ADDRESS_LIST.forEach { address ->
                addressDao.insert(address)
            }
            // Add proximities
            FixturesDatas.PROPERTY_PROXIMITY_LIST.forEach { proximity ->
                proximityDao.insert(proximity)
            }
            // Add photos
            FixturesDatas.PROPERTY_PHOTO_LIST.forEach { photo ->
                photoDao.insert(photo)
            }
        }
    }
}