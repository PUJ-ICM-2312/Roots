package com.example.roots.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.components.BottomNavBar
import com.example.roots.ui.theme.RootsTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.example.roots.data.MockInmuebles
import com.example.roots.model.Inmueble


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealMapScreen(navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    // Dentro de RealMapScreen, justo antes del GoogleMap:
    val inmuebles = remember { MockInmuebles.sample }


    // Estado para la ubicación del usuario
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // Configuración de la solicitud de ubicación
    val locationRequest = remember {
        LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }

    // Lanzador para solicitar permisos
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Si se otorgan los permisos, obtener la ubicación
            getCurrentLocation(fusedLocationClient) { location ->
                userLocation = LatLng(location.latitude, location.longitude)
            }

            // Solicitar actualizaciones continuas de ubicación
            requestLocationUpdates(fusedLocationClient, locationRequest) { location ->
                userLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }

    // Solicitar permisos al iniciar el componente
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Posición por defecto (Bogotá)
    val defaultPosition = LatLng(4.6483, -74.2479)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation ?: defaultPosition, 12f)
    }

    // Mover la cámara cuando se actualiza la ubicación del usuario
    LaunchedEffect(userLocation) {
        userLocation?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
        }
    }

    /*val markers = listOf(
        LatLng(4.6275, -74.0638) to "Apartamento cerca de la Cl. 40a",
        LatLng(4.6281, -74.0643) to "Casa amplia en Chapinero",
        LatLng(4.6268, -74.0625) to "Estudio en edificio moderno",
        LatLng(4.6259, -74.0651) to "Apartamento con vista a parque",
        LatLng(4.6290, -74.0620) to "Penthouse en zona exclusiva",
        LatLng(4.6245, -74.0640) to "Habitación económica cerca de transporte"
    )*/

    Scaffold(
        bottomBar = { BottomNavBar(navController) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Text(" PUJ, Apartamento", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false // Deshabilitamos el botón por defecto
                ),
                properties = MapProperties(
                    isMyLocationEnabled = userLocation != null
                )
            ) {
                // Marcadores de propiedades
               /* markers.forEach { (position, title) ->
                    Marker(
                        state = MarkerState(position = position),
                        title = title,
                        snippet = "Ver más..."
                    )
                }*/
                inmuebles.forEach { inmueble ->
                    Marker(
                        state   = MarkerState(LatLng(inmueble.latitud, inmueble.longitud)),
                        title   = inmueble.direccion,
                        snippet = "₡${inmueble.precio} • ${inmueble.metrosCuadrados}m²",
                        onClick = {
                            // Navegar al detalle del inmueble
                            navController.navigate("${Screen.PropertyScrollMode.route}/${inmueble.id}")
                            true
                        }
                    )
                }


                // Marcador de la ubicación del usuario
                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Tu ubicación",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }
            }

            // Botón flotante para centrar en la ubicación del usuario
            FloatingActionButton(
                onClick = {
                    userLocation?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 72.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Centrar en mi ubicación")
            }
        }
    }
}

// Función para obtener la ubicación actual una vez
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location) -> Unit
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            location?.let(onLocationReceived)
        }
}

// Función para solicitar actualizaciones continuas de ubicación
@SuppressLint("MissingPermission")
private fun requestLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    onLocationUpdate: (Location) -> Unit
) {
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let(onLocationUpdate)
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMap() {
    RootsTheme {
        RealMapScreen(navController = rememberNavController())
    }
}
