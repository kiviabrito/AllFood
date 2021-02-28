package com.kiviabrito.allfood.data.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitFactoryImpl : RetrofitFactory {

    private val baseUrl = "https://maps.googleapis.com/"

    private val client =
        OkHttpClient
            .Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    override val restaurantsService: RestaurantsService =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(RestaurantsService::class.java)

    init {
        Log.i(this::class.simpleName, this::restaurantsService.name)
    }

}