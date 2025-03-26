package com.example.roots.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roots.R

@Composable
fun ChatScreen(
    username: String = "Andres Perez",
    userImage: Int = R.drawable.usuario1, // temporal, puede ser perfil
    onBack: () -> Unit = {}
) {
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = userImage),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = username, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp) // ← aquí estaba el error
                )
                Button(
                    onClick = {
                        // Aquí podrías enviar el mensaje (agregarlo a una lista, etc.)
                        messageText = TextFieldValue("")
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD5FDE5))
                ) {
                    Text("Enviar", color = Color.Black)
                }
            }
        }

    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            // Simulación de mensajes anteriores
            ChatBubble("Hola, me interesa tu apartamento en Santa Viviana.", isMine = false)
            ChatBubble("¡Hola! Claro, ¿te gustaría visitarlo?", isMine = true)
            ChatBubble("Sí, ¿podría este sábado en la tarde?", isMine = false)
            ChatBubble("Perfecto, agendado a las 3:00 p.m.", isMine = true)
        }
    }
}

@Composable
fun ChatBubble(text: String, isMine: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isMine) Color(0xFFD5FDE5) else Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(text = text, color = Color.Black)
        }
    }
}
