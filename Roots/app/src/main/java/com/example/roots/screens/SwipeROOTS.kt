package com.example.roots.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.R
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import com.example.roots.components.BottomNavBar

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SwipeROOTS(navController: NavController) {
    val properties = listOf(
        Triple(R.drawable.inmueble1, "Santa Viviana", "60 m² • 3 km"),
        Triple(R.drawable.inmueble2, "Polo Club", "75 m² • 4.5 km"),
        Triple(R.drawable.inmueble3, "Hayuelos", "53 m² • 2.2 km"),
        Triple(R.drawable.inmueble4, "Chico Norte", "30 m² • 1.8 km"),
        Triple(R.drawable.inmueble5, "Balmoral Norte", "95 m² • 6.0 km")
    )

    var currentIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (currentIndex < properties.size) {
                AnimatedContent(
                    targetState = currentIndex,
                    transitionSpec = {
                        slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with
                                slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut()
                    },
                    label = "card-swipe-animation"
                ) { index ->
                    val property = properties[index]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp), // MÁS ALTO 🔥
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = property.first),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(320.dp)
                                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(property.second, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text(property.third, fontSize = 16.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = {
                            if (currentIndex < properties.lastIndex) currentIndex++
                            else currentIndex++
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFFFCDD2), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "No me gusta", tint = Color.Red, modifier = Modifier.size(36.dp))
                    }

                    IconButton(
                        onClick = {
                            if (currentIndex < properties.lastIndex) currentIndex++
                            else currentIndex++
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFC8E6C9), RoundedCornerShape(50))
                    ) {
                        Icon(Icons.Default.Favorite, contentDescription = "Me gusta", tint = Color(0xFF4CAF50), modifier = Modifier.size(36.dp))
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(150.dp))
                Text(
                    text = "No hay más inmuebles por hoy.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
