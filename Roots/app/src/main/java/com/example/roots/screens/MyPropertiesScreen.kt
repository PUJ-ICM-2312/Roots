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

@Composable
fun MyPropertiesScreen(navController: NavController) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tus Inmuebles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PropertyGrid()

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // TODO: Acción para agregar inmueble (por ejemplo, navegar a otra pantalla)
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(text = "+ Agregar nuevo inmueble", color = Color.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PropertyGrid() {
    val properties = listOf(
        Pair(R.drawable.inmueble1, "Santa viviana • 60 m"),
        Pair(R.drawable.inmueble2, "Polo Club. • 75 m"),
        Pair(R.drawable.inmueble3, "Hayuelos. • 53 m"),
        Pair(R.drawable.inmueble4, "Chico Norte • 30 m"),
        Pair(R.drawable.inmueble5, "Balmoral Norte • 95 m"),
        Pair(R.drawable.inmueble6, "Castellana • 72 m")
    )

    Column {
        for (i in properties.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PropertyCard(
                    imageId = properties[i].first,
                    title = properties[i].second,
                    modifier = Modifier.weight(1f)
                )
                if (i + 1 < properties.size) {
                    PropertyCard(
                        imageId = properties[i + 1].first,
                        title = properties[i + 1].second,
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
fun PropertyCard(imageId: Int, title: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(160.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = title,
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
