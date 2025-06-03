package com.example.roots.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.roots.components.BottomNavBar
import com.example.roots.repository.InmuebleRepository
import com.example.roots.model.Inmueble
import com.example.roots.ui.theme.RootsTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyPropertiesScreen(navController: NavController) {
    val properties = remember { mutableStateOf<List<Inmueble>>(emptyList()) }

    LaunchedEffect(Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            InmuebleRepository().getAll { list ->
                properties.value = list.filter { it.usuarioId == userId }
            }
        }
    }


    Scaffold(bottomBar = { BottomNavBar(navController) }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Tus Inmuebles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))

            PropertyGrid(properties = properties.value, navController = navController)

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("add_property") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5))
            ) {
                Text("+ Agregar nuevo inmueble", color = Color.Black)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}


@Composable
fun PropertyGrid(
    properties: List<Inmueble>,
    navController: NavController
) {
    Column {
        for (i in properties.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PropertyCard(
                    inmueble = properties[i],
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
                if (i + 1 < properties.size) {
                    PropertyCard(
                        inmueble = properties[i + 1],
                        navController = navController,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PropertyCard(
    inmueble: Inmueble,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable {
                navController.navigate("property_scroll_mode/${inmueble.id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            // 1) Imagen de fondo
            AsyncImage(
                model = inmueble.fotos.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2) Franja negra semi-transparente en la parte inferior, de ancho completo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)              // ajusta la altura al tamaño que necesites
                    .align(Alignment.BottomStart)
                    .background(Color(0x80000000))
            )

            // 3) El texto, sobre la franja negra
            Text(
                text = "${inmueble.barrio} • ${inmueble.metrosCuadrados.toInt()} m²",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp, bottom = 6.dp) // separaciones interiores
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMyProperties() {
    RootsTheme {
        MyPropertiesScreen(navController = rememberNavController())
    }
}
