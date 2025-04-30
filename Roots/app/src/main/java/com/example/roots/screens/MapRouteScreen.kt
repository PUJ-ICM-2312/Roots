package com.example.roots.screens

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }
    return poly
}

@SuppressLint("MissingPermission")
@Composable
fun MapRouteScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val coroutineScope = rememberCoroutineScope()

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var routePolyline by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    val destination = LatLng(4.6275, -74.0638) // 🏠 Puedes parametrizar esto

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(destination, 13f)
    }

    val locationRequest = LocationRequest.create().apply {
        interval = 5000
        fastestInterval = 2000
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    coroutineScope.launch(Dispatchers.IO) {
                        routePolyline = getRoute(userLocation!!, destination)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let {
                            userLocation = LatLng(it.latitude, it.longitude)
                        }
                    }
                },
                Looper.getMainLooper()
            )
        }
    }

    LaunchedEffect(true) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = true),
        properties = MapProperties(isMyLocationEnabled = userLocation != null)
    ) {
        userLocation?.let {
            Marker(state = MarkerState(it), title = "Tu ubicación")
        }

        Marker(state = MarkerState(destination), title = "Destino")

        if (routePolyline.isNotEmpty()) {
            Polyline(points = routePolyline, color = Color.Red, width = 8f)
        }
    }
}

suspend fun getRoute(origin: LatLng, dest: LatLng): List<LatLng> {
    return try {
        val client = OkHttpClient()
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&mode=walking" +
                "&key=AIzaSyCmJB7XLP_imzHPomUhC361kpN320d5ETw"

        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val json = response.body?.string() ?: return emptyList()
        val jsonObject = JSONObject(json)

        val poly = jsonObject.getJSONArray("routes")
            .getJSONObject(0)
            .getJSONObject("overview_polyline")
            .getString("points")

        decodePolyline(poly)
    } catch (e: Exception) {
        Log.e("MapRouteScreen", "Error al trazar ruta", e)
        emptyList()
    }
}
