package com.openclassrooms.realestatemanager.ui

import android.app.Application
import com.openclassrooms.realestatemanager.data.database.PropertyDatabase
import com.openclassrooms.realestatemanager.data.repository.AddressRepository
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.ConvertMoneyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.repository.PhotoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class MainApplication : Application() {
    private val mutex = Mutex()
    var initialized = false
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun waitForInitialization() {
        if (!initialized) {
            mutex.withLock {
                if (!initialized) {
                    // Wait for the initialization job to complete
                    initJob.join()
                    initialized = true
                }
            }
        }
    }

    private var database: PropertyDatabase? = null

    val propertyRepository: PropertyRepository?
        get() = database?.propertyDao()?.let { PropertyRepository(it) }
    val agentRepository: AgentRepository?
        get() = database?.agentDao()?.let { AgentRepository(it) }
    val addressRepository: AddressRepository?
        get() = database?.addressDao()?.let { AddressRepository(it) }
    val photoRepository: PhotoRepository?
        get() = database?.photoDao()?.let { PhotoRepository(it) }
    val convertMoneyRepository: ConvertMoneyRepository?
        get() = database?.convertMoneyDao()?.let { ConvertMoneyRepository(it) }

    private val initJob = applicationScope.launch {
        database = PropertyDatabase.getDatabase(this@MainApplication)
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
    }
}
