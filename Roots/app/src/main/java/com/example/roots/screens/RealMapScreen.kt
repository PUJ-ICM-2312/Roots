package com.example.roots.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.R
import com.example.roots.components.BottomNavBar
import com.example.roots.repository.InmuebleRepository
import com.example.roots.ui.theme.RootsTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import android.util.Log
import com.google.android.gms.maps.model.CameraPosition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.roots.model.Inmueble


@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealMapScreen(navController: NavController) {
    val myInmuebleColor = android.graphics.Color.parseColor("#B0E57C")  // verde claro
    val otherInmuebleColor = android.graphics.Color.parseColor("#a2f9fd") // azul claro
    val currentUserId = remember { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    // Dentro de RealMapScreen, justo antes del GoogleMap:
    val inmuebles = remember { mutableStateOf<List<Inmueble>>(emptyList()) }

    LaunchedEffect(Unit) {
        InmuebleRepository().getAll { list ->
            inmuebles.value = list
        }
    }


    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasCentered by remember { mutableStateOf(false) }
    var compassEnabled by remember { mutableStateOf(false) }
    var azimuth by remember { mutableStateOf(0f) }

    val locationRequest = remember {
        LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getCurrentLocation(fusedLocationClient) { location ->
                userLocation = LatLng(location.latitude, location.longitude)
            }

            requestLocationUpdates(fusedLocationClient, locationRequest) { location ->
                userLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val defaultPosition = LatLng(4.6483, -74.2479)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation ?: defaultPosition, 12f)
    }

    LaunchedEffect(userLocation) {
        if (userLocation != null && !hasCentered) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
            hasCentered = true
        }
    }

    // 🔄 SENSOR DE ROTACIÓN
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    DisposableEffect(compassEnabled) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val degrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuth = (degrees + 360) % 360

                    if (compassEnabled) {
                        val current = cameraPositionState.position
                        cameraPositionState.position =
                            CameraPosition(current.target, current.zoom, current.tilt, azimuth)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (compassEnabled) {
            sensorManager.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
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
            SearchBar(
                onPlaceSelected = { latLng ->
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
                },
                onNavigateToFilter = {
                    navController.navigate(Screen.Filter.route)
                }
            )

        }

    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false
                ),
                properties = MapProperties(
                    isMyLocationEnabled = userLocation != null
                )
            ) {
                inmuebles.value.forEach { inmueble ->
                    val isMine = inmueble.usuarioId == currentUserId
                    val bgColor = if (isMine) myInmuebleColor else otherInmuebleColor
                    val icon = remember(inmueble.id) {
                        bitmapDescriptorFromVector(context, R.drawable.hojamapa, bgColor)
                    }

                    Marker(
                        state = MarkerState(LatLng(inmueble.latitud, inmueble.longitud)),
                        title = inmueble.direccion,
                        snippet = "₡${inmueble.precio} • ${inmueble.metrosCuadrados}m²",
                        icon = icon,
                        onClick = {
                            navController.navigate("${Screen.PropertyScrollMode.route}/${inmueble.id}")
                            true
                        }
                    )
                }


                //markers.forEach { (position, title) ->
                // Marcadores de propiedades
               /* markers.forEach { (position, title) ->
                    Marker(
                        state = MarkerState(position = position),
                        title = title,
                        snippet = "Ver más...",
                        icon = markerIcon
                    )
                }*/


                userLocation?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Tu ubicación",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }
            }

            // 🧭 BOTÓN BRÚJULA
            FloatingActionButton(
                onClick = { compassEnabled = !compassEnabled },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 140.dp),
                containerColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.brujula),
                    contentDescription = if (compassEnabled) "Desactivar brújula" else "Activar brújula",
                    modifier = Modifier.size(24.dp)
                )
            }

            // 📍 BOTÓN CENTRAR
            FloatingActionButton(
                onClick = {
                    userLocation?.let {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 72.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Centrar en mi ubicación")
            }
        }
    }
}

// ✅ UBICACIÓN ACTUAL
@SuppressLint("MissingPermission")
private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (Location) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let(onLocationReceived)
    }
}

// ✅ ACTUALIZACIONES CONTINUAS
@SuppressLint("MissingPermission")
private fun requestLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    onLocationUpdate: (Location) -> Unit
) {
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let(onLocationUpdate)
        }
    }
    fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
}

// ✅ ÍCONO PERSONALIZADO PARA MARCADORES
fun bitmapDescriptorFromVector(context: Context, drawableId: Int, bgColor: Int): BitmapDescriptor {
    val drawable: Drawable = ContextCompat.getDrawable(context, drawableId)!!
    val size = 64
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = android.graphics.Paint().apply {
        color = bgColor
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    val padding = 8
    drawable.setBounds(padding, padding, size - padding, size - padding)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Preview(showBackground = true)
@Composable
fun PreviewMap() {
    RootsTheme {
        RealMapScreen(navController = rememberNavController())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    onPlaceSelected: (LatLng) -> Unit,
    onNavigateToFilter: () -> Unit
) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var expanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    ExposedDropdownMenuBox(
        expanded = expanded && predictions.isNotEmpty(),
        onExpandedChange = { }, // Evita que se cierre solo
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        TextField(
            value = query,
            onValueChange = {
                query = it
                expanded = true
            },
            label = { Text("Buscar ubicación") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            trailingIcon = {
                IconButton(onClick = {
                    onNavigateToFilter()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filtro), // Asegúrate de tener este ícono
                        contentDescription = "Ir a filtros"
                    )
                }
            },
            singleLine = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )

        ExposedDropdownMenu(
            expanded = expanded && predictions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            predictions.forEach { prediction ->
                DropdownMenuItem(
                    text = { Text(prediction.getFullText(null).toString()) },
                    onClick = {
                        expanded = false
                        query = prediction.getFullText(null).toString()

                        val placeId = prediction.placeId
                        val placeRequest = FetchPlaceRequest.builder(
                            placeId, listOf(Place.Field.LAT_LNG)
                        ).build()

                        placesClient.fetchPlace(placeRequest)
                            .addOnSuccessListener { response ->
                                response.place.latLng?.let { latLng ->
                                    onPlaceSelected(latLng)
                                }
                            }

                        focusManager.clearFocus()
                        focusRequester.requestFocus()
                    }
                )
            }
        }
    }

    LaunchedEffect(query) {
        if (query.length >= 3) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setCountries(listOf("CO")) // Opcional: limitar a Colombia
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    predictions = response.autocompletePredictions
                }
                .addOnFailureListener {
                    Log.e("Places", "Autocomplete error", it)
                    predictions = emptyList()
                }
        } else {
            predictions = emptyList()
        }
    }
}
