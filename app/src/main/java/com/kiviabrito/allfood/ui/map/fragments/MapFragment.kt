package com.kiviabrito.allfood.ui.map.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.kiviabrito.allfood.R
import com.kiviabrito.allfood.data.model.PlaceDTO
import com.kiviabrito.allfood.databinding.FragmentMapBinding
import com.kiviabrito.allfood.databinding.MarkerMapBinding
import com.kiviabrito.allfood.ui.CustomFragmentFactory.MainFragmentEnum
import com.kiviabrito.allfood.ui.map.MapViewModel
import com.kiviabrito.allfood.utils.BaseFragment
import com.kiviabrito.allfood.utils.GeoUtils
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.closeKeyboard
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.loadImage
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.orZero
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.removeAccent
import com.kiviabrito.allfood.utils.HelpFunctions.Companion.setIsVisible
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MapFragment : BaseFragment() {

    companion object {
        const val CAMERA_ZOOM = 14f
        const val DEFAULT_LAT = 37.4219999
        const val DEFAULT_LNG = -122.0862462
        const val INDEX_LAT = 0
        const val INDEX_LNG = 1
        const val DISTANCE_KM_LOAD_MORE = 3
    }

    private lateinit var binding: FragmentMapBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var mapFragment: SupportMapFragment? = null
    private val viewModel: MapViewModel by sharedViewModel()
    private val backupLocation = LatLng(DEFAULT_LAT, DEFAULT_LNG)
    private var googleMap: GoogleMap? = null
    private var locationPermissionRequests = 0
    private var markers = arrayListOf<Marker>()
    private var selectedMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setObservers()
        onMapReady()
    }

    private fun setupView() {
        binding.apply {
            btnMapList.setOnClickListener {
                loadFragment(MainFragmentEnum.LIST)
            }
            btnLoadAgain.setOnClickListener {
                googleMap?.cameraPosition?.target?.let { location ->
                    viewModel.lastLocation = doubleArrayOf(location.latitude, location.longitude)
                    viewModel.getRestaurantList(location.latitude, location.longitude)
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.restaurants.observe(viewLifecycleOwner, { restaurants ->
            binding.btnLoadAgain.setIsVisible(false)
            setupMarkers(restaurants)
            moveCamera()
        })
        viewModel.search.observe(viewLifecycleOwner, { searchCharSequence ->
            viewModel.restaurants.value?.filter {
                it.name.toLowerCase().removeAccent().contains(searchCharSequence ?: "")
            }?.let {
                setupMarkers(it)
            }
        })
    }

    private fun onMapReady() {
        mapFragment?.getMapAsync { map ->
            map?.apply {
                googleMap = this
                mapType = GoogleMap.MAP_TYPE_NORMAL
                clear()
                setOnMapClickListener { requireActivity().closeKeyboard() }
                setOnCameraMoveListener { onCameraMove(this) }
                setOnMarkerClickListener { it.onMarkerClick(); false }
                loadData()
                setInfoWindowAdapter()
            }
        }
    }

    private fun Marker.onMarkerClick() {
        requireActivity().closeKeyboard()
        selectedMarker?.let { selectedMarker ->
            val markerDrawableInactive = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_map_gray)
            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(markerDrawableInactive?.toBitmap()))
        }
        val markerDrawableActive = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_map_green)
        setIcon(BitmapDescriptorFactory.fromBitmap(markerDrawableActive?.toBitmap()))
        selectedMarker = this
    }

    private fun GoogleMap.setInfoWindowAdapter() {
        setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(p0: Marker?): View? = null

            override fun getInfoWindow(p0: Marker?): View {
                val windowBinding = MarkerMapBinding.inflate(requireActivity().layoutInflater, null, false)
                (p0?.tag as? PlaceDTO)?.let { place ->
                    windowBinding.apply {
                        name.text = place.name
                        rating.rating = place.rating?.toFloat().orZero()
                        totalRating.text = getString(R.string.total_rating_format, place.ratingTotal.orZero())
                        priceLevel.text = place.getPriceLevelAndOpenHour()
                        image.loadImage(place.imageURL)
                    }
                }
                return windowBinding.root
            }

        })
    }

    private fun onCameraMove(googleMap: GoogleMap) {
        requireActivity().closeKeyboard()
        googleMap.cameraPosition?.target?.let { location ->
            viewModel.lastLocation?.takeIf { it.isNotEmpty() }?.let { array ->
                val distance = GeoUtils.distanceInKm(
                    array[INDEX_LAT],
                    array[INDEX_LNG],
                    location.latitude,
                    location.longitude
                )
                binding.btnLoadAgain.setIsVisible(distance > DISTANCE_KM_LOAD_MORE)
            } ?: binding.btnLoadAgain.setIsVisible(true)
        }
    }

    private fun loadData() {
        getLocation { location ->
            viewModel.userLocation = location
            viewModel.userLocation?.takeIf { viewModel.lastLocation == null }?.let { searchLocation ->
                viewModel.lastLocation = doubleArrayOf(location.latitude, location.longitude)
                viewModel.getRestaurantList(searchLocation.latitude, searchLocation.longitude)
            } ?: viewModel.restaurants.value?.let { list ->
                val filterList = viewModel.search.value?.let { value ->
                    list.filter { it.name.toLowerCase().removeAccent().contains(value) }
                } ?: list
                setupMarkers(filterList)
            }
            moveCamera()
        }
    }

    private fun moveCamera() {
        viewModel.lastLocation?.let { lastLocation ->
            val lat = lastLocation.getOrNull(INDEX_LAT).orZero()
            val lng = lastLocation.getOrNull(INDEX_LNG).orZero()
            val lastLatLng = LatLng(lat, lng)
            markers.find { it.position == lastLatLng }?.let { selectedMarker ->
                selectedMarker.showInfoWindow()
                selectedMarker.onMarkerClick()
            }
            val center = CameraUpdateFactory.newLatLng(lastLatLng)
            val zoom = CameraUpdateFactory.zoomTo(CAMERA_ZOOM)
            googleMap?.moveCamera(center)
            googleMap?.animateCamera(zoom)
        } ?: viewModel.userLocation?.let {
            val center = CameraUpdateFactory.newLatLngZoom(it, CAMERA_ZOOM)
            googleMap?.animateCamera(center)
        }
    }

    private fun setupMarkers(usersList: List<PlaceDTO>) {
        googleMap?.clear()
        markers.clear()
        selectedMarker = null
        usersList.forEach { user ->
            addMarkers(user)
        }
    }

    private fun addMarkers(place: PlaceDTO) {
        googleMap?.let { googleMap ->
            val lat = place.geometry?.location?.lat.orZero()
            val lng = place.geometry?.location?.lng.orZero()
            val marker = googleMap.addMarker(MarkerOptions().position(LatLng(lat, lng)))
            marker.tag = place
            markers.add(marker)
            val markerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_marker_map_gray)
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(markerDrawable?.toBitmap()))
        }
    }

    private fun getLocation(location: (LatLng) -> Unit) {
        when {
            ActivityCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                try {
                    googleMap?.isMyLocationEnabled = true
                    googleMap?.uiSettings?.apply {
                        isMyLocationButtonEnabled = true
                        isMapToolbarEnabled = true
                    }
                    fusedLocationClient.lastLocation.addOnCompleteListener { locationTask ->
                        if (locationTask.isSuccessful) {
                            val locationResult = locationTask.result
                            val lat = locationResult?.latitude ?: DEFAULT_LAT
                            val lng = locationResult?.longitude ?: DEFAULT_LNG
                            location(LatLng(lat, lng))
                        } else {
                            location(backupLocation)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    location(backupLocation)
                }
            }
            locationPermissionRequests < 1 -> {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 0
                )
                locationPermissionRequests++
            }
            else -> {
                location(backupLocation)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.apply {
                isMyLocationButtonEnabled = true
                isMapToolbarEnabled = true
            }
            getLocation { location ->
                viewModel.userLocation = location
                viewModel.getRestaurantList(location.latitude, location.longitude)
                moveCamera()
                viewModel.lastLocation = doubleArrayOf(location.latitude, location.longitude)
            }
        } else {
            loadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapFragment?.onLowMemory()
    }

}
