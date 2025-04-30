package com.example.roots.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.R
import com.example.roots.ui.theme.RootsTheme
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.roots.model.Inmueble
import com.example.roots.data.MockInmuebles

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PropertyScrollModeScreen(navController: NavController, inmueble: Inmueble) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ImageCarousel(inmueble)
            PropertyHeaderInfo(inmueble)
            PropertyDescription()
            PropertyLocation()
            PropertyMap()
            ContactButton()
            Spacer(modifier = Modifier.height(80.dp)) // Para dar espacio al navbar
        }
    }
}

@Composable
fun ImageCarousel(inmueble: Inmueble) {
    val images = listOf(
        R.drawable.inmueble1,
        R.drawable.inmueble2,
        R.drawable.inmueble3,
        R.drawable.inmueble4,
        R.drawable.inmueble5,
        R.drawable.inmueble6
    )

    val pagerState = rememberPagerState(pageCount = { inmueble.fotos.size })

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
    ) { page ->
       /* Image(
            painter = painterResource(id = images[page]),
            contentDescription = "Property Image",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )*/

        AsyncImage(
            model = inmueble.fotos[page],
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun PropertyHeaderInfo(inmueble: Inmueble) {
    Column(modifier = Modifier.padding(16.dp)) {
       /* Text("Apartamento en Venta", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("$990.000.000", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A1A))
        Text("+ $757.000 administración", color = Color.Gray)*/

        Text(inmueble.direccion, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("₡${inmueble.precio}")
        Text("+ ₡${inmueble.mensualidadAdministracion} administración")

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            /*PropertyFeature(Icons.Default.Bed, "3 Habs")
            PropertyFeature(Icons.Default.Shower, "3 Baños")
            PropertyFeature(Icons.Default.SquareFoot, "110 m²")
            PropertyFeature(Icons.Default.DirectionsCar, "2 Parqueaderos")*/

            PropertyFeature(Icons.Default.Bed, "${inmueble.numHabitaciones} Habs")
            PropertyFeature(Icons.Default.Shower, "${inmueble.numBaños} Baños")
            PropertyFeature(Icons.Default.SquareFoot, "${inmueble.metrosCuadrados} m²")
        }
    }
}

@Composable
fun PropertyDescription() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("""
            Vendo El Chico ALEGÓ, Área de 110 m2, muy bien aprovechados,
            8 piso exterior, muy iluminado con una vista envidiable, 15 años de construido,
            3 habitaciones, 3 baños, Estudio, Pisos laminados madera,
            Sala y comedor, Ventanales piso techo, Balcón, Chimenea a gas,
            Cocina abierta con barra, Zona de lavandería ventilada, 2 parqueaderos.
        """.trimIndent())
    }
}

@Composable
fun PropertyLocation() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ubicación principal", fontWeight = FontWeight.Bold)
        Text("El chico, Bogotá, Bogotá, D.C.")
        Spacer(modifier = Modifier.height(4.dp))
        Text("Ubicaciones asociadas", fontWeight = FontWeight.Bold)
        Text("El Chico, Barrios Unidos, Chico Norte, Chapinero...")
    }
}

@Composable
fun PropertyMap() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(250.dp)
        .padding(16.dp)) {

        Image(
            painter = painterResource(id = R.drawable.fakemap),
            contentDescription = "Mapa",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = { /* TODO: Mostrar ruta */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(48.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
        ) {
            Icon(Icons.Default.Route, contentDescription = "Ver ruta")
        }
    }
}

@Composable
fun ContactButton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { /* TODO: Contactar al dueño */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD5FDE5),
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()


        ) {
            Icon(Icons.Default.ChatBubble, contentDescription = "Contactar")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Contactar", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PropertyFeature(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null)
        Text(text = label)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPropertyScrollMode() {
    RootsTheme {
        // Coge un mock cualquiera:
        PropertyScrollModeScreen(
            navController = rememberNavController(),
            inmueble      = MockInmuebles.sample.first()
        )
    }
}

