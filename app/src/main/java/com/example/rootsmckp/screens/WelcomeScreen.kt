package com.example.rootsmckp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavHostController
import com.example.rootsmckp.R

@Composable
fun WelcomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.rootslogo),
            contentDescription = "Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(120.dp)
        )
        Text("Roots", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { navController.navigate("login") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98F5A9)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        }
        Button(
            onClick = { navController.navigate("signup") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98F5A9)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Registrarse", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        }
        Button(
            onClick = { navController.navigate("filters") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF98F5A9)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Explorar", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        }
    }
}
