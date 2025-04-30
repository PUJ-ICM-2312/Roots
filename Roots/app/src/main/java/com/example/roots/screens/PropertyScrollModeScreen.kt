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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.example.roots.data.InmuebleRepository
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.roots.components.BottomNavBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PropertyScrollModeScreen(
    navController: NavController,
    propertyId: Int
) {
    // 2) Recupera el inmueble del repositorio
    val inmueble = remember(propertyId) {
        InmuebleRepository.inmuebles.firstOrNull { it.id == propertyId }
    } ?: run {
        // Si no existe, vuelve atrás o muestra un placeholder
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

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
            PropertyDescription(inmueble)
            PropertyLocation(inmueble)
            PropertyMap(inmueble)
            ContactButton(inmueble)
            Spacer(modifier = Modifier.height(80.dp)) // Para dar espacio al navbar
        }
    }
}

@Composable
fun ImageCarousel(inmueble: Inmueble) {
    /*val images = listOf(
        R.drawable.inmueble1,
        R.drawable.inmueble2,
        R.drawable.inmueble3,
        R.drawable.inmueble4,
        R.drawable.inmueble5,
        R.drawable.inmueble6
    )*/

    val pagerState = rememberPagerState(pageCount = { inmueble.fotos.size })

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fill,
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(8.dp)
    ) { page ->
        when (val item = inmueble.fotos[page]) {
            is Int    -> Image(
                painter            = painterResource(id = item),
                contentDescription = null,
                modifier           = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale       = ContentScale.Crop
            )
            is String -> AsyncImage(
                model              = item,
                contentDescription = null,
                modifier           = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale       = ContentScale.Crop
            )
            else      -> Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(220.dp))  // fallback
        }
    }
}


@Composable
fun PropertyHeaderInfo(inmueble: Inmueble) {
    val formattedPrice = remember(inmueble.precio) {
        NumberFormat.getNumberInstance(Locale.US)
            .format(inmueble.precio.toLong())
    }
    val formattedPriceAdmin = remember(inmueble.mensualidadAdministracion) {
        NumberFormat.getNumberInstance(Locale.US)
            .format(inmueble.mensualidadAdministracion.toLong())
    }

    Column(modifier = Modifier.padding(16.dp)) {
       /* Text("Apartamento en Venta", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("$990.000.000", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF1A1A1A))
        Text("+ $757.000 administración", color = Color.Gray)*/

        Text(inmueble.direccion, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(inmueble.ciudad + ", " + inmueble.barrio, fontWeight = FontWeight.Bold, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(10.dp))

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val fechaTexto = sdf.format(Date(inmueble.fechaPublicacion))
        Text("Publicado el $fechaTexto")

        Spacer(modifier = Modifier.height(10.dp))

        val anti = inmueble.antiguedad
        Text("Antigüedad: ")
        if (anti <= 1)
            Text("$anti año")
        else
            Text("$anti años")
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "$ $formattedPrice",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF1A1A1A))
        Text("$ $formattedPriceAdmin administración")

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
            PropertyFeature(Icons.Default.DirectionsCar, "${inmueble.numParqueaderos} Parqueaderos")
        }
    }
}

@Composable
fun PropertyDescription(inmueble: Inmueble) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Descripción", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(" ${inmueble.descripcion} ".trimIndent())

    }
}

@Composable
fun PropertyLocation(inmueble: Inmueble) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ubicación principal", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(Modifier.height(4.dp))
        // Mostramos barrio y ciudad desde el inmueble
        Text("${inmueble.barrio}, ${inmueble.ciudad}", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun PropertyMap(inmueble: Inmueble) {
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
fun ContactButton(inmueble: Inmueble) {
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
        // Si quieres previsualizar:
        val first = InmuebleRepository.inmuebles.first()
        PropertyScrollModeScreen(
            navController = rememberNavController(),
            propertyId    = first.id
        )
    }
}

