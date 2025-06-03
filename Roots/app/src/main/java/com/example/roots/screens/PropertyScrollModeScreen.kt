package com.example.roots.screens

import android.annotation.SuppressLint
import android.location.Location
import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.roots.components.BottomNavBar
import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository
import com.example.roots.service.ChatService
import com.example.roots.service.InmuebleService
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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.service.LoginService
import com.example.roots.ui.theme.RootsTheme

val inmuebleRepository = InmuebleRepository()
val inmuebleService = InmuebleService(inmuebleRepository)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PropertyScrollModeScreen(
    navController: NavController,
    propertyId: String // <— ahora String, no Int
) {
    // 1) Estado local para el inmueble que viene de Firestore
    val inmuebleService = remember { InmuebleService(InmuebleRepository()) }
    var inmueble by remember { mutableStateOf<Inmueble?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // 2) Al montar la pantalla, pedimos a Firestore que nos devuelva el Inmueble
    LaunchedEffect(propertyId) {
        inmuebleService.obtener(propertyId) { fetched ->
            if (fetched != null) {
                inmueble = fetched
                isLoading = false
            } else {
                // Si no existe o hay error, volvemos atrás
                navController.popBackStack()
            }
        }
    }

    // 3) Mientras carga, mostramos un ProgressIndicator centrado
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 4) A estas alturas, `inmueble` no es nulo (lo hemos validado en el LaunchEffect)
    val item = inmueble!!

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        // Aplicamos innerPadding para que el contenido no quede tapado por la bottomBar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ImageCarousel(item)
            PropertyHeaderInfo(item)
            PropertyDescription(item)
            PropertyLocation(item)
            PropertyMap(item)

            // ——————————————
            // Aquí agregamos la caja (Box) que contendrá los botones,
            // con un fondo claro y padding, separada del mapa.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5)) // color de fondo claro para distinguir
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                ContactAndLikeButtons(navController = navController, inmueble = item)
            }
            // ——————————————

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
        // Asumimos que fotos es lista de URLs (Strings). Si antes había enteros, Firebase devuelve Strings.
        AsyncImage(
            model = item,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
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
        position = CameraPosition.fromLatLngZoom(
            LatLng(inmueble.latitud, inmueble.longitud),
            15f
        )
    }

    LaunchedEffect(Unit) {
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val origin = LatLng(it.latitude, it.longitude)
                    userLocation = origin

                    // Reemplaza "YOUR_API_KEY" por tu clave de Directions API
                    val url =
                        "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${inmueble.latitud},${inmueble.longitud}&key=AIzaSyDKjhqaBtcvLF4zW_VsHkXZYi3y4lCWeh0"
                    val client = OkHttpClient()
                    val request = Request.Builder().url(url).build()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = client.newCall(request).execute()
                            val body = response.body?.string()

                            body?.let { json ->
                                val jsonObject = JSONObject(json)
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
            Marker(
                state = MarkerState(position = LatLng(inmueble.latitud, inmueble.longitud)),
                title = "Inmueble",
                snippet = inmueble.direccion
            )
            userLocation?.let {
                Marker(state = MarkerState(position = it), title = "Tú")
            }
            if (routePoints.isNotEmpty()) {
                Polyline(points = routePoints, color = Color.Blue, width = 6f)
            }
        }

        IconButton(
            onClick = {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    LatLng(inmueble.latitud, inmueble.longitud),
                    15f
                )
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
fun ContactAndLikeButtons(
    navController: NavController,
    inmueble: Inmueble
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    if (currentUserId.isNullOrBlank()) return

    val chatService = remember { ChatService() }
    // 1) Estado local para el usuario cargado desde Firestore
    var usuario by remember { mutableStateOf<com.example.roots.model.Usuario?>(null) }
    // 2) Estado local para isLiked, se inicializará luego de cargar el usuario
    var isLiked by remember { mutableStateOf(false) }
    // 3) Estado para forzar recuento de "likes"
    var likesCount by remember { mutableStateOf(inmueble.numFavoritos) }

    // 4) Cuando se monte este Composable, cargamos el usuario y calculamos isLiked:
    LaunchedEffect(currentUserId, inmueble.id) {
        usuarioService.obtener(currentUserId) { fetchedUser ->
            if (fetchedUser != null) {
                usuario = fetchedUser
                // Verificamos si el inmueble está en su lista de favoritos (comparamos ids)
                val yaEsFavorito = fetchedUser.favoritos.any { it.id == inmueble.id }
                isLiked = yaEsFavorito
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ———— Botón “Contactar” ————
        Button(
            onClick = {
                val ownerUserId = inmueble.usuarioId
                val fotoThumbnail = inmueble.fotos.firstOrNull().orEmpty()
                val barrio = inmueble.barrio

                chatService.createOrGetChat(
                    currentUserId = currentUserId,
                    ownerUserId = ownerUserId,
                    propertyId = inmueble.id,
                    propertyFoto = fotoThumbnail,
                    propertyBarrio = barrio
                ) { chatId ->
                    if (chatId != null) {
                        navController.navigate("chat_room/$chatId")
                    } else {
                        Toast.makeText(
                            context,
                            "No se pudo iniciar el chat. Intenta más tarde.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD5FDE5),
                contentColor = Color.Black
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.ChatBubble, contentDescription = "Contactar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Contactar", fontWeight = FontWeight.Bold)
        }

        // ———— Botón “Me Gusta” con contador al lado del icono ————
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(if (isLiked) Color(0xFFFFE5E5) else Color(0xFFF0F0F0))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    // Asegurarse de que el usuario ya esté cargado
                    val u = usuario ?: return@IconButton

                    // 1) Invertimos el estado local
                    isLiked = !isLiked

                    if (isLiked) {
                        // Convertimos la lista inmutable a mutable para poder agregar
                        val nuevosFavoritos = u.favoritos.toMutableList().apply { add(inmueble) }
                        val usuarioActualizado = u.copy(favoritos = nuevosFavoritos)
                        // Actualizamos numFavoritos localmente
                        inmueble.numFavoritos += 1
                        // Persistimos en Firestore: usuario y inmueble
                        usuarioService.actualizar(usuarioActualizado)
                        inmuebleService.actualizar(inmueble) { success ->
                            if (success) {
                                likesCount = inmueble.numFavoritos
                                usuario = usuarioActualizado
                            } else {
                                // Si falla, revertir cambios
                                inmueble.numFavoritos -= 1
                                isLiked = false
                                likesCount = inmueble.numFavoritos
                            }
                        }
                    } else {
                        // Si estaba en favoritos, lo quitamos
                        val nuevosFavoritos = u.favoritos.toMutableList().apply {
                            removeAll { it.id == inmueble.id }
                        }
                        val usuarioActualizado = u.copy(favoritos = nuevosFavoritos)
                        inmueble.numFavoritos -= 1
                        usuarioService.actualizar(usuarioActualizado)
                        inmuebleService.actualizar(inmueble) { success ->
                            if (success) {
                                likesCount = inmueble.numFavoritos
                                usuario = usuarioActualizado
                            } else {
                                // Si falla, revertir cambios
                                inmueble.numFavoritos += 1
                                isLiked = true
                                likesCount = inmueble.numFavoritos
                            }
                        }
                    }
                }
            ) {
                val icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                val tint = if (isLiked) Color.Red else Color.Gray
                Icon(
                    imageVector = icon,
                    contentDescription = if (isLiked) "Ya te gusta" else "Me gusta",
                    tint = tint
                )
            }

            // Texto con la cantidad de “likes” al lado del icono
            Text(
                text = likesCount.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isLiked) Color.Red else Color.Gray
            )
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
        // En el preview no podemos llamar a Firestore. Creamos un Inmueble ficticio:
        val dummy = Inmueble(
            id = "demo",
            direccion = "Demo Calle 123",
            barrio = "Demo Barrio",
            ciudad = "Demo Ciudad",
            precio = 500000.0f,
            mensualidadAdministracion = 50000.0f,
            estrato = 3,
            numBanos = 2,
            numParqueaderos = 1,
            numHabitaciones = 3,
            metrosCuadrados = 80.0f,
            antiguedad = 2,
            numFavoritos = 0,
            descripcion = "Este es un inmueble de demostración.",
            fotos = listOf(
                "https://via.placeholder.com/400x300.png",
                "https://via.placeholder.com/400x300.png"
            ),
            fechaPublicacion = System.currentTimeMillis(),
            latitud = 4.6515,
            longitud = -74.0628,
            tipoPublicacion = com.example.roots.model.TipoPublicacion.Venta,
            tipoInmueble = com.example.roots.model.TipoInmueble.Apartamento
        )
        // Como Preview, forzamos que no sea nulo:
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ImageCarousel(dummy)
            PropertyHeaderInfo(dummy)
            PropertyDescription(dummy)
            PropertyLocation(dummy)
            PropertyMap(dummy)
            ContactAndLikeButtons(navController = rememberNavController(), inmueble = dummy)
            Spacer(modifier = Modifier.height(80.dp))
        }
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
