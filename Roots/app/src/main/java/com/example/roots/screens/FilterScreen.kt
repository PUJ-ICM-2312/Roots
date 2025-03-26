package com.example.roots.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen() {
    var address by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var rooms by remember { mutableStateOf("") }
    val transactionTypes = listOf("Arriendo", "En venta")
    val propertyTypes = listOf("Casa", "Apartamento", "Aparta-estudio")
    val selectedTransactions = remember { mutableStateMapOf<String, Boolean>().apply { transactionTypes.forEach { put(it, true) } } }
    val selectedProperties = remember { mutableStateMapOf<String, Boolean>().apply { propertyTypes.forEach { put(it, true) } } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Filtros",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección Aprox") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = bathrooms,
            onValueChange = { bathrooms = it },
            label = { Text("# Baños") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rooms,
            onValueChange = { rooms = it },
            label = { Text("# Habitaciones") },
            modifier = Modifier.fillMaxWidth()
        )

        FilterSection("Tipo de Transacción", transactionTypes, selectedTransactions)
        FilterSection("Tipo de Propiedad", propertyTypes, selectedProperties)

        Spacer(modifier = Modifier.height(16.dp))

        IconButton(onClick = { /* Buscar acción */ }){

        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FilterSection(title: String, options: List<String>, selectedOptions: MutableMap<String, Boolean>) {
    Scaffold(
        bottomBar = { BottomNavBar() },
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color(0xFFA8FF98), shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = option, modifier = Modifier.weight(1f))
                    Checkbox(
                        checked = selectedOptions[option] == true,
                        onCheckedChange = { selectedOptions[option] = it }
                    )
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewFiltersScreen() {
    FiltersScreen()
}
