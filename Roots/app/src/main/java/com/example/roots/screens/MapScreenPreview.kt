package com.example.roots.screens

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
import com.example.roots.R

@Composable
fun MapScreenPreview() {
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
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.fakemap),
                contentDescription = "Fake Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 12.dp, end = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$location, $selectedType", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Icon(
                    painter = painterResource(id = R.drawable.filter),
                    contentDescription = "Filter",
                    modifier = Modifier.size(36.dp)
                )
            }

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
                            onClick = { /* TODO: acción "ver más" */ },
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98F5A9))
                        ) {
                            Text("Ver más", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Location",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 72.dp)
                    .size(48.dp)
            )
        }
    }
}
