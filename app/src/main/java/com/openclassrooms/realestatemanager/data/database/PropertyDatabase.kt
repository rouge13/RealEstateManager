package com.openclassrooms.realestatemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Database(entities = [PropertyEntity::class, AgentEntity::class], version = 2, exportSchema = false)
abstract class PropertyDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao
    abstract fun agentDao(): AgentDao

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
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(instance.propertyDao(), instance.agentDao())
                }

                instance
            }
        }

        private suspend fun prepopulateDatabase(propertyDao: PropertyDao, agentDao: AgentDao) {
            // Add agents
            FixturesDatas.AGENT_LIST.forEach { agent ->
                agentDao.insert(agent)
            }
            // Add properties
            FixturesDatas.PROPERTY_LIST.forEach { property ->
                propertyDao.insert(property)
            }
        }
    }
}