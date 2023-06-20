package com.openclassrooms.realestatemanager.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.dao.AgentDao
import com.openclassrooms.realestatemanager.data.model.AgentEntity
import com.openclassrooms.realestatemanager.ui.utils.Utils
import kotlinx.coroutines.flow.Flow

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
class AgentRepository(private val agentDao: AgentDao, private val context: Context) {
    // Add a callback to get isInternetAvailable value
    val isInternetAvailable: MutableLiveData<Boolean> = MutableLiveData()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            isInternetAvailable.postValue(true)
        }
        override fun onLost(network: Network) {
            isInternetAvailable.postValue(false)
        }
    }
    init {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder()
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    // make sure to unregister the callback when you're done to avoid memory leaks
    fun cleanup() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    // get all the agents from the database
    val allAgents: Flow<List<AgentEntity>> = agentDao.getAllAgents()
    // insert an agent in the database

    suspend fun insert(agent: AgentEntity): Long? {
        val id = agentDao.insert(agent)
        return if (id != -1L) id else null
    }
    // get agent by id
    fun getAgentData(agentId: Int): Flow<AgentEntity> {
        return agentDao.getAgentData(agentId)
    }
    // get agent by name
    fun getAgentByName(agentName: String): Flow<AgentEntity?> {
        return agentDao.getAgentByName(agentName)
    }

    fun getAgentHasInternet(): LiveData<Boolean> {
        return isInternetAvailable
    }

}


