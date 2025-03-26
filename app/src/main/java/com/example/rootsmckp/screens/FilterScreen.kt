package com.example.rootsmckp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun FilterScreen(navController: NavHostController) {
    var isRenting by remember { mutableStateOf(true) }
    var showPropertyTypes by remember { mutableStateOf(false) }
    val selectedTypes = remember { mutableStateListOf("Apartamento", "Casa") }
    val allTypes = listOf("Apartamento", "Casa", "Residencia estudiantil", "Habitacion", "Apartaestudio")
    var location by remember { mutableStateOf(TextFieldValue("Bogota, Medellin")) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFF5F5F5))
        ) {
            FilterTab("Rentar", isSelected = isRenting) { isRenting = true }
            FilterTab("Comprar", isSelected = !isRenting) { isRenting = false }
        }
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20)).background(Color.White).padding(16.dp).clickable {
                showPropertyTypes = !showPropertyTypes
            },
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (showPropertyTypes) "Selecciona tus filtros" else selectedTypes.joinToString(),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            if (showPropertyTypes) {
                Spacer(Modifier.height(12.dp))
                allTypes.forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Checkbox(
                            checked = selectedTypes.contains(type),
                            onCheckedChange = {
                                if (it) selectedTypes.add(type) else selectedTypes.remove(type)
                            }
                        )
                        Text(type)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            placeholder = { Text("Bogota, Medellin") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            FilterButton("Listado", Icons.Default.List)
            FilterButton("House", Icons.Default.Place)
        }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("map") },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98F5A9))
        ) {
            Text("Buscar", fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun FilterTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bg = if (isSelected) Color(0xFF98F5A9) else Color.Transparent
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(bg).clickable { onClick() }.padding(horizontal = 24.dp, vertical = 12.dp),
        color = Color.Black
    )
}

@Composable
fun FilterButton(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(20)).background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
