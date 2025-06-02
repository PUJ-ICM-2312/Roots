package com.example.roots.screens

import com.example.roots.model.Usuario
import android.Manifest
import android.content.Context
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.roots.R
import com.example.roots.components.BottomNavBar
import com.example.roots.repository.UsuarioRepository
import com.example.roots.service.LoginService
import com.example.roots.service.UsuarioService
import com.example.roots.ui.theme.RootsTheme
import saveImageToInternalStorage
import java.io.File
import java.io.FileOutputStream

/**
 * Helper para guardar un Bitmap en internal storage y devolver su ruta absoluta.
 */



private fun saveBitmapToInternalStorage(context: android.content.Context, bmp: Bitmap): String {
    val filename = "user_${System.currentTimeMillis()}.png"
    val file = context.filesDir.resolve(filename)
    FileOutputStream(file).use { out ->
        bmp.compress(Bitmap.CompressFormat.PNG, 90, out)
    }
    return file.absolutePath
}

private fun saveImageToInternalStorage(context: Context, uri: Uri): String {
    // generamos un nombre único
    val filename = "profile_${System.currentTimeMillis()}.jpg"
    // resolvemos el fichero destino
    val file = File(context.filesDir, filename)
    // abrimos el input de la URI y el output hacia el fichero
    context.contentResolver.openInputStream(uri)?.use { input ->
        FileOutputStream(file).use { output ->
            input.copyTo(output)
        }
    }
    return file.absolutePath
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val usuarioRepository = UsuarioRepository()
    val usuarioService = UsuarioService(usuarioRepository)

    /*// estados inicializados con los datos actuales
    var profileImagePath by remember { mutableStateOf(repoUser.fotoPath) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 1) permiso
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
*/

    var currentUser: Usuario? = null

    val firebaseUser = LoginService.getCurrentUser()
    if (firebaseUser != null) {
        usuarioService.obtener(firebaseUser.uid) { usuario ->
            if (usuario != null) {
                currentUser = usuario
                println("Usuario actual: ${currentUser?.nombres}")
                // Aquí ya puedes usar currentUser
            } else {
                println("Usuario no encontrado")
            }
        }
    } else {
        println("No hay usuario autenticado")
    }

    var nombres   by remember { mutableStateOf(currentUser?.nombres ?: "") }
    var apellidos by remember { mutableStateOf(currentUser?.apellidos ?: "") }
    var correo    by remember { mutableStateOf(currentUser?.correo ?: "") }
    var celular   by remember { mutableStateOf(currentUser?.celular ?: "") }
    var cedula    by remember { mutableStateOf(currentUser?.cedula ?: "") }


    // estado para la ruta de la foto en disco
    var profileImagePath by remember { mutableStateOf(currentUser?.fotoPath ?: "") }


    // 1) launcher para tomar foto (recibe un Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        bmp?.let {
            // guardo el bitmap en disco y actualizo el path
            val path = saveBitmapToInternalStorage(context, it)
            profileImagePath = path
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // 2) launcher para escoger de galería
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // guardo el stream en un fichero local
            val path = saveImageToInternalStorage(context, it)
            if (path != null) {
                profileImagePath = path
            }
        }
    }

    // 3) launcher para pedir permiso de cámara
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Necesito permiso de cámara para tomar foto", Toast.LENGTH_SHORT).show()
        }
    }


    Scaffold(bottomBar = { BottomNavBar(navController) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo y título
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.height(60.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // Avatar circular
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (profileImagePath.isNotBlank()) {
                    AsyncImage(
                        model = File(profileImagePath),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Botones
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        // 1) ¿Ya tengo permiso?
                        if (ContextCompat.checkSelfPermission(
                                context, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // 2) Sí: abro la cámara
                            cameraLauncher.launch(null)
                        } else {
                            // 3) No: pido permiso
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
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

            // Campos de texto
            ProfileTextField("Nombres", nombres) { nombres = it }
            ProfileTextField("Apellidos", apellidos) { apellidos = it }
            ProfileTextField("Correo", correo) { correo = it }
            ProfileTextField("Celular", celular) { celular = it }
            ProfileTextField("Cédula", cedula) { cedula = it }

            Spacer(Modifier.height(24.dp))

            // Botón de guardar
            Button(
                onClick = {
                    val actualizado = Usuario(
                        id        = currentUser?.id ?: ""
                    ,
                        nombres   = nombres,
                        apellidos = apellidos,
                        correo    = correo,
                        fotoPath  = profileImagePath,
                        celular   = celular,
                        cedula    = cedula
                    )
                    usuarioService.actualizar(actualizado)
                    navController.popBackStack()
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

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewEditProfileScreen() {
    RootsTheme {
        EditProfileScreen(navController = rememberNavController())
    }
}
