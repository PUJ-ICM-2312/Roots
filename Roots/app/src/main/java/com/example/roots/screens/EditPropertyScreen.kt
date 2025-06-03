package com.example.roots.screens

import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.roots.R
import com.example.roots.components.BottomNavBar
import com.example.roots.model.Inmueble
import com.example.roots.model.TipoInmueble
import com.example.roots.model.TipoPublicacion
import com.example.roots.repository.InmuebleRepository
import com.example.roots.service.InmuebleService
import com.example.roots.ui.theme.RootsTheme
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID
import java.util.concurrent.CancellationException

private fun Double.format(digits: Int): String = "%.${digits}f".format(this)

@Composable
fun EditPropertyScreen(
    navController: NavController,
    propertyId: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    val inmuebleService = remember { InmuebleService(InmuebleRepository()) }

    // 1) Estados para el Inmueble y loading
    var inmueble by remember { mutableStateOf<Inmueble?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var isUploading by remember { mutableStateOf(false) }
    var triggerUpload by remember { mutableStateOf(false) }

    // 2) Campos de formulario
    var direccion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var estrato by remember { mutableStateOf("Seleccionar") }
    var numBanos by remember { mutableStateOf("") }
    var numParqueaderos by remember { mutableStateOf("") }
    var numHabitaciones by remember { mutableStateOf("") }
    var metros by remember { mutableStateOf("") }
    var barrio by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var admin by remember { mutableStateOf("") }
    var antiguedad by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var tipoPublicacion by remember { mutableStateOf(TipoPublicacion.Venta) }
    var tipoInmueble by remember { mutableStateOf(TipoInmueble.Apartamento) }

    // 3) Ubicación
    var propertyLatLng by remember { mutableStateOf<LatLng?>(null) }

    // 4) Imágenes: URLs existentes vs. nuevas URIs
    var existingImageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var newImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // 5) Para seleccionar múltiples imágenes → las vamos agregando en lugar de reemplazar
    val launcherMultiple = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        // newImageUris = uris    // ← ya no
        newImageUris = newImageUris + uris
    }


    // 6) Cargar Inmueble desde Firestore al montar
    LaunchedEffect(propertyId) {
        inmuebleService.obtener(propertyId) { fetched ->
            if (fetched != null) {
                inmueble = fetched
                // rellenar campos con datos existentes
                direccion = fetched.direccion
                precio = String.format(Locale.getDefault(), "%.0f", fetched.precio)
                estrato = fetched.estrato.toString()
                numBanos = fetched.numBanos.toString()
                numParqueaderos = fetched.numParqueaderos.toString()
                numHabitaciones = fetched.numHabitaciones.toString()
                metros = fetched.metrosCuadrados.toString()
                barrio = fetched.barrio
                ciudad = fetched.ciudad
                admin = String.format(Locale.getDefault(), "%.0f", fetched.mensualidadAdministracion)
                antiguedad = fetched.antiguedad.toString()
                descripcion = fetched.descripcion
                tipoPublicacion = fetched.tipoPublicacion
                tipoInmueble = fetched.tipoInmueble
                propertyLatLng = LatLng(fetched.latitud, fetched.longitud)
                existingImageUrls = fetched.fotos
                isLoading = false
            } else {
                // Si no existe, volvemos
                navController.popBackStack()
            }
        }
    }

    // 7) Mientras carga, mostramos indicador
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // 8) Helpers
    fun searchLocationByText(query: String) {
        coroutineScope.launch {
            val list = withContext(Dispatchers.IO) {
                geocoder.getFromLocationName(query, 1)
            }
            if (list.isNullOrEmpty()) {
                Toast.makeText(context, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
            } else {
                val addr = list[0]
                propertyLatLng = LatLng(addr.latitude, addr.longitude)
                Toast.makeText(
                    context,
                    "Ubicación encontrada: ${addr.latitude}, ${addr.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val estratos = (1..6).map { it.toString() }
    val tiposPublicacionStrings = TipoPublicacion.values().map { it.name }
    val tiposInmuebleStrings = TipoInmueble.values().map { it.name }
    var thumbnailToDelete by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(60.dp)
                )
            }

            Text("Editar Inmueble", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // --- Campo: Dirección ---
            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección o Nombre del Edificio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { searchLocationByText(direccion) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar ubicación")
            }
            propertyLatLng?.let { latLng ->
                Text(
                    text = "Lat: ${latLng.latitude.format(6)},  Lng: ${latLng.longitude.format(6)}",
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Ciudad y Barrio ---
            OutlinedTextField(
                value = ciudad,
                onValueChange = { ciudad = it },
                label = { Text("Ciudad") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            OutlinedTextField(
                value = barrio,
                onValueChange = { barrio = it },
                label = { Text("Barrio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            // --- Campos numéricos ---
            NumberField("Precio", precio) { precio = it }
            DropDownSelector("Estrato", estrato, estratos) { estrato = it }
            NumberField("Baños", numBanos) { numBanos = it }
            NumberField("Habitaciones", numHabitaciones) { numHabitaciones = it }
            NumberField("Metros cuadrados", metros) { metros = it }
            NumberField("Parqueaderos", numParqueaderos) { numParqueaderos = it }
            NumberField("Antiguedad en años", antiguedad) { antiguedad = it }
            NumberField("Administración mensual", admin) { admin = it }

            // --- Tipo de publicación e inmueble ---
            DropDownSelector(
                "Tipo de publicación",
                tipoPublicacion.name,
                tiposPublicacionStrings
            ) {
                tipoPublicacion = TipoPublicacion.valueOf(it)
            }
            DropDownSelector(
                "Tipo de inmueble",
                tipoInmueble.name,
                tiposInmuebleStrings
            ) {
                tipoInmueble = TipoInmueble.valueOf(it)
            }

            // --- Descripción ---
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 6
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- Selección de imágenes nuevas ---
            Button(
                onClick = { launcherMultiple.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Agregar Nuevas Imágenes", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))

            // ─── Mostrar todas las miniaturas (URLs existentes + URIs nuevas) con botón “X” ───
            Spacer(modifier = Modifier.height(12.dp))

            // Construimos una lista mixta (String, esUriNueva: Boolean)
            //   - las URLs antiguas pasan como (url, false)
            //   - las URIs nuevas pasan como (uri.toString(), true)
            val allThumbnails = existingImageUrls
                .map { Pair(it, false) } +
                    newImageUris.map { Pair(it.toString(), true) }

            thumbnailToDelete?.let { (thumbString, esUriNueva) ->
                showDeleteImageDialog(
                    urlString        = thumbString,
                    esUriNueva       = esUriNueva,
                    existingImageUrls = existingImageUrls,
                    newImageUris      = newImageUris,
                    onRemoveExisting = { nuevaListaUrls ->
                        existingImageUrls = nuevaListaUrls
                    },
                    onRemoveNewUri   = { nuevaListaUris ->
                        newImageUris = nuevaListaUris
                    }
                ) {
                    // Finalmente, cerramos el diálogo quitando el state:
                    thumbnailToDelete = null
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                items(allThumbnails) { (thumbString, esUriNueva) ->
                    // Cada miniatura va en un Box para superponer el IconButton (X)
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        // 1) Mostrar la imagen (si es URI nueva, convertimos a Uri; si es URL, la dejamos como String)
                        AsyncImage(
                            model = if (esUriNueva) Uri.parse(thumbString) else thumbString,
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // 2) IconButton “X” en la esquina superior izquierda
                        IconButton(
                            onClick = {
                                // Abrimos un AlertDialog para confirmar eliminación
                                thumbnailToDelete = Pair(thumbString, esUriNueva)
                            },
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.TopStart)
                                .background(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar foto",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Botón “Actualizar inmueble” ---
            Button(
                onClick = {
                    // Solo si no estamos ya subiendo, activamos la señal:
                    if (!isUploading) {
                        triggerUpload = true
                    }
                },
                enabled = !isUploading, // Deshabilita el botón mientras isUploading == true
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2DFDB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isUploading) "Actualizando..." else "Actualizar Inmueble",
                    color = Color.Black
                )
            }

            // ────────────────────────────────────────────────────────────────────
            //  LaunchedEffect que se activa cuando “triggerUpload” pasa a true
            // ────────────────────────────────────────────────────────────────────
            LaunchedEffect(triggerUpload) {
                if (!triggerUpload) return@LaunchedEffect

                isUploading = true

                try {
                    /******************************************************************************
                     * Paso 1: Inicializamos finalUrls con las URLs antiguas que aún queremos conservar.
                     *         (existingImageUrls ya ha sido filtrado por el diálogo de eliminación)
                     ******************************************************************************/
                    val finalUrls = existingImageUrls.toMutableList()

                    /******************************************************************************
                     * Paso 2: Si hay nuevas URIs (newImageUris), las subimos en paralelo y las
                     *         agregamos a finalUrls.
                     ******************************************************************************/
                    if (newImageUris.isNotEmpty()) {
                        val storage = FirebaseStorage.getInstance()

                        // Creamos un Deferred por cada URI, para subirlas concurrentemente
                        val uploadJobs: List<Deferred<String>> = newImageUris.map { uri ->
                            async(Dispatchers.IO) {
                                val fileName = UUID.randomUUID().toString()
                                val ref = storage.reference.child("inmuebles/$fileName.jpg")
                                // sube la URI y espera el resultado
                                ref.putFile(uri).await()
                                // obtiene la URL pública
                                ref.downloadUrl.await().toString()
                            }
                        }
                        // Esperamos a que todas terminen y obtenemos las URLs resultantes
                        val nuevasUrls = uploadJobs.awaitAll()
                        // Agregamos esas nuevas URLs a la lista final
                        finalUrls.addAll(nuevasUrls)
                    }

                    /******************************************************************************
                     * Paso 3: Validar que exista la ubicación y campos obligatorios, igual que antes
                     ******************************************************************************/
                    val lat = propertyLatLng?.latitude
                    val lng = propertyLatLng?.longitude
                    if (lat == null || lng == null) {
                        Toast.makeText(context, "Primero busca la ubicación", Toast.LENGTH_SHORT).show()
                        return@LaunchedEffect
                    }

                    if (direccion.isBlank() ||
                        ciudad.isBlank() ||
                        barrio.isBlank() ||
                        precio.isBlank() ||
                        descripcion.isBlank()
                    ) {
                        Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                        return@LaunchedEffect
                    }

                    // Convertir a números
                    val precioFloat = precio.toFloatOrNull() ?: 0f
                    val estratoInt = estrato.toIntOrNull() ?: 0
                    val banosInt = numBanos.toIntOrNull() ?: 0
                    val parqueaderosInt = numParqueaderos.toIntOrNull() ?: 0
                    val habitacionesInt = numHabitaciones.toIntOrNull() ?: 0
                    val metrosFloat = metros.toFloatOrNull() ?: 0f
                    val adminFloat = admin.toFloatOrNull() ?: 0f
                    val antiguedadInt = antiguedad.toIntOrNull() ?: 0

                    // Mantener userId y fechaPublicacion del inmueble original
                    val original = inmueble!!
                    val userId = original.usuarioId

                    // Construir el Inmueble actualizado usando “finalUrls” (que ya contiene todas las imágenes)
                    val updatedInmueble = Inmueble(
                        id = original.id,
                        direccion = direccion,
                        precio = precioFloat,
                        estrato = estratoInt,
                        numBanos = banosInt,
                        numParqueaderos = parqueaderosInt,
                        numHabitaciones = habitacionesInt,
                        metrosCuadrados = metrosFloat,
                        barrio = barrio,
                        ciudad = ciudad,
                        descripcion = descripcion,
                        fotos = finalUrls,                   // <-- aquí guardamos la lista combinada
                        tipoPublicacion = tipoPublicacion,
                        tipoInmueble = tipoInmueble,
                        numFavoritos = original.numFavoritos,
                        mensualidadAdministracion = adminFloat,
                        antiguedad = antiguedadInt,
                        fechaPublicacion = original.fechaPublicacion,
                        latitud = lat,
                        longitud = lng,
                        usuarioId = userId
                    )

                    // Finalmente llamamos a Firestore para actualizar
                    inmuebleService.actualizar(updatedInmueble) { success ->
                        if (success) {
                            Toast.makeText(context, "Inmueble actualizado", Toast.LENGTH_SHORT).show()
                            navController.navigate("${Screen.PropertyScrollMode.route}/${updatedInmueble.id}")
                        } else {
                            Toast.makeText(context, "Error al actualizar inmueble", Toast.LENGTH_SHORT).show()
                        }
                    }

                } catch (e: CancellationException) {
                    // Si se canceló el coroutine, no hacemos nada
                } finally {
                    isUploading = false
                    triggerUpload = false
                }
            }



            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
/**
 * Muestra un AlertDialog para confirmar la eliminación de una miniatura.
 * - urlString: la cadena (URI.toString() o URL) de la miniatura a borrar
 * - esUriNueva: si es URI (true) o URL (false)
 * - existingImageUrls: lista actual de URLs guardadas en Firestore
 * - newImageUris: lista actual de URIs nuevas seleccionadas
 * - onRemoveExisting: lambda que recibe la nueva lista de URLs antiguas (sin la que borramos)
 * - onRemoveNewUri: lambda que recibe la nueva lista de URIs nuevas (sin la que borramos)
 */

@Composable
private fun showDeleteImageDialog(
    urlString: String,
    esUriNueva: Boolean,
    existingImageUrls: List<String>,
    newImageUris: List<Uri>,
    onRemoveExisting: (List<String>) -> Unit,
    onRemoveNewUri: (List<Uri>) -> Unit,
    onDialogClose: () -> Unit            // <<< nuevo parámetro
) {
    var openDialog by remember { mutableStateOf(true) }
    if (!openDialog) {
        // Cerramos el diálogo y notificamos al caller que lo cierre también
        onDialogClose()
        return
    }

    AlertDialog(
        onDismissRequest = {
            openDialog = false
            // Notificamos al cerrar:
            onDialogClose()
        },
        title   = { Text("¿Deseas eliminar esta foto?") },
        text    = { Text("No podrás recuperarla luego.") },
        confirmButton = {
            TextButton(onClick = {
                if (esUriNueva) {
                    // Filtramos la URI que coincide con urlString
                    val nuevaListaUris = newImageUris.filter { it.toString() != urlString }
                    onRemoveNewUri(nuevaListaUris)
                } else {
                    // Filtramos la URL que coincide con urlString
                    val nuevaListaUrls = existingImageUrls.filter { it != urlString }
                    onRemoveExisting(nuevaListaUrls)
                }
                openDialog = false
                onDialogClose()
            }) {
                Text("Sí, eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                openDialog = false
                onDialogClose()
            }) {
                Text("Cancelar")
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun PreviewEditProperty() {
    // No se puede probar sin Firestore; podrías crear un Inmueble ficticio si lo deseas
    RootsTheme {
        // EditPropertyScreen(navController = rememberNavController(), propertyId = "demoId")
    }
}