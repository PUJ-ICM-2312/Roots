package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.roots.R
import coil.compose.rememberAsyncImagePainter

@Composable
fun PaymentSuccessScreen(onPublishClick: () -> Unit = {}) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp), // más espacio arriba
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo más arriba
            Image(
                painter = painterResource(id = R.drawable.logo), // Cambia por tu logo real
                contentDescription = "Logo Roots",
                modifier = Modifier
                    .size(170.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // GIF de check más grande
            Image(
                painter = rememberAsyncImagePainter(R.drawable.check_circle), // El gif
                contentDescription = "Pago exitoso",
                modifier = Modifier
                    .size(180.dp) // antes era 120.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Plan comprado exitosamente",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Gracias por subscribirte a Roots.\nA tu correo te llegará la factura",
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onPublishClick() },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB6F2C1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Publica un inmueble",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

