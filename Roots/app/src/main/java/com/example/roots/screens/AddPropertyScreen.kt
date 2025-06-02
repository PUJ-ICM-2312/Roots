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
import com.example.roots.R
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.components.BottomNavBar
import com.example.roots.model.`Inmueble.kt`
import com.example.roots.model.TipoInmueble
import com.example.roots.model.TipoPublicacion
import com.example.roots.ui.theme.RootsTheme
import saveImageToInternalStorage
import com.example.roots.data.InmuebleRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


private fun Double.format(digits: Int): String =
    "%.${digits}f".format(this)


@Composable
fun AddPropertyScreen(navController: NavController) {
    var direccion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var estrato by remember { mutableStateOf("Seleccionar") }
    var numBanos by remember { mutableStateOf("") }
    var numParqueaderos by remember { mutableStateOf("")}
    var numHabitaciones by remember { mutableStateOf("") }
    var metros by remember { mutableStateOf("") }
    var barrio by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var admin by remember { mutableStateOf("") }
    var antiguedad by remember { mutableStateOf("")}
    var fechaPublicacion by remember { mutableStateOf("")}
    var descripcion by remember { mutableStateOf("") }
    var tipoPublicacion by remember { mutableStateOf(TipoPublicacion.Venta) }
    var tipoInmueble     by remember { mutableStateOf(TipoInmueble.Apartamento) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var propertyLatLng by remember { mutableStateOf<LatLng?>(null) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    // Estado para mantener todas las URIs seleccionadas
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

// Launcher para múltiples selecciones
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
                Toast.makeText(context, "Ubicación encontrada: ${addr.latitude}, ${addr.longitude}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val estratos = (1..6).map { it.toString() }
    val tiposPublicacionStrings = TipoPublicacion.values().map { it.name }
    val tiposInmuebleStrings    = TipoInmueble.values().map    { it.name }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(60.dp)
                        .padding(start = 0.dp)
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

            // 2) Botón para geocodificar esa dirección
            Button(
                onClick = { searchLocationByText(direccion) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar ubicación")
            }

            // 3) Mostrar resultado si ya se encontró
            propertyLatLng?.let { latLng ->
                Text(
                    text = "Lat: ${latLng.latitude.format(6)},  Lng: ${latLng.longitude.format(6)}",
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

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

            NumberField("Precio", precio) { precio = it }
            DropDownSelector(
                label    = "Estrato",
                selected = estrato,
                options  = estratos
            ) { nuevo ->
                estrato = nuevo
            }
            NumberField("Baños", numBanos) { numBanos = it }
            NumberField("Habitaciones", numHabitaciones) { numHabitaciones = it }
            NumberField("Metros cuadrados", metros) { metros = it }
            NumberField("Parqueaderos", numParqueaderos) { numParqueaderos = it }


            NumberField("Antiguedad en años", antiguedad) { antiguedad = it }


            NumberField("Administración mensual", admin) { admin = it }

            DropDownSelector(
                label    = "Tipo de publicación",
                selected = tipoPublicacion.name,
                options  = tiposPublicacionStrings
            ) { seleccionado ->
                tipoPublicacion = TipoPublicacion.valueOf(seleccionado)
            }
            Spacer(modifier = Modifier.height(8.dp))
            DropDownSelector(
                label    = "Tipo de inmueble",
                selected = tipoInmueble.name,
                options  = tiposInmuebleStrings
            ) { seleccionado ->
                tipoInmueble = TipoInmueble.valueOf(seleccionado)
            }

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

            Button(
                onClick = {
                    launcherMultiple.launch("image/*")
                          },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Seleccionar Imagen", color = Color.Black)
            }

            imageUri?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (imageUris.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    items(imageUris) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = null,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .padding(end = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Button(
                onClick = {
                    // 1) guarda la imagen y obtén la ruta local
                    val paths = imageUris.mapNotNull { uri ->
                        saveImageToInternalStorage(context, uri)
                    }

                    if (paths.isNullOrEmpty()) {
                        // no había imagen seleccionada o hubo error al guardar
                        Toast.makeText(context, "Selecciona al menos una imagen válida", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val lat = propertyLatLng?.latitude
                    val lng = propertyLatLng?.longitude

                    if (lat == null || lng == null) {
                        Toast.makeText(context, "Primero busca la ubicación", Toast.LENGTH_SHORT).show()
                    } else {
                        // Aquí creas y agregas tu Inmueble usando InmuebleRepository.nextId(), path de fotos, etc.
                        val nuevo = `Inmueble.kt`(
                            id                         = InmuebleRepository.nextId(),
                            direccion                  = direccion,
                            precio                     = precio.toFloatOrNull()?:0f,
                            estrato                    = estrato.toIntOrNull()?:0,
                            numBaños                   = numBanos.toIntOrNull()?:0,
                            numParqueaderos            = numParqueaderos.toIntOrNull()?:0,
                            numHabitaciones            = numHabitaciones.toIntOrNull()?:0,
                            metrosCuadrados            = metros.toFloatOrNull()?:0f,
                            barrio                     = barrio,
                            ciudad                     = ciudad,
                            descripcion                = descripcion,
                            fotos                      = paths,
                            tipoPublicacion            = tipoPublicacion,
                            tipoInmueble               = tipoInmueble,
                            numFavoritos               = 0,
                            mensualidadAdministracion  = admin.toFloatOrNull()?:0f,
                            antiguedad                 = antiguedad.toIntOrNull()?:0,
                            fechaPublicacion           = System.currentTimeMillis(),
                            latitud                    = lat,
                            longitud                   = lng
                        )
                        InmuebleRepository.add(nuevo)

                        navController.navigate("${Screen.PropertyScrollMode.route}/${nuevo.id}")
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
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

    Box(Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value        = selected,
            onValueChange= { /*no editable*/ },
            readOnly     = true,
            label        = { Text(label) },
            modifier     = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded        = expanded,
            onDismissRequest= { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text    = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


/*@Composable
fun NumberField(label: String, selected: String, options: List<String>, onSelect: (String) -> Unit) {
    DropDownSelector(label, selected, options, onSelect)
}*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Enum<T>> EnumDropDownSelector(
    label: String,
    selected: T,
    options: List<T>,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = selected.name,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = { expanded = !expanded }           // ← aquí, parámetro nombrado
                ) {
                    Icon(
                        imageVector   = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
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

