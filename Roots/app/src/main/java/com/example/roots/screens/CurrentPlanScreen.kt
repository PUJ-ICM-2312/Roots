package com.example.roots.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.roots.components.BottomNavBar
import com.example.roots.model.Plan
import com.example.roots.model.Usuario
import com.example.roots.repository.UsuarioRepository
import com.example.roots.service.LoginService
import com.example.roots.service.UsuarioService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CurrentPlanScreen(navController: NavController) {
    val context = LocalContext.current
    val usuarioService = remember { UsuarioService(UsuarioRepository()) }
    val firebaseUser = LoginService.getCurrentUser()
    var currentUser by remember { mutableStateOf<Usuario?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Simulamos la lista de planes (tal como antes)
    val planes = listOf(
        Plan(id = "plan1", nombre = "Básico", descripcion = "1 mes sin restricciones", precioMensual = 9.99),
        Plan(id = "plan2", nombre = "Intermedio", descripcion = "3 meses con 10% off", precioMensual = 8.50),
        Plan(id = "plan3", nombre = "Premium", descripcion = "12 meses con 20% off", precioMensual = 7.99)
    )

    LaunchedEffect(firebaseUser?.uid) {
        firebaseUser?.uid?.let { uid ->
            usuarioService.obtener(uid) { usuario ->
                currentUser = usuario
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Colores verdes de la app:
    val verdeClaro = Color(0xFFD5FDE5)
    val grisClaro   = Color(0xFFE0E0E0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        // — Título principal —
        Text(
            text = "Plan actual",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (currentUser == null) {
            // — Caso: no hay usuario autenticado —
            Text("No hay usuario autenticado.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Volver")
            }

        } else {
            // — Usuario cargado —
            if (currentUser!!.tienePlan) {
                // — Encontramos el plan que tenga asignado el usuario —
                val planUsuario = planes.find { it.id == currentUser!!.planId }

                // — Primera tarjeta: datos del plan y descripción fija —
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = verdeClaro),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Nombre del plan (si no lo encuentra, muestro “Plan desconocido”)
                        Text(
                            text = planUsuario?.nombre ?: "Plan desconocido",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Descripción fija (idéntica para todos los planes):
                        // • 5 publicaciones – 60 días
                        // • Vigencia: 60 días
                        // • Sin anuncios
                        // • Inmuebles destacados
                        // • Estadísticas avanzadas
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text("• 5 publicaciones – 60 días", fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Vigencia: 60 días", fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Sin anuncios", fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Inmuebles destacados", fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Estadísticas avanzadas", fontSize = 16.sp, color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // — Segunda tarjeta: Membresía y facturación —
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = grisClaro),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Membresía y facturación",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Correo del usuario:
                        Text(
                            text = currentUser!!.correo,
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))


                        Text(
                            text = "VISA  **** **** **** 0231",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0033CC) // un azul para resaltar
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Próxima facturación: calculada a partir de la fecha de expiración
                        val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
                        val fechaExp = Date(currentUser!!.fechaExpiracion)
                        Text(
                            text = "Tu próxima fecha de facturación es el ${sdf.format(fechaExp)}.",
                            fontSize = 16.sp,
                            color = Color.DarkGray
                        )
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // “Información de facturación” (ficticio, sin acción real)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    Toast
                                        .makeText(context, "Info de facturación (pendiente)", Toast.LENGTH_SHORT)
                                        .show()
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Información de facturación",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.ArrowForwardIos,
                                contentDescription = "Más",
                                tint = Color.DarkGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // — Botón “Cancelar membresía” —
                Button(
                    onClick = {
                        navController.navigate("plans")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height( FiftyDp ), // ajusta al alto que desees
                    colors = ButtonDefaults.buttonColors(containerColor = verdeClaro),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Renovar plan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                        text = "Regresra",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))


            } else {
                // — Usuario SIN plan activo —
                Text(
                    text = "Aún no tienes un plan activo.\nPara poder publicar tus inmuebles, selecciona uno de nuestros planes.",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate("plans") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height( FiftyDp ), // ajusta al alto que desees
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9AF5B4))
                ) {
                    Text("Ver Planes", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

// Puedes definir este valor para el alto del botón si lo prefieres:
private val FiftyDp = 50.dp


/*
@Composable
fun CurrentPlanScreen(navController: NavController
) {
    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Plan actual",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card del plan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD5FDE5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Plan Pro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf(
                        "5 publicaciones - 60 días",
                        "Vigencia: 60 días",
                        "Sin anuncios",
                        "Inmuebles destacados",
                        "Estadísticas avanzadas"
                    ).forEach {
                        Text("• $it", fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card de facturación
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE9F0EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Membresía y facturación",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "diegocortes@gmail.com", fontSize = 14.sp)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "VISA   **** **** **** 0231",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A237E)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Tu próxima fecha de facturación es el 25 de noviembre de 2025.",
                        fontSize = 13.sp
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { /* acción */ }
                    ) {
                        Text(
                            text = "Información de facturación",
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* cancelar membresía */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Cancelar membresía", color = Color.Black)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
*/