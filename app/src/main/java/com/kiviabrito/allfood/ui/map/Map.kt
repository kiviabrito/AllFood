package com.kiviabrito.allfood.ui.map

import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.utils.DataState

interface Map {

    interface ViewModel {
        fun getRestaurantList(latitude: Double, longitude: Double)
        fun handleDataState(dataState: DataState<List<PlaceDTO>>)
        fun onSearch(s: CharSequence?)
        fun insertFavoritePlace(favoritePlace: FavoritePlace)
        fun deleteFavoritePlace(favoritePlace: FavoritePlace)
    }

}