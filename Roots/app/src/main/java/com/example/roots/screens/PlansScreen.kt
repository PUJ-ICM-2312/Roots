package com.example.roots.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.ui.theme.RootsTheme

@Composable
fun PlansScreen() {
    Scaffold(
        bottomBar = { BottomNavBar() }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(35.dp)) // espacio seguro para el notch
            Text(
                text = "Planes",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Publica tu inmueble y elige el plan ideal para ti.\nTodos los planes eliminan anuncios.",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SubscriptionCard(
                title = "Plan Básico - GRATIS",
                features = listOf(
                    "Ver inmuebles",
                    "Con anuncios",
                    "No puedes publicar",
                    "Seleccionado automáticamente"
                )
            )

            SubscriptionCard(
                title = "Plan Publicador - \$9.900",
                features = listOf(
                    "1 publicación - 30 días",
                    "Sin anuncios",
                    "Estadísticas básicas"
                )
            )

            SubscriptionCard(
                title = "Plan Pro - \$19.900",
                features = listOf(
                    "5 publicaciones - 60 días",
                    "Sin anuncios",
                    "Inmuebles destacados",
                    "Estadísticas avanzadas"
                )
            )

            SubscriptionCard(
                title = "Plan Premium - \$29.900",
                features = listOf(
                    "Publicaciones ilimitadas - 90 días",
                    "Sin anuncios",
                    "Inmuebles en primer lugar",
                    "Estadísticas avanzadas",
                    "Soporte prioritario"
                )
            )
        }
    }
}

@Composable
fun SubscriptionCard(title: String, features: List<String>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFFD5FDE5)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            for (feature in features) {
                Text("• $feature", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun BottomNavBar() {
    Column {
        Divider(color = Color.LightGray, thickness = 1.dp)

        BottomAppBar(
            containerColor = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.VpnKey, contentDescription = "Tus inmuebles")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar inmuebles")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Message, contentDescription = "Mensajes")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Settings, contentDescription = "Configuración")
                }
            }
        }
    }
}

