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
import com.example.roots.repository.UsuarioRepository
import com.example.roots.service.UsuarioService
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
            InmuebleRepository(),                                // 1) Repo de inmuebles
            UsuarioService(UsuarioRepository())                  // 2) Servicio de usuario
        )
    )
) {
    val properties by swipeViewModel.properties.collectAsState()
    val isLoading by swipeViewModel.isLoading.collectAsState()
    val currentIndex by swipeViewModel.currentIndex.collectAsState()

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
                    targetState = currentIndex,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { width -> width } + fadeIn() with
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() with
                                    slideOutHorizontally { width -> width } + fadeOut()
                        }
                    },
                    label = "propertyCardAnimation"
                ) {
                    PropertyCard(
                        inmueble = property,
                        navController = navController,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                    )
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
                            .background(Color(0xFFFCE4EC), RoundedCornerShape(50))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "No me gusta",
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = { swipeViewModel.userLikedProperty(property.id) },
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(50))
                    ) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Me gusta",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            } else {
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
