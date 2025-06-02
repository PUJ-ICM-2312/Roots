package com.example.roots.screens

import android.location.Geocoder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.navigation.compose.rememberNavController
import com.example.roots.R
import com.example.roots.service.InmuebleService
import com.example.roots.components.BottomNavBar
import com.example.roots.model.Inmueble
import com.example.roots.model.TipoInmueble
import com.example.roots.model.TipoPublicacion
import com.example.roots.ui.theme.RootsTheme
import saveImageToInternalStorage
import com.example.roots.repository.InmuebleRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


private fun Double.format(digits: Int): String = "%.\${digits}f".format(this)

@Composable
fun AddPropertyScreen(navController: NavController) {
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
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var propertyLatLng by remember { mutableStateOf<LatLng?>(null) }

    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val launcherMultiple = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        imageUris = uris
    }

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
                Toast.makeText(context, "Ubicación encontrada: \${addr.latitude}, \${addr.longitude}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val estratos = (1..6).map { it.toString() }
    val tiposPublicacionStrings = TipoPublicacion.values().map { it.name }
    val tiposInmuebleStrings = TipoInmueble.values().map { it.name }

    val inmuebleService = remember { InmuebleService(InmuebleRepository()) }

    Scaffold(bottomBar = { BottomNavBar(navController) }) {
        Column(
            modifier = Modifier
                .padding(it)
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

            Text("Agregar Inmueble", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección o Nombre del Edificio") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = { searchLocationByText(direccion) }, modifier = Modifier.fillMaxWidth()) {
                Text("Buscar ubicación")
            }

            propertyLatLng?.let { latLng ->
                Text(
                    text = "Lat: \${latLng.latitude.format(6)},  Lng: \${latLng.longitude.format(6)}",
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(value = ciudad, onValueChange = { ciudad = it }, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp))
            OutlinedTextField(value = barrio, onValueChange = { barrio = it }, label = { Text("Barrio") }, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp))

            NumberField("Precio", precio) { precio = it }
            DropDownSelector("Estrato", estrato, estratos) { estrato = it }
            NumberField("Baños", numBanos) { numBanos = it }
            NumberField("Habitaciones", numHabitaciones) { numHabitaciones = it }
            NumberField("Metros cuadrados", metros) { metros = it }
            NumberField("Parqueaderos", numParqueaderos) { numParqueaderos = it }
            NumberField("Antiguedad en años", antiguedad) { antiguedad = it }
            NumberField("Administración mensual", admin) { admin = it }

            DropDownSelector("Tipo de publicación", tipoPublicacion.name, tiposPublicacionStrings) {
                tipoPublicacion = TipoPublicacion.valueOf(it)
            }
            DropDownSelector("Tipo de inmueble", tipoInmueble.name, tiposInmuebleStrings) {
                tipoInmueble = TipoInmueble.valueOf(it)
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 6
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { launcherMultiple.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Seleccionar Imagen", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (imageUris.isNotEmpty()) {
                LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    items(imageUris) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).padding(end = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Button(
                onClick = {
                    // 1. Validar que haya imágenes
                    val paths = imageUris.mapNotNull { saveImageToInternalStorage(context, it) }
                    if (paths.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Selecciona al menos una imagen válida",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    // 2. Validar ubicación
                    val lat = propertyLatLng?.latitude
                    val lng = propertyLatLng?.longitude
                    if (lat == null || lng == null) {
                        Toast.makeText(
                            context,
                            "Primero busca la ubicación",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    // 3. Validar campos obligatorios básicos
                    if (direccion.isBlank() || ciudad.isBlank() || barrio.isBlank() ||
                        precio.isBlank() || descripcion.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Completa todos los campos obligatorios",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    // Convertir a números seguros
                    val precioFloat = precio.toFloatOrNull() ?: 0f
                    val estratoInt = estrato.toIntOrNull() ?: 0
                    val banosInt = numBanos.toIntOrNull() ?: 0
                    val parqueaderosInt = numParqueaderos.toIntOrNull() ?: 0
                    val habitacionesInt = numHabitaciones.toIntOrNull() ?: 0
                    val metrosFloat = metros.toFloatOrNull() ?: 0f
                    val adminFloat = admin.toFloatOrNull() ?: 0f
                    val antiguedadInt = antiguedad.toIntOrNull() ?: 0

                    // Crear el objeto Inmueble con id = ""
                    val nuevoInmueble = Inmueble(
                        id = "",  // <-- dejamos vacío para que Firestore genere el ID
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
                        fotos = paths,
                        tipoPublicacion = tipoPublicacion,
                        tipoInmueble = tipoInmueble,
                        numFavoritos = 0,
                        mensualidadAdministracion = adminFloat,
                        antiguedad = antiguedadInt,
                        fechaPublicacion = System.currentTimeMillis(),
                        latitud = lat,
                        longitud = lng
                    )

                    // LLAMADA AL SERVICIO QUE ESCRIBE EN FIRESTORE
                    inmuebleService.crear(nuevoInmueble) { generatedId ->
                        if (generatedId != null) {
                            // Si Firestore devolvió un ID válido, navegamos con ese ID
                            navController.navigate("PropertyScrollMode/$generatedId")
                        } else {
                            // Hubo error al escribir en Firestore
                            Toast.makeText(
                                context,
                                "Error al guardar el inmueble. Intenta nuevamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB2DFDB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Inmueble", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun NumberField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onValueChange(it) },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownSelector(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onSelect(option)
                    expanded = false
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddProperty() {
    RootsTheme {
        AddPropertyScreen(navController = rememberNavController())
    }
}
