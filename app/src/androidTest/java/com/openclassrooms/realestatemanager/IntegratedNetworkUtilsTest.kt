package com.openclassrooms.realestatemanager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.ui.utils.Utils.isInternetAvailable
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(AndroidJUnit4::class)
class IntegratedNetworkUtilsTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun testIsInternetAvailable() {
        // Get the ConnectivityManager
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Create a NetworkRequest to specify TRANSPORT_WIFI
        val networkRequestWifi = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        // Register a network callback to listen for network changes
        checkIfInternetIsAvailable(connectivityManager, networkRequestWifi)

        // Create a NetworkRequest to specify TRANSPORT_CELLULAR
        val networkRequestCellular = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        checkIfInternetIsAvailable(connectivityManager, networkRequestCellular)

        // Create a NetworkRequest to specify TRANSPORT_ETHERNET
        val networkRequestEthernet = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .build()

        checkIfInternetIsAvailable(connectivityManager, networkRequestEthernet)

        // Create a NetworkRequest without specifying any transport type to check if internet is available
        val networkRequestNoTransportType = NetworkRequest.Builder()
            .build()

        checkIfInternetIsAvailable(connectivityManager, networkRequestNoTransportType)
    }

    private fun checkIfInternetIsAvailable(
        connectivityManager: ConnectivityManager,
        networkRequest: NetworkRequest
    ) {
        connectivityManager.registerNetworkCallback(networkRequest, object :
            ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Network is available, check if internet is available
                val isInternetAvailable = isInternetAvailable(context)
                // Check if the network is not null
                assertTrue(isInternetAvailable)
                // Unregister the network callback
                connectivityManager.unregisterNetworkCallback(this)

            }
        })
    }
}



