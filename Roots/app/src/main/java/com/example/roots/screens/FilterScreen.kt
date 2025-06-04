package com.example.roots.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.viewmodel.SharedFilterViewModel
import com.example.roots.viewmodel.initialFilterState

@Stable // Asegúrate que FilterState es @Stable si no es ya inmutable
data class FilterState(
    val selectedEstado: String = "Todos",
    val ocultarVistos: Boolean = false,
    val selectedTipoOferta: String = "Venta",
    val selectedTipoInmueble: List<String> = emptyList(), // Usar List inmutable para el estado
    val selectedHabitaciones: List<String> = emptyList(),
    val selectedBanios: List<String> = emptyList(),
    val selectedEstratos: List<String> = emptyList(),
    val selectedAntiguedad: String = "Todos",
    val selectedFecha: String = "Todos",
    val parqueadero: Boolean = false,
    val precioMin: String = "",
    val precioMax: String = "",
    val areaMin: String = "",
    val areaMax: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    sharedFilterViewModel: SharedFilterViewModel = viewModel()
) {
    val estados = listOf("Todos", "Nuevos", "Usados")
    val tipoOferta = listOf("Venta", "Arriendo")
    val tipoInmueble = listOf("Casa", "Apartamento", "Apartaestudio", "Cabaña", "Casa Campestre", "Casa Lote", "Finca", "Habitacion", "Lote", "Bodega", "Consultorio", "Local", "Oficina", "Parqueadero", "Edificio")
    val habitaciones = listOf("1 Habitacion", "2 Habitaciones", "3 Habitaciones", "4 Habitaciones", "5+ Habitaciones")
    val banos = listOf("1 Baño", "2 Baños", "3+ Baños")
    val estratos = listOf("Estrato 1", "Estrato 2", "Estrato 3", "Estrato 4", "Estrato 5", "Estrato 6", "Campestre")
    val antiguedad = listOf("Todos", "Menores a 1 año", "de 1 a 8 años", "De 9 a 15 años", "De 16 a 30 años", "Mas de 30 años")
    val fechaPublicacion = listOf("Todos", "Hoy", "Desde ayer", "Ultima semana", "Ultimos 15 dias", "Ultimos 30 dias", "Ultimos 40 dias")

    // Usar un estado local que se inicializa desde el ViewModel
    // y se actualiza en el ViewModel al aplicar los filtros.
    var localState by remember { mutableStateOf(sharedFilterViewModel.filterState.value.copy()) }

    // Sincronizar el estado local si el estado del ViewModel cambia externamente (ej. por "Limpiar")
    LaunchedEffect(sharedFilterViewModel.filterState.collectAsState()) {
        localState = sharedFilterViewModel.filterState.value.copy()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtros", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        sharedFilterViewModel.clearFilters()
                        // localState se actualizará por el LaunchedEffect
                    }) {
                        Text("Limpiar")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    navController.popBackStack("swipe", inclusive = false)
                    navController.navigate(Screen.Swipe.route) { // Asegúrate que Screen.Swipe.route es correcto
                        // Opcional: configura popUpTo para limpiar el backstack si es necesario
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true // Guarda el estado de la pantalla de inicio (si es tu startDestination)
                        }
                        launchSingleTop = true // Evita múltiples instancias de SwipeScreen
                        restoreState = true // Restaura el estado si se vuelve a SwipeScreen
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE082)) // Ejemplo de color
            ) {
                Text("Ver propiedades", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))
            DropDownField("Estado", estados, localState.selectedEstado) { localState = localState.copy(selectedEstado = it) }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Ocultar propiedades vistas", modifier = Modifier.weight(1f))
                Switch(checked = localState.ocultarVistos, onCheckedChange = { localState = localState.copy(ocultarVistos = it) })
            }
            SingleSelectChips("Tipo de Oferta", tipoOferta, localState.selectedTipoOferta) { localState = localState.copy(selectedTipoOferta = it) }

            MultiSelectChipsField("Tipo de Inmueble", tipoInmueble, localState.selectedTipoInmueble) { newList ->
                localState = localState.copy(selectedTipoInmueble = newList)
            }
            MultiSelectChipsField("Habitaciones", habitaciones, localState.selectedHabitaciones) { newList ->
                localState = localState.copy(selectedHabitaciones = newList)
            }
            MultiSelectChipsField("Baños", banos, localState.selectedBanios) { newList ->
                localState = localState.copy(selectedBanios = newList)
            }
            MultiSelectChipsField("Estrato", estratos, localState.selectedEstratos) { newList ->
                localState = localState.copy(selectedEstratos = newList)
            }

            Text("Precio", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = localState.precioMin,
                    onValueChange = { localState = localState.copy(precioMin = it.filter { char -> char.isDigit() }) },
                    label = { Text("Min $") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = localState.precioMax,
                    onValueChange = { localState = localState.copy(precioMax = it.filter { char -> char.isDigit() }) },
                    label = { Text("Max $") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            SingleSelectChips("Antigüedad", antiguedad, localState.selectedAntiguedad) { localState = localState.copy(selectedAntiguedad = it) }

            Text("Área", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = localState.areaMin,
                    onValueChange = { localState = localState.copy(areaMin = it.filter { char -> char.isDigit() }) },
                    label = { Text("Min m²") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = localState.areaMax,
                    onValueChange = { localState = localState.copy(areaMax = it.filter { char -> char.isDigit() }) },
                    label = { Text("Max m²") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Parqueadero", modifier = Modifier.weight(1f))
                Switch(checked = localState.parqueadero, onCheckedChange = { localState = localState.copy(parqueadero = it) })
            }

            SingleSelectChips("Fecha de Publicación", fechaPublicacion, localState.selectedFecha.ifEmpty { "Todos" }) { selected ->
                localState = localState.copy(selectedFecha = selected)
            }

            Spacer(Modifier.height(80.dp)) // Espacio para el botón inferior
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownField(label: String, items: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onSelect(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectChipsField(label: String, options: List<String>, selectedItems: List<String>, onSelectionChanged: (List<String>) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Espacio vertical entre filas de chips
        ) {
            options.forEach { option ->
                val isSelected = option in selectedItems
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        val newList = selectedItems.toMutableList()
                        if (isSelected) newList.remove(option)
                        else newList.add(option)
                        onSelectionChanged(newList.toList()) // Pasar lista inmutable
                    },
                    label = { Text(option) },
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SingleSelectChips(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(option) },
                    shape = RoundedCornerShape(50) // Forma de píldora
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFiltersScreen() {
    // Para la preview, puedes necesitar un SharedFilterViewModel mockeado o uno real
    val navController = rememberNavController()
    val sharedFilterViewModel: SharedFilterViewModel = viewModel() // Esto funcionará en previews de Compose
    FilterScreen(navController = navController, sharedFilterViewModel = sharedFilterViewModel)
}