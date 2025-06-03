package com.example.roots.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.roots.R
import com.example.roots.components.BottomNavBar
import com.example.roots.model.Usuario
import com.example.roots.service.LoginService
import com.example.roots.service.UsuarioService
import com.example.roots.repository.UsuarioRepository
import com.example.roots.util.uploadProfileImageToFirebase
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.ui.theme.RootsTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Pantalla para editar el perfil: se carga la info de Firebase, muestra campos (nombres, apellidos, correo, celular, cédula)
 * y permite cambiar la foto (cámara o galería). Al actualizar, sube la foto a Firebase Storage (si cambió) y luego actualiza Firestore.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val usuarioService = remember { UsuarioService(UsuarioRepository()) }
    val firebaseUser = LoginService.getCurrentUser()
    val coroutineScope = rememberCoroutineScope()

    // 1) Estado para el Usuario traído desde Firestore
    var currentUser by remember { mutableStateOf<Usuario?>(null) }

    // 2) Estados locales para cada campo de texto (los llenaremos cuando llegue currentUser)
    var nombres   by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var correo    by remember { mutableStateOf("") }
    var celular   by remember { mutableStateOf("") }
    var cedula    by remember { mutableStateOf("") }

    // 3) Estados para la foto:
    //    - profileImageUri: URI local (cámara o galería) si el usuario elige una nueva foto
    //    - profileImageUrl: URL en Firebase Storage (si existe en Firestore)
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImageUrl by remember { mutableStateOf("") }

    // 4) Bandera para saber si el usuario cambió la foto (entonces debemos subirla)
    var photoChanged by remember { mutableStateOf(false) }

    // ─── CARGAR DATOS DEL USUARIO ───
    LaunchedEffect(firebaseUser?.uid) {
        firebaseUser?.uid?.let { uid ->
            usuarioService.obtener(uid) { usuario ->
                if (usuario != null) {
                    currentUser = usuario
                    // Llenamos los estados de texto con los valores que vienen de Firestore
                    nombres   = usuario.nombres
                    apellidos = usuario.apellidos
                    correo    = usuario.correo
                    celular   = usuario.celular
                    cedula    = usuario.cedula
                    profileImageUrl = usuario.fotoPath  // URL en Storage (puede estar vacía)
                } else {
                    Toast.makeText(context, "No se pudo cargar usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ─── LANZADORES PARA FOTO (CÁMARA Y GALERÍA) ───

    // 5) Launcher para tomar foto: devuelve Bitmap
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        bmp?.let {
            // Convertimos el Bitmap en un archivo temporal en cache y obtenemos su URI
            val filename = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, filename)
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            val uri = Uri.fromFile(file)
            profileImageUri = uri
            photoChanged = true
        }
    }

    // 6) Launcher para elegir foto de la galería: devuelve URI
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            photoChanged = true
        }
    }

    // 7) Lanzador para pedir permiso de cámara
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(bottomBar = { BottomNavBar(navController) }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo + Título
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.height(60.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // Avatar circular:
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                when {
                    // ① Si hay una URI local nueva
                    profileImageUri != null -> {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Nueva foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // ② Si no hay URI local, pero sí una URL en Storage
                    profileImageUrl.isNotBlank() -> {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // ③ Si no hay ninguna, mostramos un icono genérico
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Sin foto",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Botones para cambiar la foto
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        // Verificar permiso de cámara antes de lanzar cámara
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            cameraLauncher.launch(null)
                        } else {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
                    Spacer(Modifier.width(4.dp))
                    Text("Cámara", color = Color.Black)
                }

                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5))
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
                    Spacer(Modifier.width(4.dp))
                    Text("Galería", color = Color.Black)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ─── Campos de texto prellenados ───
            OutlinedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = { Text("Nombres") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            OutlinedTextField(
                value = celular,
                onValueChange = { celular = it },
                label = { Text("Celular") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
            OutlinedTextField(
                value = cedula,
                onValueChange = { cedula = it },
                label = { Text("Cédula") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )

            Spacer(Modifier.height(24.dp))

            // ─── Botón “Actualizar perfil” ───
            Button(
                onClick = {
                    coroutineScope.launch {
                        // 1) Si cambió la foto, subimos profileImageUri a Firebase Storage
                        var finalPhotoUrl = profileImageUrl
                        if (photoChanged && profileImageUri != null) {
                            try {
                                finalPhotoUrl = uploadProfileImageToFirebase(profileImageUri!!)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(
                                    context,
                                    "Error al subir foto: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@launch
                            }
                        }

                        // 2) Verificar que currentUser tenga un ID válido
                        val usuarioExistente = currentUser
                        if (usuarioExistente == null || usuarioExistente.id.isBlank()) {
                            Toast.makeText(context, "Usuario no válido", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        // 3) Creación del objeto Usuario con los nuevos valores
                        val updatedUser = Usuario(
                            id = usuarioExistente.id,
                            nombres = nombres,
                            apellidos = apellidos,
                            correo = correo,
                            fotoPath = finalPhotoUrl, // ahora es URL Cloud Storage
                            celular = celular,
                            cedula = cedula
                        )

                        // 4) Llamar a UsuarioService.actualizar()
                        usuarioService.actualizar(updatedUser) { success ->
                            if (success) {
                                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5))
            ) {
                Text("Actualizar perfil", fontSize = 16.sp, color = Color.Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditProfileScreen() {
    RootsTheme {
        EditProfileScreen(navController = rememberNavController())
    }
}
