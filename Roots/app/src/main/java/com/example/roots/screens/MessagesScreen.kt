package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
fun MessagesScreen() {
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
            Text(
                text = "Mis Inmuebles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPropertyList()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mensajes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensajes simulados para cada inmueble
            // En la sección de mensajes:
            MessageItem(R.drawable.inmueble1, "Andres Perez", "Me encantó el balcón y la vista al parque.")
            MessageItem(R.drawable.inmueble3, "Juan Diego Reyes", "¿El precio es negociable?")
            MessageItem(R.drawable.inmueble2, "Daniel Gonzalez", "¿Tiene parqueadero cubierto?", bold = true)
            MessageItem(R.drawable.inmueble5, "Valentina Valencia", "¡Justo lo que buscaba para mudarme con mi gato!")
            MessageItem(R.drawable.inmueble4, "Camila López", "¿Incluye lavadora y estufa?", bold = true)

        }
    }
}

@Composable
fun HorizontalPropertyList() {
    val properties = listOf(
        Pair(R.drawable.inmueble1, "Santa Viviana"),
        Pair(R.drawable.inmueble2, "Polo Club"),
        Pair(R.drawable.inmueble3, "Hayuelos"),
        Pair(R.drawable.inmueble4, "Chico Norte"),
        Pair(R.drawable.inmueble5, "Balmoral Norte"),
        Pair(R.drawable.inmueble6, "Castellana")
    )

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        properties.forEach { (imageId, label) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Image(
                        painter = painterResource(id = imageId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun MessageItem(imageId: Int, title: String, message: String, bold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = message,
                fontSize = 13.sp,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
            )
        }
    }

    Divider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.2f))
}
