// app/src/main/java/com/example/roots/screens/MyPropertiesScreen.kt
package com.example.roots.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.roots.components.BottomNavBar
import com.example.roots.model.Inmueble
import com.example.roots.model.Usuario
import com.example.roots.repository.InmuebleRepository
import com.example.roots.service.UsuarioService
import com.example.roots.repository.UsuarioRepository
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MyPropertiesScreen(navController: NavController) {
    val context = LocalContext.current

    // 1) Estado para el usuario
    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var cargandoUsuario by remember { mutableStateOf(true) }

    // 2) Estado para la lista de inmuebles propios
    var properties by remember { mutableStateOf<List<Inmueble>>(emptyList()) }

    // 3) Instanciamos el servicio de Usuario (que leerá desde Firestore)
    val usuarioService = remember { UsuarioService(UsuarioRepository()) }

    // 4) Al iniciar, obtenemos el objeto Usuario actual y luego, solo si tieneSuscripcion==true, cargamos propiedades
    LaunchedEffect(Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            usuarioService.obtener(currentUserId) { u ->
                if (u != null) {
                    usuario = u
                    cargandoUsuario = false

                    // Si tiene suscripción, obtenemos sus inmuebles
                    if (u.tienePlan) {
                        InmuebleRepository().getAll { lista ->
                            properties = lista.filter { it.usuarioId == currentUserId }
                        }
                    }
                } else {
                    cargandoUsuario = false
                    Toast.makeText(context, "No se pudo cargar datos de usuario", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            cargandoUsuario = false
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Mis Inmuebles",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))

            // ─── Mientras cargo datos del usuario, muestro un Spinner ───
            if (cargandoUsuario) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                Spacer(Modifier.height(24.dp))
                return@Column
            }

            // ─── Si el usuario NO tiene suscripción activa ───
            if (usuario?.tienePlan == false) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Aún no tienes un plan activo.\n" +
                            "Para poder publicar tus inmuebles, selecciona uno de nuestros planes.",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("plans") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9AF5B4))
                ) {
                    Text("Ver Planes", color = Color.Black, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(32.dp))
                return@Column
            }

            // ─── Si el usuario TIENE suscripción activa ───
            // Mostramos su grid de propiedades (tal como antes)
            PropertyGrid(properties = properties, navController = navController)

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

        // Si no tiene ninguna propiedad, se muestra un texto indicándolo
        if (properties.isEmpty()) {
            Text(
                text = "No has agregado ningún inmueble aún.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
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
                    .height(32.dp)
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
                    .padding(start = 8.dp, bottom = 6.dp)
            )
        }
    }
}

