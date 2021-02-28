package com.kiviabrito.allfood.ui.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.kiviabrito.allfood.MainCoroutineRule
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.data.repository.FakePlacesRepository
import com.kiviabrito.allfood.data.repository.FakePlacesRepository.Companion.INVALID_LAT
import com.kiviabrito.allfood.data.repository.FakePlacesRepository.Companion.INVALID_LNG
import com.kiviabrito.allfood.data.repository.FakePlacesRepository.Companion.VALID_LAT
import com.kiviabrito.allfood.data.repository.FakePlacesRepository.Companion.VALID_LNG
import com.kiviabrito.allfood.getOrAwaitValueTest
import com.kiviabrito.allfood.utils.DataState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.ERROR_MSG

@ExperimentalCoroutinesApi
class MapViewModelTest {

    companion object {
        const val SEARCH_VALUE = "SEARCH_VALUE"
    }

    private val favoritePlace = FavoritePlace("id", "uuid")

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MapViewModel

    @Before
    fun setup() {
        viewModel = MapViewModel(FakePlacesRepository())
    }

    @Test
    fun `get restaurant with valid lat and lng list, set success restaurants`() {
        viewModel.getRestaurantList(VALID_LAT, VALID_LNG)
        val value = viewModel.restaurants.getOrAwaitValueTest()
        assertThat(value).isEqualTo(FakePlacesRepository.defaultList.results)
    }

    @Test
    fun `get restaurant with invalid lat and lng list, set error msg`() {
        viewModel.getRestaurantList(INVALID_LAT, INVALID_LNG)
        val value = viewModel.error.getOrAwaitValueTest().get()
        assertThat(value).isEqualTo(ERROR_MSG)
    }

    @Test
    fun `handle data state success, set success empty restaurants`() {
        val dataState : DataState<List<PlaceDTO>> = DataState.Success(FakePlacesRepository.defaultList.results)
        viewModel.handleDataState(dataState)
        val value = viewModel.restaurants.getOrAwaitValueTest()
        assertThat(value).isEqualTo(FakePlacesRepository.defaultList.results)
    }

    @Test
    fun `handle data state error, set error msg`() {
        val dataState : DataState<List<PlaceDTO>> = DataState.Error(ERROR_MSG)
        viewModel.handleDataState(dataState)
        val value = viewModel.error.getOrAwaitValueTest().get()
        assertThat(value).isEqualTo(ERROR_MSG)
    }

    @Test
    fun `set onSearch value, set search value`() {
        viewModel.onSearch(SEARCH_VALUE)
        val value = viewModel.search.getOrAwaitValueTest()
        assertThat(value).isEqualTo(SEARCH_VALUE)
    }

    @Test
    fun `insert favoritePlace value to db, value inserted`() {
        viewModel.insertFavoritePlace(favoritePlace)
        val value = viewModel.favoritePlaces.getOrAwaitValueTest()
        assertThat(value).isEqualTo(listOf(favoritePlace))
    }

    @Test
    fun `delete favoritePlace value to db, value deleted`() {
        viewModel.insertFavoritePlace(favoritePlace)
        viewModel.deleteFavoritePlace(favoritePlace)
        val value = viewModel.favoritePlaces.getOrAwaitValueTest()
        assertThat(value).isEqualTo(listOf<FavoritePlace>())
    }

}