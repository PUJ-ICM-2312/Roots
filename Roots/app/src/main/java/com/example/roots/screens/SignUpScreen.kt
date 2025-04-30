package com.example.roots.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.roots.R


@Composable
fun SignUpScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LOGO
        Image(
            painter = painterResource(id = R.drawable.logo), // Cambia por tu logo real
            contentDescription = "Logo",
            modifier = Modifier
                .size(100.dp)
                .padding(top = 16.dp)
        )

        // CAMPOS DE TEXTO
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            SignUpField("Usuario")
            SignUpField("Correo Electronico")
            SignUpField("Contraseña")
            SignUpField("Confirmar Contraseña")

            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9AF5B4)),
                shape = RoundedCornerShape(50),
                modifier = Modifier.shadow(4.dp)
            ) {
                Text("Confirmar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        // REGISTRO CON REDES SOCIALES
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Text("O regístrate con")

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                IconButton(onClick = { /* TODO */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google), // 🔁 tu logo de Google
                        contentDescription = "Google",
                        modifier = Modifier.size(32.dp)
                    )
                }
                IconButton(onClick = { /* TODO */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook), // 🔁 tu logo de Facebook
                        contentDescription = "Facebook",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpField(label: String) {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        placeholder = {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        singleLine = true
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}


