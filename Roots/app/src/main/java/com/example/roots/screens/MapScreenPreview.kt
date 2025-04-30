package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.roots.R

@Composable
fun MapScreenPreview() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Imagen de fondo como "mapa"
            Image(
                painter = painterResource(id = R.drawable.fakemap),
                contentDescription = "Fake Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Search bar y filtros arriba
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
                    Text("PUJ, Apartamento", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    /*Row {
                        Icon(
                            painter = painterResource(id = R.drawable.locate_in_map),
                            contentDescription = "Map",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(end = 8.dp)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_table),
                            contentDescription = "Table",
                            modifier = Modifier.size(36.dp)
                        )
                    }*/

                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Filter",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Botón flotante de ubicación (abajo a la derecha)
            Icon(
                painter = painterResource(id = R.drawable.locate_in_map),
                contentDescription = "Location",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 72.dp)
                    .size(48.dp)
            )
        }
    }
}
