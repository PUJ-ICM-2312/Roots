// app/src/main/java/com/example/roots/screens/PlansScreen.kt
package com.example.roots.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roots.model.Plan

@Composable
fun PlansScreen(navController: NavController) {
    val planes = listOf(
        Plan(id = "plan1", nombre = "Publicador - \$9.900",      descripcion = "", precioMensual = 9_900.0),
        Plan(id = "plan2", nombre = "Pro - \$19.900",            descripcion = "", precioMensual = 19_900.0),
        Plan(id = "plan3", nombre = "Premium - \$29.900",        descripcion = "", precioMensual = 29_900.0)
    )

    val verdeClaro = Color(0xFFD5FDE5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()) // <<< Aquí habilitamos scroll a TODO el Column
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Planes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Publica tu inmueble y elige el plan ideal para ti.\nTodos los planes eliminan anuncios.",
            fontSize = 16.sp,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Recorremos manualmente la lista dentro de un Column
        planes.forEach { plan ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("confirm_subscription/${plan.id}")
                    }
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = verdeClaro),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = plan.nombre,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    when (plan.id) {
                        "plan1" -> {
                            PlanBullet(text = "1 publicación · 30 días")
                            PlanBullet(text = "Sin anuncios")
                            PlanBullet(text = "Estadísticas básicas")
                        }
                        "plan2" -> {
                            PlanBullet(text = "5 publicaciones · 60 días")
                            PlanBullet(text = "Sin anuncios")
                            PlanBullet(text = "Inmuebles destacados")
                            PlanBullet(text = "Estadísticas avanzadas")
                        }
                        "plan3" -> {
                            PlanBullet(text = "Publicaciones ilimitadas · 90 días")
                            PlanBullet(text = "Sin anuncios")
                            PlanBullet(text = "Inmuebles en primer lugar")
                            PlanBullet(text = "Estadísticas avanzadas")
                            PlanBullet(text = "Soporte prioritario")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBDBDBD), // Gris claro de fondo
                contentColor = Color(0xFF000000)    // Gris oscuro para el texto
            )
        ) {
            Text(
                text = "Cancelar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PlanBullet(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.padding(end = 6.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
