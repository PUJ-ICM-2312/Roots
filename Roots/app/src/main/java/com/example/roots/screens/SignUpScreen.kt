package com.example.roots.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.R
import com.example.roots.model.Usuario
import com.example.roots.repository.UsuarioRepository
import com.example.roots.service.LoginService
import com.example.roots.service.SecureStorage
import com.example.roots.service.UsuarioService
import com.example.roots.service.showBiometricPrompt
//import com.example.roots.ui.theme.RootsTheme
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    var name by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val usuarioRepository = UsuarioRepository()
    val usuarioService = UsuarioService(usuarioRepository)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(top = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                // El usuario se creó en FirebaseAuth
                                val uid = task.result?.user?.uid ?: ""
                                if (uid.isBlank()) {
                                    Toast.makeText(context, "Error al obtener UID", Toast.LENGTH_LONG).show()
                                    return@addOnCompleteListener
                                }

                                // 1) Creamos el objeto Usuario con todos los campos
                                val nuevoUsuario = Usuario(
                                    id = uid,
                                    nombres = name,
                                    apellidos = lastName,
                                    correo = email,
                                    fotoPath = "",    // inicialmente vacío
                                    celular = "",     // inicialmente vacío
                                    cedula = ""       // inicialmente vacío
                                )

                                // 2) Guardamos en Firestore (colección "usuarios")
                                usuarioService.crear(nuevoUsuario) { successCrear ->
                                    if (successCrear) {
                                        // 3) Mostramos autenticación biométrica
                                        showBiometricPrompt(
                                            context = context,
                                            onAuthSuccess = {
                                                SecureStorage.saveCredentials(context, email, password)
                                                Toast.makeText(context, "Huella registrada y login guardado", Toast.LENGTH_SHORT).show()
                                                navController.navigate("login") {
                                                    popUpTo("signup") { inclusive = true }
                                                }
                                            },
                                            onAuthError = {
                                                Toast.makeText(context, "Registro exitoso pero falló la huella", Toast.LENGTH_SHORT).show()
                                                navController.navigate("login") {
                                                    popUpTo("signup") { inclusive = true }
                                                }
                                            }
                                        )
                                    } else {
                                        Toast.makeText(context, "Error al guardar usuario en Firestore", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } else {
                                val errorMsg = task.exception?.message ?: "Error desconocido"
                                Toast.makeText(context, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                            }
                        }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9AF5B4)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLoading) "Registrando..." else "Confirmar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text("O regístrate con")

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconButton(onClick = { /* Login con Google */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { /* Login con Facebook */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignUp() {
    SignUpScreen(navController = rememberNavController())
}