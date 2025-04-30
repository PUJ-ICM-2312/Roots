package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.R
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.roots.components.BottomNavBar
import com.example.roots.data.UsuarioRepository
import com.example.roots.model.Usuario
import coil.compose.AsyncImage
import com.example.roots.ui.theme.RootsTheme
import java.io.File


@Composable
fun SettingsScreen(navController: NavController) {
    val user = UsuarioRepository.usuario

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

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

            Box(
                modifier = Modifier.size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                if (user.fotoPath.isNotEmpty()) {
                    AsyncImage(
                        model = File(user.fotoPath),
                        contentDescription = null,
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
            Text(
                text = "${user.nombres} ${user.apellidos}",
                fontSize = 16.sp
            )


            Spacer(modifier = Modifier.height(24.dp))

            SettingsButton(Icons.Default.Settings, "Perfil") {
                navController.navigate("edit_profile")
            }
            SettingsButton(Icons.Default.Description, "Términos y condiciones") {
                // TODO: Navegar a terminos
            }
            SettingsButton(Icons.Default.GridOn, "Revisa tu plan") {
                navController.navigate("current_plan")
            }
            SettingsButton(Icons.Default.Star, "Obtén Roots Premium") {
                navController.navigate("plans")
            }
        }
    }
}

@Composable
fun SettingsButton(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
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
    RootsTheme {
       SettingsScreen(navController = rememberNavController())
    }
}
