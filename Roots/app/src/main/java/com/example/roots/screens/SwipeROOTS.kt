package com.example.roots.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage // Para cargar imágenes desde URL
import com.example.roots.R // Para el placeholder
import com.example.roots.components.BottomNavBar // Asumo que tienes este componente
import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository // Necesario para la factory
import com.example.roots.viewmodel.SharedFilterViewModel
import com.example.roots.viewmodel.SwipeViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SwipeROOTS(
    navController: NavController,
    sharedFilterViewModel: SharedFilterViewModel = viewModel(),
    swipeViewModel: SwipeViewModel = viewModel(
        factory = SwipeViewModel.provideFactory(
            InmuebleRepository(), // Asegúrate que esto sea la forma correcta de obtener tu repo
            sharedFilterViewModel
        )
    )
) {
    val properties by swipeViewModel.properties.collectAsState()
    val isLoading by swipeViewModel.isLoading.collectAsState()
    val currentIndex by swipeViewModel.currentIndex.collectAsState()

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply{
        maximumFractionDigits = 0
    }}


    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (properties.isEmpty()) {
                Text(
                    text = "No hay inmuebles que coincidan con tu búsqueda.\nPrueba cambiando los filtros.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            } else if (currentIndex < properties.size) {
                val property = properties[currentIndex]

                AnimatedContent(
                    targetState = currentIndex, // Animar basado en el índice
                    transitionSpec = {
                        // Define tu animación de swipe
                        if (targetState > initialState) { // Swipe hacia la izquierda (siguiente)
                            slideInHorizontally { width -> width } + fadeIn() with
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else { // Swipe hacia la derecha (anterior - si lo implementas)
                            slideInHorizontally { width -> -width } + fadeIn() with
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }
                    },
                    label = "propertyCardAnimation"
                ) { _ ->
                    PropertyCard(property = property, currencyFormatter = currencyFormatter)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { swipeViewModel.userDislikedProperty(property.id) },
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFFCE4EC), RoundedCornerShape(50)) // Tono rosa más claro
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "No me gusta", tint = Color(0xFFE91E63), modifier = Modifier.size(36.dp)) // Rosa fuerte
                    }

                    IconButton(
                        onClick = { swipeViewModel.userLikedProperty(property.id) },
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(50)) // Tono verde más claro
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Me gusta", tint = Color(0xFF4CAF50), modifier = Modifier.size(36.dp)) // Verde
                    }
                }
            } else { // currentIndex >= properties.size y properties no está vacío
                Text(
                    text = "¡Eso es todo por ahora!\nNo hay más inmuebles con los filtros actuales.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PropertyCard(property: Inmueble, currencyFormatter: NumberFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 500.dp, max = 600.dp), // Altura flexible pero con límites
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            AsyncImage(
                model = property.fotos.firstOrNull() ?: R.drawable.inmueble1, // Usa un placeholder
                contentDescription = "Foto de ${property.barrio}",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f) // Proporción para la imagen
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.inmueble1), // Placeholder mientras carga
                error = painterResource(id = R.drawable.inmueble1) // Placeholder en caso de error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = property.barrio.ifBlank { property.direccion.ifBlank { "Ubicación no disponible" } },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "${property.metrosCuadrados.toInt()} m² • ${property.ciudad.ifBlank { "Ciudad no esp."}}",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currencyFormatter.format(property.precio),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                // Puedes añadir más detalles aquí si lo deseas
                // Text("Habitaciones: ${property.numHabitaciones}", fontSize = 14.sp)
                // Text("Baños: ${property.numBanos}", fontSize = 14.sp)
            }
        }
    }
}