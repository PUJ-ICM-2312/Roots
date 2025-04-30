package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.roots.R
import com.example.roots.ui.theme.RootsTheme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.data.InmuebleRepository
import com.example.roots.model.Inmueble
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import com.example.roots.components.BottomNavBar


@Composable
fun MyPropertiesScreen(navController: NavController) {
    val properties = InmuebleRepository.inmuebles

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

            PropertyGrid(properties, navController)

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
  /*  val properties = listOf(
        Pair(R.drawable.inmueble1, "Santa viviana • 60 m"),
        Pair(R.drawable.inmueble2, "Polo Club. • 75 m"),
        Pair(R.drawable.inmueble3, "Hayuelos. • 53 m"),
        Pair(R.drawable.inmueble4, "Chico Norte • 30 m"),
        Pair(R.drawable.inmueble5, "Balmoral Norte • 95 m"),
        Pair(R.drawable.inmueble6, "Castellana • 72 m")
    )*/


    Column {
        // Dí de dos en dos
        for (i in properties.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PropertyCard(
                    inmueble      = properties[i],
                    navController = navController,
                    modifier      = Modifier.weight(1f)
                )
                if (i + 1 < properties.size) {
                    PropertyCard(
                        inmueble      = properties[i + 1],
                        navController = navController,
                        modifier      = Modifier.weight(1f)
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
                // NAVEGA al detalle pasando el ID
                navController.navigate("property_scroll_mode/${inmueble.id}")
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            // Carga la primera foto; si es Int (recurso) o String (URL), AsyncImage lo maneja
            AsyncImage(
                model              = inmueble.fotos.first(),
                contentDescription = null,
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
            Text(
                text = "${inmueble.barrio} • ${inmueble.metrosCuadrados.toInt()} m²",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color(0x80000000))
                    .padding(8.dp)
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

