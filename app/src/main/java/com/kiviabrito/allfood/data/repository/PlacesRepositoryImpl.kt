package com.kiviabrito.allfood.data.repository

import androidx.lifecycle.LiveData
import com.kiviabrito.allfood.data.api.RetrofitFactory
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlaceDao
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.utils.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlacesRepositoryImpl(
    private val favoritePlaceDao: FavoritePlaceDao,
    private val retrofitFactory: RetrofitFactory
) : PlacesRepository {

    companion object {
        const val ERROR_NULL_OBJ = "Error: Null obj"
        const val ERROR_EXCEPTION = "Error: "
        const val LOADING_DELAY = 2000L
    }

    override suspend fun getRestaurantList(latitude: Double, longitude: Double): Flow<DataState<List<PlaceDTO>>> = flow {
        emit(DataState.Loading)
        try {
            retrofitFactory.restaurantsService.getRestaurantList("$latitude,$longitude").apply {
                delay(LOADING_DELAY)
                if (isSuccessful) {
                    body()?.let { obj ->
                        emit(DataState.Success(obj.results))
                    } ?: emit(DataState.Error(ERROR_NULL_OBJ))
                } else {
                    emit(DataState.Error(ERROR_EXCEPTION + message()))
                }
            }
        } catch (e: Exception) {
            emit(DataState.Error(ERROR_EXCEPTION + e.message))
        }
    }

    override suspend fun insertFavoritePlace(favoritePlace: FavoritePlace) {

        favoritePlaceDao.insertPlace(favoritePlace)
    }

    override suspend fun deleteFavoritePlace(favoritePlace: FavoritePlace) {
        favoritePlaceDao.deletePlace(favoritePlace)
    }

    override fun observeAllFavoritePLaces(): LiveData<List<FavoritePlace>> {
        return favoritePlaceDao.observeAllPlaces()
    }

}