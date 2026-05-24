package dev.xero.tomabar.domain.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false

    // For a LAN server, "has wifi transport" is a better signal than internet validation,
    // since a local-only network may report no internet but still reach the server.
    return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}