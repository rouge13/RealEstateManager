package com.openclassrooms.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.openclassrooms.realestatemanager.ui.utils.Utils.isInternetAvailable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import org.robolectric.shadows.ShadowNetworkInfo


/**
 * Created by Julien HAMMER - Apprenti Java with openclassrooms on .
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.P])
@LooperMode(LooperMode.Mode.PAUSED)
class IntegratedNetworkUtilsTest {
    // For connectivityManager and shadowOfIsInternetAvailableNetworkInfo and the context
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var shadowOfIsInternetAvailableNetworkInfo: ShadowNetworkInfo
    private var context: Context? = null

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        shadowOfIsInternetAvailableNetworkInfo = shadowOf(connectivityManager.activeNetworkInfo)
    }

    @Test
    @Throws(Exception::class)
    fun checkHasNoInternet() {
        shadowOfIsInternetAvailableNetworkInfo.setConnectionStatus(NetworkInfo.State.DISCONNECTED as NetworkInfo.State?)
        assertEquals(EXPECTED_VALUE_FOR_NO_INTERNET, isInternetAvailable(connectivityManager))
    }

    @Test
    @Throws(Exception::class)
    fun checkIsInternetOn() {
        shadowOfIsInternetAvailableNetworkInfo.setConnectionStatus(NetworkInfo.State.CONNECTED as NetworkInfo.State?)
        assertTrue(isInternetAvailable(connectivityManager))
    }

    companion object {
        private const val EXPECTED_VALUE_FOR_NO_INTERNET = false
    }
}



