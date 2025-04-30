package com.example.roots.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roots.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(navController: NavController) {
    val estados = listOf("Todos", "Nuevos", "Usados")
    val tipoOferta = listOf("Venta", "Arriendo")
    val tipoInmueble = listOf("Casa", "Apartamento", "Apartaestudio", "Cabaña", "Casa Campestre", "Casa Lote", "Finca", "Habitacion", "Lote", "Bodega", "Consultorio", "Local", "Oficina", "Parqueadero", "Edificio")
    val habitaciones = listOf("1 Habitacion", "2 Habitaciones", "3 Habitaciones", "4 Habitaciones", "5+ Habitaciones")
    val banos = listOf("1 Baño", "2 Baños", "3+ Baños")
    val estratos = listOf("Estrato 1", "Estrato 2", "Estrato 3", "Estrato 4", "Estrato 5", "Estrato 6", "Campestre")
    val antiguedad = listOf("Todos", "Menores a 1 año", "de 1 a 8 años", "De 9 a 15 años", "De 16 a 30 años", "Mas de 30 años")
    val fechaPublicacion = listOf("Hoy", "Desde ayer", "Ultima semana", "Ultimos 15 dias", "Ultimos 30 dias", "Ultimos 40 dias")

    var state by remember { mutableStateOf(FilterState()) }

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
                    TextButton(onClick = { state = FilterState() }) {
                        Text("Limpiar")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { /* aplicar filtros */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE082))
            ) {
                Text("Ver propiedades", fontWeight = FontWeight.Bold)
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
            DropDownField("Estado", estados, state.selectedEstado) { state = state.copy(selectedEstado = it) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Ocultar propiedades vistas", modifier = Modifier.weight(1f))
                Switch(checked = state.ocultarVistos, onCheckedChange = { state = state.copy(ocultarVistos = it) })
            }
            SingleSelectChips("Tipo de Oferta", tipoOferta, state.selectedTipoOferta) { state = state.copy(selectedTipoOferta = it) }
            MultiSelectChips("Tipo de Inmueble", tipoInmueble, state.selectedTipoInmueble)
            MultiSelectChips("Habitaciones", habitaciones, state.selectedHabitaciones)
            MultiSelectChips("Baños", banos, state.selectedBanios)
            MultiSelectChips("Estrato", estratos, state.selectedEstratos)

            Text("Precio", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.precioMin,
                    onValueChange = { state = state.copy(precioMin = it) },
                    label = { Text("Min $") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.precioMax,
                    onValueChange = { state = state.copy(precioMax = it) },
                    label = { Text("Max $") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            SingleSelectChips("Antigüedad", antiguedad, state.selectedAntiguedad) { state = state.copy(selectedAntiguedad = it) }

            Text("Área", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.areaMin,
                    onValueChange = { state = state.copy(areaMin = it) },
                    label = { Text("Min m²") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = state.areaMax,
                    onValueChange = { state = state.copy(areaMax = it) },
                    label = { Text("Max m²") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Parqueadero", modifier = Modifier.weight(1f))
                Switch(checked = state.parqueadero, onCheckedChange = { state = state.copy(parqueadero = it) })
            }

            SingleSelectChips("Fecha de Publicación", fechaPublicacion, state.selectedFecha) { state = state.copy(selectedFecha = it) }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Stable
data class FilterState(
    val selectedEstado: String = "Todos",
    val ocultarVistos: Boolean = false,
    val selectedTipoOferta: String = "Venta",
    val selectedTipoInmueble: MutableList<String> = mutableStateListOf(),
    val selectedHabitaciones: MutableList<String> = mutableStateListOf(),
    val selectedBanios: MutableList<String> = mutableStateListOf(),
    val selectedEstratos: MutableList<String> = mutableStateListOf(),
    val selectedAntiguedad: String = "Todos",
    val selectedFecha: String = "Hoy",
    val parqueadero: Boolean = false,
    val precioMin: String = "",
    val precioMax: String = "",
    val areaMin: String = "",
    val areaMax: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownField(label: String, items: List<String>, selected: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
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
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiSelectChips(label: String, options: List<String>, selected: MutableList<String>) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val isSelected = option in selected
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selected.remove(option)
                        else selected.add(option)
                    },
                    label = { Text(option) },
                    shape = RoundedCornerShape(6.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SingleSelectChips(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(label, fontWeight = FontWeight.Bold)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                FilterChip(
                    selected = selected == option,
                    onClick = { onSelect(option) },
                    label = { Text(option) },
                    shape = RoundedCornerShape(50)
                )
            }
        }
    }
}