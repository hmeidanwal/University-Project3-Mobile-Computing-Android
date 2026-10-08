package com.example.pricecomparable.ViewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecomparable.model.MapUiState
import com.example.pricecomparable.model.MarkerData
import com.example.pricecomparable.network.StoreApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.util.GeoPoint
import java.util.ArrayList

class MapViewModel(application: Application, private val storeApiService: StoreApiService) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState

    private val roadManager: RoadManager = OSRMRoadManager(application, "PriceComparableApp/1.0")
    private var storeMarkers: List<MarkerData> = emptyList()






    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        viewModelScope.launch {
            try {
                val locationManager =
                    getApplication<Application>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                if (location != null) {
                    val point = GeoPoint(location.latitude, location.longitude)
                    _uiState.update { it.copy(userLocation = point) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun getStoreLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val storesResponse = storeApiService.getAllStores()
                val ownerNamesResponse = storeApiService.getAllOwnerNames()
                
                if (storesResponse.isSuccessful && ownerNamesResponse.isSuccessful) {
                    val stores = storesResponse.body() ?: emptyList()
                    val ownerNames = ownerNamesResponse.body() ?: emptyList()
                    
                    // Create a map of email -> owner name for quick lookup
                    val ownerNameMap = ownerNames.associateBy { it.email }
                    
                    storeMarkers = stores.mapNotNull { store ->
                        val address = listOfNotNull(
                            store.street_number,
                            store.street_name,
                        ).joinToString(", ")

                        val geoPoint = geocodeAddress(address)
                        // Look up owner name by email, fallback to full_name or default
                        val storeName = store.account_email?.let { ownerNameMap[it]?.full_name } 
                            ?: store.full_name ?: "Store"

                        if (geoPoint != null) {
                            MarkerData(position = geoPoint, title = storeName)
                        } else null
                    }
                    _uiState.update { it.copy(markers = storeMarkers, isLoading = false) }
                } else {
                    _uiState.update { it.copy(error = "Failed to fetch stores or owner names", isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun geocodeAddress(address: String): GeoPoint? {
        return try {
            val geocoder = Geocoder(getApplication())
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                GeoPoint(addresses[0].latitude, addresses[0].longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun loadRouteFromAddresses(startPoint: GeoPoint?, endPoint: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val startPointAddress = _uiState.value.userLocation ?: startPoint
            val endPointAddress = geocodeAddress(endPoint)

            when {
                startPointAddress == null || endPointAddress == null -> {
                    _uiState.update { it.copy(isLoading = false, error = "Could not geocode address") }
                }
                else -> loadRoute(startPointAddress, endPointAddress)
            }
        }
    }

    fun loadRoute(startPoint: GeoPoint, endPoint: GeoPoint) {
        viewModelScope.launch {
            try {
                val waypoints = arrayListOf(startPoint, endPoint)
                val road: Road = roadManager.getRoad(waypoints)

                val routePoints = if (road.mRouteHigh.isNotEmpty()) road.mRouteHigh else listOf(startPoint, endPoint)
                val markers = ArrayList(storeMarkers).apply {
                    add(MarkerData(position = startPoint, title = "Start"))
                    add(MarkerData(position = endPoint, title = "End"))
                }

                _uiState.update { it.copy(routePoints = routePoints, markers = markers, isLoading = false, error = null) }
            } catch (e: Exception) {
                val fallbackMarkers = ArrayList(storeMarkers).apply {
                    add(MarkerData(position = startPoint, title = "Start"))
                    add(MarkerData(position = endPoint, title = "End"))
                }
                _uiState.update { it.copy(routePoints = listOf(startPoint, endPoint), markers = fallbackMarkers, isLoading = false, error = e.message) }
            }
        }
    }


}