package com.openclassrooms.realestatemanager.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.data.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.model.PropertyEntity

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@Database(entities = [PropertyEntity::class, AgentEntity::class], version = 1, exportSchema = false)
abstract class PropertyDatabase : RoomDatabase() {
    abstract fun propertyDao(): PropertyDao

    // Create the compagnion object
    companion object {
        // Create the instance
        @Volatile
        private var INSTANCE: PropertyDatabase? = null

        // Create the function to get the instance
        fun getDatabase(context: Context): PropertyDatabase {
            // Create the instance if it's null
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PropertyDatabase::class.java,
                    "property_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}