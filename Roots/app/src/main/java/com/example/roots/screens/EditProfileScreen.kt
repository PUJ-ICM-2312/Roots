package com.example.roots.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.roots.R
import com.example.roots.components.BottomNavBar
import com.example.roots.data.UsuarioRepository
import com.example.roots.model.Usuario
import com.example.roots.ui.theme.RootsTheme
import saveImageToInternalStorage
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



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    val repoUser = UsuarioRepository.usuario

    // estados inicializados con los datos actuales
    var profileImagePath by remember { mutableStateOf(repoUser.fotoPath) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var nombres   by remember { mutableStateOf(repoUser.nombres) }
    var apellidos by remember { mutableStateOf(repoUser.apellidos) }
    var correo    by remember { mutableStateOf(repoUser.correo) }
    var celular   by remember { mutableStateOf(repoUser.celular) }
    var cedula    by remember { mutableStateOf(repoUser.cedula) }

    // 1) Selector de GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // intentamos guardar en interno; aquí obtenemos String?
            val maybePath = saveImageToInternalStorage(context, it)
            // sólo si no es null, asignamos
            maybePath?.let { path ->
                profileImagePath = path
                // luego tu lógica de ImageDecoder…
                if (Build.VERSION.SDK_INT >= 28) {
                    val src = ImageDecoder.createSource(context.contentResolver, it)
                    profileBitmap = ImageDecoder.decodeBitmap(src)
                }
            }
        }
    }


    // 2) Selector de CÁMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        bmp?.let {
            // lo guardamos y actualizamos both path y bitmap
            val path = saveBitmapToInternalStorage(context, it)
            profileImagePath = path
            profileBitmap = it
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
                profileBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Botones
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { cameraLauncher.launch(null) },
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
                        id        = repoUser.id,
                        nombres   = nombres,
                        apellidos = apellidos,
                        correo    = correo,
                        fotoPath  = profileImagePath,
                        celular   = celular,
                        cedula    = cedula
                    )
                    UsuarioRepository.updateUsuario(actualizado)
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
