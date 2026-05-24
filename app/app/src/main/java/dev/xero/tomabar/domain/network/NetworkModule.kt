package dev.xero.tomabar.domain.network

import retrofit2.Retrofit
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun buildApi(baseUrl: String): TomaBarApi {
    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .build()
        .create(TomaBarApi::class.java)
}