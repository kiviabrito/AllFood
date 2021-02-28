package com.kiviabrito.allfood.data.repository

import androidx.lifecycle.LiveData
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.utils.DataState
import kotlinx.coroutines.flow.Flow

interface PlacesRepository {

    suspend fun getRestaurantList(
        latitude: Double,
        longitude: Double
    ): Flow<DataState<List<PlaceDTO>>>

    suspend fun insertFavoritePlace(favoritePlace: FavoritePlace)

    suspend fun deleteFavoritePlace(favoritePlace: FavoritePlace)

    fun observeAllFavoritePLaces(): LiveData<List<FavoritePlace>>

}