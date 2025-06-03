package com.example.roots.screens

import com.example.roots.service.NotificationUtils
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
import androidx.navigation.NavController
import com.example.roots.model.Mensaje
import com.example.roots.service.ChatService
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    receptorId: String
)
{
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val chatService = remember { ChatService() }
    val context = LocalContext.current
    LaunchedEffect(chatId, currentUserId) {
        chatService.marcarComoLeido(chatId, currentUserId)
    }


    // 1) Estado para mensajes
    var mensajes by remember { mutableStateOf<List<Mensaje>>(emptyList()) }

    // 2) Escuchar mensajes en tiempo real
    LaunchedEffect(chatId) {
        chatService.getMessagesOfChat(chatId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                // 🔔--- detectar SOLO los documentos recién añadidos
                val nuevos = snapshot?.documentChanges
                    ?.filter { it.type == com.google.firebase.firestore.DocumentChange.Type.ADDED }
                    ?.mapNotNull { dc ->
                        dc.document.toObject(Mensaje::class.java)
                            ?.copy(id = dc.document.id, idChat = chatId)
                    } ?: emptyList()

                // 🔔--- notificar si lo envió otro usuario
                nuevos.forEach { msg ->
                    if (msg.idEmisor != currentUserId) {
                        NotificationUtils.showNewMessageNotification(
                            context     = context,
                            senderName  = "Nuevo mensaje",
                            messageText = msg.contenido,
                            chatId      = chatId,
                            receptorId  = receptorId      // o msg.idEmisor si prefieres
                        )
                    }
                }

                // lista completa para la UI
                mensajes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Mensaje::class.java)?.copy(id = doc.id, idChat = chatId)
                } ?: emptyList()
            }
    }

    // 3) Campo de texto para escribir
    var nuevoTexto by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            items(mensajes) { msg ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (msg.idEmisor == currentUserId)
                        Arrangement.End else Arrangement.Start
                ) {
                    Text(
                        text = msg.contenido,
                        color = if (msg.idEmisor == currentUserId) Color.White else Color.Black,
                        modifier = Modifier
                            .background(
                                if (msg.idEmisor == currentUserId) Color(0xFF4CAF50)
                                else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp)
                    )
                }
            }
        }
        Divider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = nuevoTexto,
                onValueChange = { nuevoTexto = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") }
            )
            IconButton(
                onClick = {
                    if (nuevoTexto.trim().isNotEmpty()) {
                        val mensaje = Mensaje(
                            id = "",
                            idChat = chatId,
                            idEmisor = currentUserId,
                            idReceptor = receptorId, // ✅ Aquí ahora sí lo tienes
                            contenido = nuevoTexto.trim(),
                            timestamp = System.currentTimeMillis(),
                            leidoPor = listOf(currentUserId)
                        )
                        chatService.sendMessage(chatId, mensaje, remitenteId = currentUserId) { success ->
                            if (success) nuevoTexto = ""
                        }

                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
            }
        }
    }
}

