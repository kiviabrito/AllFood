package com.kiviabrito.allfood.data.api

import com.kiviabrito.allfood.BuildConfig.GOOGLE_API_KEY
import com.kiviabrito.allfood.data.model.PlacesListDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantsService {

    @GET("maps/api/place/nearbysearch/json")
    suspend fun getRestaurantList(
        @Query("location") location: String,
        @Query("radius") radius: Int = 5000,
        @Query("types") types: List<String> = listOf("restaurant"),
        @Query("sensor") sensor: Boolean = false,
        @Query("key") key : String = GOOGLE_API_KEY
        ): Response<PlacesListDTO?>
}