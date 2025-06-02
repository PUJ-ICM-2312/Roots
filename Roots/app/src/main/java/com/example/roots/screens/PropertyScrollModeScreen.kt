package com.example.roots.screens

import android.location.Location
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.ui.theme.RootsTheme
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.roots.components.BottomNavBar
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PropertyScrollModeScreen(
    navController: NavController,
    propertyId: Int
) {
    val inmueble = remember(propertyId) {
        InmuebleRepository.inmuebles.firstOrNull { it.id == propertyId }
    } ?: run {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ImageCarousel(inmueble)
            PropertyHeaderInfo(inmueble)
            PropertyDescription(inmueble)
            PropertyLocation(inmueble)
            PropertyMap(inmueble)
            ContactButton(inmueble)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ImageCarousel(inmueble: Inmueble) {
    val pagerState = rememberPagerState(pageCount = { inmueble.fotos.size })

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
    ) { page ->
        val item = inmueble.fotos[page]
        when {
            item is Int -> {
                Image(
                    painter = painterResource(id = item),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            item is String -> {
                AsyncImage(
                    model = item,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Spacer(modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp))
            }
        }
    }

}

@Composable
fun PropertyHeaderInfo(inmueble: Inmueble) {
    val formattedPrice = remember(inmueble.precio) {
        NumberFormat.getNumberInstance(Locale.US).format(inmueble.precio.toLong())
    }
    val formattedPriceAdmin = remember(inmueble.mensualidadAdministracion) {
        NumberFormat.getNumberInstance(Locale.US).format(inmueble.mensualidadAdministracion.toLong())
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(inmueble.direccion, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text("${inmueble.ciudad}, ${inmueble.barrio}", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(10.dp))
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val fechaTexto = sdf.format(Date(inmueble.fechaPublicacion))
        Text("Publicado el $fechaTexto")
        Spacer(modifier = Modifier.height(10.dp))
        val anti = inmueble.antiguedad
        Text("Antigüedad: ${if (anti <= 1) "$anti año" else "$anti años"}")
        Spacer(modifier = Modifier.height(10.dp))
        Text("$ $formattedPrice", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A1A))
        Text("$ $formattedPriceAdmin administración")
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PropertyFeature(Icons.Default.Bed, "${inmueble.numHabitaciones} Habs")
            PropertyFeature(Icons.Default.Shower, "${inmueble.numBanos} Baños")
            PropertyFeature(Icons.Default.SquareFoot, "${inmueble.metrosCuadrados} m²")
            PropertyFeature(Icons.Default.DirectionsCar, "${inmueble.numParqueaderos} Parqueaderos")
        }
    }
}

@Composable
fun PropertyDescription(inmueble: Inmueble) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(inmueble.descripcion.trimIndent())
    }
}

@Composable
fun PropertyLocation(inmueble: Inmueble) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ubicación principal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        Text("${inmueble.barrio}, ${inmueble.ciudad}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun PropertyMap(inmueble: Inmueble) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(inmueble.latitud, inmueble.longitud), 15f)
    }

    LaunchedEffect(Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val origin = LatLng(it.latitude, it.longitude)
                    userLocation = origin

                    val url = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${inmueble.latitud},${inmueble.longitud}&key=YOUR_API_KEY"
                    val client = okhttp3.OkHttpClient()
                    val request = okhttp3.Request.Builder().url(url).build()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = client.newCall(request).execute()
                            val body = response.body?.string()

                            body?.let { json ->
                                val jsonObject = org.json.JSONObject(json)
                                val routes = jsonObject.getJSONArray("routes")
                                if (routes.length() > 0) {
                                    val overviewPolyline = routes
                                        .getJSONObject(0)
                                        .getJSONObject("overview_polyline")
                                        .getString("points")

                                    val decoded = decodePolyline(overviewPolyline)
                                    withContext(Dispatchers.Main) {
                                        routePoints = decoded
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = userLocation != null),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            Marker(state = MarkerState(position = LatLng(inmueble.latitud, inmueble.longitud)), title = "Inmueble", snippet = inmueble.direccion)
            userLocation?.let { Marker(state = MarkerState(position = it), title = "Tú") }
            if (routePoints.isNotEmpty()) {
                Polyline(points = routePoints, color = Color.Blue, width = 6f)
            }
        }

        IconButton(
            onClick = {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(inmueble.latitud, inmueble.longitud), 15f)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(48.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
        ) {
            Icon(Icons.Default.Route, contentDescription = "Ver ruta")
        }
    }
}

@Composable
fun ContactButton(inmueble: Inmueble) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /* TODO: Contactar al dueño */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD5FDE5),
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ChatBubble, contentDescription = "Contactar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Contactar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PropertyFeature(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null)
        Text(text = label)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPropertyScrollMode() {
    RootsTheme {
        val first = InmuebleRepository.inmuebles.first()
        PropertyScrollModeScreen(
            navController = rememberNavController(),
            propertyId = first.id
        )
    }
}

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = mutableListOf<LatLng>()
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
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
        lng += dlng

        val latLng = LatLng(lat / 1E5, lng / 1E5)
        poly.add(latLng)
    }
    return poly
}