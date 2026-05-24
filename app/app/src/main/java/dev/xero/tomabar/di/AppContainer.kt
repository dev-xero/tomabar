package dev.xero.tomabar.di

import android.content.Context
import dev.xero.tomabar.data.ConnectionPrefs
import dev.xero.tomabar.data.MetricsRepository

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val connectionPrefs: ConnectionPrefs by lazy { ConnectionPrefs(appContext) }

    val metricsRepository: MetricsRepository by lazy {
        MetricsRepository(prefs = connectionPrefs, context = appContext)
    }
}