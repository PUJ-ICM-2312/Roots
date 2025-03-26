package com.example.rootsmckp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rootsmckp.R
import com.example.rootsmckp.components.BottomNavBar

@Composable
fun MapScreen(navController: NavHostController) {
    val location = "PUJ"
    val selectedType = "Apartamento"
    val points = listOf(
        Triple(80.dp, 200.dp, "Casa amplia en el centro"),
        Triple(150.dp, 260.dp, "Apartamento moderno"),
        Triple(220.dp, 300.dp, "Residencia compartida"),
        Triple(280.dp, 350.dp, "Estudio minimalista"),
        Triple(180.dp, 400.dp, "Casa con jardín"),
        Triple(100.dp, 450.dp, "Habitación económica")
    )
    var selectedDescription by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("$location, $selectedType", fontWeight = FontWeight.Medium)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.fake_map),
                        contentDescription = "Mapa base",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    points.forEach { (x, y, description) ->
                        Box(
                            modifier = Modifier
                                .offset(x = x, y = y)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                                .clickable { selectedDescription = description },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌿", fontSize = 14.sp)
                        }
                    }

                    selectedDescription?.let { desc ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 100.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = desc, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { /* TODO: Navegar a detalle */ },
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98F5A9))
                                ) {
                                    Text("Ver más", fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { /* centrar ubicación */ },
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            }
        }
    }
}
