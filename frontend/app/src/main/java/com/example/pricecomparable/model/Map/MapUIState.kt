// kotlin
package com.example.pricecomparable.model

import org.osmdroid.util.GeoPoint

data class MapUiState(
    val userLocation: GeoPoint? = null,
    val markers: List<MarkerData> = emptyList(),
    val routePoints: List<GeoPoint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
