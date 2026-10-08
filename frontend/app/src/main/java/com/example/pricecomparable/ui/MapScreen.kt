package com.example.pricecomparable.ui

import android.Manifest
import android.app.Application
import org.osmdroid.util.GeoPoint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pricecomparable.ViewModel.MapViewModel
import com.example.pricecomparable.network.ApiClient
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import kotlin.math.ceil
import kotlin.math.ln
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen() {

    val context = LocalContext.current
    val viewModel: MapViewModel = viewModel {
        MapViewModel(context.applicationContext as Application, ApiClient.storeApi)
    }

    val uiState by viewModel.uiState.collectAsState()
    val markers = uiState.markers
    val routePoints = uiState.routePoints
    val userLocation = uiState.userLocation

    var endAddress by remember { mutableStateOf("") }

    // Permission launcher
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            viewModel.getUserLocation()
        }
    }

    // Load markers + request permission
    LaunchedEffect(Unit) {
        viewModel.getStoreLocation()

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // UI
    Scaffold(
        topBar = {},

        ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {

            // Top Row: Logo + Search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Logo circle
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color(0xFFE0E0E0)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("Logo")
                    }
                }

                // Search bar
                OutlinedTextField(
                    value = endAddress,
                    onValueChange = { endAddress = it },
                    placeholder = { Text("Search") },
                    leadingIcon = {
                        IconButton(
                            onClick = {
                                if (endAddress.isNotEmpty()) {
                                    viewModel.loadRouteFromAddresses(
                                        startPoint = userLocation,
                                        endPoint = endAddress
                                    )
                                }
                            }
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(50),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (endAddress.isNotEmpty()) {
                                viewModel.loadRouteFromAddresses(
                                    startPoint = userLocation,
                                    endPoint = endAddress
                                )
                            }
                        }
                    )
                )
            }

            // Map Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {

                AndroidView(
                    factory = { ctx ->
                        Configuration.getInstance()
                            .load(ctx, ctx.getSharedPreferences("osmdroid", 0))

                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)

                            // No repeated worlds (so no grid of multiple Earths)
                            setHorizontalMapRepetitionEnabled(false)
                            setVerticalMapRepetitionEnabled(false)

                            // Limit panning to the "valid world" area
                            val worldBox = BoundingBox(
                                85.0,   // north
                                180.0,  // east
                                -85.0,  // south
                                -180.0  // west
                            )
                            setScrollableAreaLimitDouble(worldBox)

                            // Dynamically compute min zoom so the map
                            // always fills the view (no shrinking into the middle and no grid effect)
                            post {
                                val viewWidth = width
                                val viewHeight = height
                                if (viewWidth > 0 && viewHeight > 0) {
                                    val maxDim = max(viewWidth, viewHeight).toDouble()
                                    val tileSize = 256.0

                                    // worldSizePx = 256 * 2^zoom
                                    // need worldSizePx >= maxDim
                                    // => zoom >= log2(maxDim / 256)
                                    val zoomNeeded = ceil(
                                        ln(maxDim / tileSize) / ln(2.0)
                                    )

                                    minZoomLevel = zoomNeeded

                                    if (zoomLevelDouble < zoomNeeded) {
                                        controller.setZoom(zoomNeeded)
                                    }
                                }
                            }

                            // Start reasonably zoomed in; will be clamped by minZoom above
                            controller.setZoom(15.0)
                        }
                    },
                    update = { mapView ->

                        // Remove old polylines
                        mapView.overlays.removeAll { it is Polyline }

                        // User marker
                        userLocation?.let { location ->
                            val exists = mapView.overlays.any {
                                it is Marker && it.title == "Your Location"
                            }
                            if (!exists) {
                                val userMarker = Marker(mapView).apply {
                                    position = location
                                    title = "Your Location"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                                }
                                mapView.overlays.add(userMarker)
                            }
                            if (location != null) {
                                mapView.controller.setCenter(location)
                            } else {
                                //default center venlo
                                mapView.controller.setCenter(GeoPoint(51.3700, 6.1722))
                            }
                        }

                        // Store markers (avoid duplicates)
                        markers.forEach { markerData ->
                            val exists = mapView.overlays.any {
                                it is Marker && it.title == markerData.title
                            }
                            if (!exists) {
                                val marker = Marker(mapView).apply {
                                    position = markerData.position
                                    title = markerData.title
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                mapView.overlays.add(marker)
                            }
                        }

                        // Route polyline
                        if (routePoints.isNotEmpty()) {
                            val polyline = Polyline().apply {
                                setPoints(routePoints)
                            }
                            mapView.overlays.add(polyline)
                        }

                        mapView.invalidate()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
