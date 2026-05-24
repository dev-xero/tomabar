package dev.xero.tomabar.domain.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface TomaBarApi {
    @GET("health")
    suspend fun health(): Response<Unit>

    @GET("metrics")
    suspend fun metrics(): Response<ResponseBody>
}