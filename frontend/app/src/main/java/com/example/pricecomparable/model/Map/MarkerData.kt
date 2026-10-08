// kotlin
package com.example.pricecomparable.model

import org.osmdroid.util.GeoPoint

data class MarkerData(
    val position: GeoPoint,
    val title: String,
    val snippet: String? = null
)
