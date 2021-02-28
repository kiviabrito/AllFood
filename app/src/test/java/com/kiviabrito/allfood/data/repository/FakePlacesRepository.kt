package com.kiviabrito.allfood.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.model.*
import com.kiviabrito.allfood.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.android.ext.koin.ERROR_MSG

class FakePlacesRepository : PlacesRepository {

    companion object {
        val defaultList = PlacesListDTO(
            results = listOf(
                PlaceDTO(
                    id = "uuid",
                    name = "fullName",
                    geometry = GeometryDTO(location = LocationDTO(8.5586, 76.8814)),
                    imageURL = "imageUrl",
                    rating = 4.5,
                    ratingTotal = 60,
                    businessStatus = "OPERATIONAL",
                    priceLevel = 1,
                    openHours = OpenHoursDTO(true)
                )
            )
        )

        const val INVALID_LAT = 77.11112223331
        const val INVALID_LNG = 249.99999999
        const val VALID_LAT = 8.5586
        const val VALID_LNG = 76.8814

    }

    private val favoritePlaces = mutableListOf<FavoritePlace>()
    private val observableAllFavoritePlaces = MutableLiveData<List<FavoritePlace>>(favoritePlaces)

    override suspend fun getRestaurantList(
        latitude: Double,
        longitude: Double
    ): Flow<DataState<List<PlaceDTO>>> = flow {
        if (latitude == INVALID_LAT || longitude == INVALID_LNG) {
            emit(DataState.Error(ERROR_MSG))
        } else {
            emit(DataState.Success(defaultList.results))
        }
    }

    override suspend fun insertFavoritePlace(favoritePlace: FavoritePlace) {
        favoritePlaces.add(favoritePlace)
        refreshLiveData()
    }

    override suspend fun deleteFavoritePlace(favoritePlace: FavoritePlace) {
        favoritePlaces.remove(favoritePlace)
        refreshLiveData()
    }

    override fun observeAllFavoritePLaces(): LiveData<List<FavoritePlace>> {
        return  observableAllFavoritePlaces
    }

    private fun refreshLiveData() {
        observableAllFavoritePlaces.postValue(favoritePlaces)
    }

}











