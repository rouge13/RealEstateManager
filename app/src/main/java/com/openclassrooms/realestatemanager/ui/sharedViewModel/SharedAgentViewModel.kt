package com.openclassrooms.realestatemanager.ui.sharedViewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.ui.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class SharedAgentViewModel(private val repository: AgentRepository, application: Application) : AndroidViewModel(application) {
    companion object {
        private const val LOCATION_UPDATE_INTERVAL = 10000L // 10 seconds
        private const val LOCATION_UPDATE_DISTANCE = 10f // 10 meters
    }
    // Get agent location
    private val _agentLocation = MutableLiveData<Location>().apply {
        value = Location("").apply {
            latitude = 40.7171
            longitude = -74.0064
        }
    }
    val allAgents: LiveData<List<AgentEntity>> = repository.allAgents.asLiveData()
    // Mutable LiveData to get the agent by email and password
    private var _loggedAgent = MutableLiveData<AgentEntity?>()
    val loggedAgent: LiveData<AgentEntity?> get() = _loggedAgent
    // Function to get agent by email and password
    fun agentData(email: String, password: String): LiveData<AgentEntity> {
        return repository.agentData(email, password).asLiveData()
    }
    fun setLogedAgent(agent: AgentEntity?) {
        viewModelScope.launch {
            if (agent != null) {
                _loggedAgent.postValue(agent)

            } else {
                _loggedAgent.postValue(null)
            }
        }
    }
    // Insert agent
    suspend fun insertAgent(agent: AgentEntity) {
        repository.insert(agent)
    }
    // Check if the agent exist by email
    suspend fun getAgentByEmail(email: String): AgentEntity? {
        return repository.getAgentByEmail(email)
    }
    // get agent location
    fun getAgentLocation(): LiveData<Location> {
        return _agentLocation
    }
    // Request agent location update
    fun requestAgentLocationUpdates(context: Context, lastLocation: Location?) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, LOCATION_UPDATE_DISTANCE, object : android.location.LocationListener {
                    override fun onLocationChanged(location: Location) {
                        _agentLocation.postValue(location)
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String) {
                    }

                    override fun onProviderDisabled(provider: String) {
                    }
                })
            } else if (lastLocation != null) {
                _agentLocation.postValue(lastLocation!!)
            }
        }
    }

    // Check if GPS and Network are enabled
    fun checkGpsAndNetworkEnabled(lm: LocationManager): Boolean {
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!gpsEnabled && !networkEnabled) {
            viewModelScope.launch(Dispatchers.Main) {
                initAlertDialogForGPSAndNetworkEnabled(getApplication())
            }
            return false
        }
        return true
    }
    // Initialize AlertDialog for GPS and Network services
    private fun initAlertDialogForGPSAndNetworkEnabled(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("GPS and Network services")
            setMessage("GPS and Network services are not enabled. Do you want to enable them?")
            setPositiveButton("Settings") { _, _ ->
                (context as Activity).startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }.show()
    }
}