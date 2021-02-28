package com.kiviabrito.allfood.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.data.repository.PlacesRepository
import com.kiviabrito.allfood.utils.DataState
import com.kiviabrito.allfood.utils.SingleEventWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(
    private val repository: PlacesRepository
) : ViewModel(), Map.ViewModel {

    private val _restaurants = MutableLiveData<List<PlaceDTO>>()
    val restaurants: LiveData<List<PlaceDTO>> = _restaurants

    private val _error = MutableLiveData<SingleEventWrapper<String>>()
    val error: LiveData<SingleEventWrapper<String>> = _error

    private val _loading = MutableLiveData<SingleEventWrapper<Boolean>>()
    val loading: LiveData<SingleEventWrapper<Boolean>> = _loading

    private val _search = MutableLiveData<CharSequence?>()
    val search: LiveData<CharSequence?> = _search

    val favoritePlaces = repository.observeAllFavoritePLaces()

    var lastLocation: DoubleArray? = null
    var userLocation: LatLng? = null

    override fun getRestaurantList(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getRestaurantList(latitude, longitude)
                .collect { dataState ->
                    withContext(Dispatchers.Main) {
                        handleDataState(dataState)
                    }
                }
        }
    }

    override fun handleDataState(dataState: DataState<List<PlaceDTO>>) {
        when (dataState) {
            is DataState.Success<List<PlaceDTO>> -> {
                _loading.postValue(SingleEventWrapper(false))
                dataState.data.let { employees ->
                    _restaurants.postValue(employees)
                }
            }
            is DataState.Error -> {
                _loading.postValue(SingleEventWrapper(false))
                _error.postValue(SingleEventWrapper(dataState.errorMsg))
            }
            is DataState.Loading -> {
                _loading.postValue(SingleEventWrapper(true))
            }
        }
    }

    override fun onSearch(s: CharSequence?) {
        _search.postValue(s)
    }

    override fun insertFavoritePlace(favoritePlace: FavoritePlace) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertFavoritePlace(favoritePlace)
        }
    }

    override fun deleteFavoritePlace(favoritePlace: FavoritePlace) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFavoritePlace(favoritePlace)
        }
    }

}