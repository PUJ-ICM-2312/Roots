package com.example.roots.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// Asegúrate de importar el BottomNavBar desde el paquete correcto
import com.example.roots.screens.BottomNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RealMapScreen() {
    val initialPosition = LatLng(4.6483, -74.2479) // Bogotá ejemplo
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPosition, 12f)
    }

    val markers = listOf(
        LatLng(4.65, -74.25) to "Casa amplia en el centro",
        LatLng(4.66, -74.24) to "Apartamento moderno",
        LatLng(4.64, -74.23) to "Residencia compartida",
        LatLng(4.63, -74.22) to "Estudio minimalista",
        LatLng(4.62, -74.21) to "Casa con jardín",
        LatLng(4.61, -74.20) to "Habitación económica"
    )

    Scaffold(
        bottomBar = { BottomNavBar() },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null)
                        Text(" PUJ, Apartamento", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                markers.forEach { (position, title) ->
                    Marker(
                        state = MarkerState(position = position),
                        title = title,
                        snippet = "Ver más..."
                    )
                }
            }

            FloatingActionButton(
                onClick = { /* TODO: centrar ubicación */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 72.dp),
                containerColor = Color.White
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Ubicación")
            }
        }
    }
}