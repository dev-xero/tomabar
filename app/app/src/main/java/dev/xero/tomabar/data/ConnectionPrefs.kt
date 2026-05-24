package dev.xero.tomabar.data

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "connection")

class ConnectionPrefs(private val context: Context) {
    private val serverKey = stringPreferencesKey("server_base_url")
    private val metricsKey = stringPreferencesKey("cached_metrics_ndjson")
    private val fetchedAtKey = longPreferencesKey("metrics_fetched_at")

    val baseUrl: Flow<String?> = context.dataStore.data.map { it[serverKey] }
    val cachedMetrics: Flow<String?> = context.dataStore.data.map { it[metricsKey] }
    val lastFetchedAt: Flow<Long?> = context.dataStore.data.map { it[fetchedAtKey] }

    suspend fun saveConnection(baseUrl: String) =
        context.dataStore.edit { it[serverKey] = baseUrl }

    suspend fun saveMetrics(ndjson: String) =
        context.dataStore.edit {
            it[metricsKey] = ndjson
            it[fetchedAtKey] = System.currentTimeMillis()
        }

    suspend fun clearConnection() =
        context.dataStore.edit { it.remove(serverKey)
    }
}