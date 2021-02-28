package com.kiviabrito.allfood.ui.map.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kiviabrito.allfood.data.local.favoriteplace.FavoritePlace
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.databinding.FragmentListBinding
import com.kiviabrito.allfood.ui.CustomFragmentFactory.MainFragmentEnum
import com.kiviabrito.allfood.ui.map.MapViewModel
import com.kiviabrito.allfood.ui.map.fragments.MapFragment.Companion.DEFAULT_LAT
import com.kiviabrito.allfood.ui.map.fragments.MapFragment.Companion.DEFAULT_LNG
import com.kiviabrito.allfood.utils.BaseActivity
import com.kiviabrito.allfood.utils.BaseFragment
import com.kiviabrito.allfood.utils.GeoUtils
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.copy
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.orZero
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class ListFragment : BaseFragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: MapViewModel by sharedViewModel()
    private val adapter: RestaurantAdapter by lazy {
        RestaurantAdapter(
            ArrayList(),
            onItemClickListener = { openMapAtItem(it) },
            onFavoriteClickListener = { insertOrDeleteFavorite(it) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObservers()
    }

    private fun setupView() {
        binding.apply {
            restaurantList.adapter = adapter
            btnMapList.setOnClickListener {
                (activity as? BaseActivity)?.goBackToFragment(MainFragmentEnum.MAP)
            }
        }
    }

    private fun setObservers() {
        viewModel.restaurants.observe(viewLifecycleOwner, { restaurants ->
            setupItemsToRecyclerView(restaurants)
        })
        viewModel.search.observe(viewLifecycleOwner, { searchCharSequence ->
            adapter.filter.filter(searchCharSequence)
        })
        viewModel.favoritePlaces.observe(viewLifecycleOwner, {
            val placesList = viewModel.restaurants.value.orEmpty().copy()
            setupItemsToRecyclerView(placesList)
        })
    }

    private fun setupItemsToRecyclerView(placeList: List<PlaceDTO>) {
        val sortedList = placeList.sortedBy { place ->
            val distance = GeoUtils.distanceInKm(
                viewModel.userLocation?.latitude ?: DEFAULT_LAT,
                viewModel.userLocation?.longitude ?: DEFAULT_LNG,
                place.geometry?.location?.lat.orZero(),
                place.geometry?.location?.lng.orZero()
            )
            place.distanceToUserKm = distance
            place.isFavorite = viewModel.favoritePlaces.value?.map { it.id }?.contains(place.id) == true
            distance
        }
        adapter.setItemsAdapter(sortedList)
    }

    private fun openMapAtItem(item: PlaceDTO) {
        viewModel.lastLocation = doubleArrayOf(
            item.geometry?.location?.lat.orZero(),
            item.geometry?.location?.lng.orZero()
        )
        (activity as? BaseActivity)?.goBackToFragment(MainFragmentEnum.MAP)
    }

    private fun insertOrDeleteFavorite(item: PlaceDTO) {
        val favoritePlace = FavoritePlace(id = item.id, name = item.name)
        if (item.isFavorite) {
            viewModel.deleteFavoritePlace(favoritePlace)
        } else {
            viewModel.insertFavoritePlace(favoritePlace)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.restaurantList.adapter = null
    }

}
