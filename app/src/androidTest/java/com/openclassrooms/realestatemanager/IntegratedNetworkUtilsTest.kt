package com.openclassrooms.realestatemanager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import com.openclassrooms.realestatemanager.ui.utils.Utils.isInternetAvailable
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowNetwork
import org.robolectric.shadows.ShadowNetworkCapabilities

/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(RobolectricTestRunner::class)
class IntegratedNetworkUtilsTest {

    @Test
    fun testIsInternetAvailable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities = ShadowNetworkCapabilities.newInstance()
        networkCapabilities.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)

        val network = ShadowNetwork.newInstance()
        network.setNetworkCapabilities(networkCapabilities)

        connectivityManager.bindProcessToNetwork(network)

        assertTrue(isInternetAvailable(context))

        connectivityManager.bindProcessToNetwork(null)

        assertFalse(isInternetAvailable(context))
    }
}