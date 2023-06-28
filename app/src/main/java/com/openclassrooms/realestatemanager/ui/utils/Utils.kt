package com.openclassrooms.realestatemanager.ui.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


/**
 * Created by Philippe on 21/02/2018.
 */
object Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
//    @JvmStatic
//    fun convertDollarToEuro(dollars: Int): Int {
//        return (dollars * 0.812).roundToInt()
//    }

    @JvmStatic
    fun convertDollarsToEuros(dollars: Int): Int {
        return (dollars * 0.93028).roundToInt()
    }

    @JvmStatic
    fun convertEurosToDollars(euros: Int): Int {
        return (euros * 1.07473).roundToInt()
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
//    val todayDateFranceFormat: String
//        get() {
//            val dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
//            return dateFormat.format(Date())
//        }

    // I want to return a SimpleDateFormat and not a String so I changed the return type of the function and then I can change the SimpleDateFormat in the fragments
    val todayDateFranceFormat: SimpleDateFormat
        get() {
            return SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
        }

    val todayDateUsaFormat: SimpleDateFormat
        get() {
            return SimpleDateFormat("yyyy/MM/dd", Locale.US)
        }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    // Here the code is commented because it is not used in the project but was kept to show it in exam
//    fun isInternetAvailable(context: Context): Boolean {
//        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        return wifi.isWifiEnabled
//    }

    fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {

        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            return networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        }

        return false

//        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
//            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
//            return true
//        }
//        else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
//            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
//            return true
//        }
//        else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
//            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
//            return true
//        }
//        return false
    }
}