package dev.xero.tomabar.data

import android.content.Context
import dev.xero.tomabar.domain.models.ConnectResult
import dev.xero.tomabar.domain.models.LoadResult
import dev.xero.tomabar.domain.network.buildApi
import dev.xero.tomabar.domain.utils.isOnline
import dev.xero.tomabar.domain.utils.normalizeBaseUrl
import dev.xero.tomabar.domain.utils.parseMetrics
import kotlinx.coroutines.flow.first

class MetricsRepository(
    private val prefs: ConnectionPrefs,
    private val context: Context,
) {
    /** First-time / re-onboarding connect: validate, fetch, persist on success. */
    suspend fun connectAndFetch(rawInput: String): ConnectResult {
        val baseUrl = normalizeBaseUrl(rawInput) ?: return ConnectResult.InvalidAddress
        val api = buildApi(baseUrl)
        return try {
            val health = api.health()
            if (!health.isSuccessful) return ConnectResult.ServerError(health.code())
            val metrics = api.metrics()
            if (!metrics.isSuccessful) return ConnectResult.ServerError(metrics.code())

            val body = metrics.body()?.string().orEmpty()
            prefs.saveConnection(baseUrl)
            prefs.saveMetrics(body)
            ConnectResult.Success(parseMetrics(body))
        } catch (e: java.io.IOException) {
            ConnectResult.Unreachable
        }
    }

    /** Launch-time refresh against a saved IP, offline-first. */
    suspend fun refresh(): LoadResult {
        val baseUrl = prefs.baseUrl.first() ?: return LoadResult.StaleConnection
        val api = buildApi(baseUrl)
        return try {
            val health = api.health()
            val metrics = if (health.isSuccessful) api.metrics() else null
            if (metrics?.isSuccessful == true) {
                val body = metrics.body()?.string().orEmpty()
                prefs.saveMetrics(body)
                LoadResult.Fresh(parseMetrics(body), System.currentTimeMillis())
            } else {
                onlineButServerFailed()
            }
        } catch (e: java.io.IOException) {
            if (isOnline(context)) onlineButServerFailed()
            else fallBackToCache()
        }
    }

    private suspend fun onlineButServerFailed(): LoadResult {
        prefs.clearConnection()
        return LoadResult.StaleConnection
    }

    private suspend fun fallBackToCache(): LoadResult {
        val cached = prefs.cachedMetrics.first()
        val at = prefs.lastFetchedAt.first()
        return if (cached != null) LoadResult.Cached(parseMetrics(cached), at)
        else LoadResult.NoData
    }
}