package com.example.roots.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.roots.R
import com.example.roots.components.BottomNavBar
import com.example.roots.model.Usuario
import com.example.roots.service.LoginService
import com.example.roots.service.UsuarioService
import com.example.roots.repository.UsuarioRepository
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.ui.theme.RootsTheme
import com.google.firebase.auth.FirebaseAuth

val usuarioRepository = UsuarioRepository()
val usuarioService = UsuarioService(usuarioRepository)

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseUser = LoginService.getCurrentUser()
    val usuarioService = remember { UsuarioService(UsuarioRepository()) }

    // Estado observable para el Usuario (inicia en null)
    var currentUser by remember { mutableStateOf<Usuario?>(null) }

    // 1) Cargar usuario desde Firestore cuando el Composable se monte
    LaunchedEffect(firebaseUser?.uid) {
        firebaseUser?.uid?.let { uid ->
            usuarioService.obtener(uid) { usuario ->
                if (usuario != null) {
                    currentUser = usuario
                } else {
                    Toast.makeText(context, "No se pudo cargar datos de usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Logo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(60.dp)
                        .padding(start = 0.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Avatar circular
            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!currentUser?.fotoPath.isNullOrBlank()) {
                    // Si existe una URL de foto en Firestore, la mostramos con AsyncImage
                    AsyncImage(
                        model = currentUser!!.fotoPath,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // placeholder
                    Box(
                        Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre completo o texto de carga
            Text(
                text = currentUser?.let { "${it.nombres} ${it.apellidos}" } ?: "Cargando...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de navegación
            SettingsButton(Icons.Default.Settings, "Perfil") {
                navController.navigate("edit_profile")
            }
            SettingsButton(Icons.Default.Description, "Términos y condiciones") {
                // TODO: Navegar a términos
            }
            SettingsButton(Icons.Default.GridOn, "Revisa tu plan") {
                navController.navigate("current_plan")
            }
            SettingsButton(Icons.Default.Star, "Obtén Roots Premium") {
                navController.navigate("plans")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Logout
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE0E0)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Cerrar sesión", color = Color.Black)
            }
        }
    }
}

@Composable
private fun SettingsButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = text, tint = Color.Black)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = Color.Black)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettings() {
    // Para preview podemos simular un usuario ficticio. No hace falta cargar Firestore.
    val dummy = Usuario(
        id = "123",
        nombres = "Juan",
        apellidos = "Pérez",
        correo = "juan@example.com",
        fotoPath = "", // o URL a imagen pública
        celular = "3001234567",
        cedula = "12345678"
    )
    // Forzamos el estado en preview:
    var currentUser by remember { mutableStateOf<Usuario?>(dummy) }

    RootsTheme {
        SettingsScreen(navController = rememberNavController())
    }
}
