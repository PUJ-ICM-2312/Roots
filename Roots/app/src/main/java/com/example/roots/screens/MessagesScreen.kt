package com.example.roots.screens

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.roots.model.Chat
import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository
import com.example.roots.service.ChatService
import com.example.roots.service.InmuebleService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("UnrememberedMutableState")
@Composable
fun MessagesScreen(navController: NavController) {
    // 1) Obtenemos el UID del usuario actual (si no está logueado, salimos)
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

    // 2) Instanciamos los servicios
    val inmService = remember { InmuebleService(InmuebleRepository()) }
    val chatService = remember { ChatService() }
    val context = LocalContext.current

    // 3) Estados:
    //    misPropiedades -> lista de Inmueble publicados por este usuario
    var misPropiedades by remember { mutableStateOf<List<Inmueble>>(emptyList()) }
    //    allChats -> todos los chats en los que participa este usuario
    var allChats by remember { mutableStateOf<List<Chat>>(emptyList()) }
    //    filtros -> conjunto de propertyIds seleccionados para filtrar
    var filtros by remember { mutableStateOf<Set<String>>(emptySet()) }

    // ─── CARGAR PROPIEDADES PROPIAS ───
    LaunchedEffect(currentUserId) {
        inmService.getPropertiesOfUser(currentUserId) { lista ->
            misPropiedades = lista
        }
    }

    // ─── ESCUCHAR CHATS ───
    var listenerChats: ListenerRegistration? = null
    DisposableEffect(currentUserId) {
        // Creamos el Query que trae todos los chats donde 'participantes' contenga currentUserId
        val query: Query = chatService.getChatsForUser(currentUserId)

        listenerChats = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Toast.makeText(context, "Error al cargar chats", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val lista = snapshot.documents.mapNotNull { doc ->
                    val chat = doc.toObject(Chat::class.java)
                    chat?.copy(id = doc.id)
                }
                allChats = lista
            }
        }
        onDispose {
            listenerChats?.remove()
        }
    }

    // ─── FILTRAR CHATS ───
    val chatsAMostrar by derivedStateOf {
        if (filtros.isEmpty()) {
            allChats
        } else {
            allChats.filter { it.propertyId in filtros }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // ─── LISTA HORIZONTAL DE PROPIEDADES PROPIAS (FILTROS) ───
        if (misPropiedades.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(vertical = 8.dp)
            ) {
                items(misPropiedades) { prop ->
                    // Cada tarjeta es clickeable para alternar filtro
                    val seleccionado = prop.id in filtros
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(width = 120.dp, height = 120.dp)
                            .clickable {
                                filtros = if (seleccionado) {
                                    filtros - prop.id
                                } else {
                                    filtros + prop.id
                                }
                            },
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (seleccionado) Color(0xFFB2DFDB) else Color.White
                        )
                    ) {
                        Column {
                            // Foto (primera URL en prop.fotos)
                            if (prop.fotos.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(prop.fotos.first()),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(80.dp)
                                        .fillMaxWidth(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .height(80.dp)
                                        .fillMaxWidth()
                                        .background(Color.LightGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Sin foto", fontSize = 12.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prop.barrio,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // ─── LISTADO VERTICAL DE CHATS ───
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(chatsAMostrar) { chat ->
                ChatListItem(chat = chat, navController = navController)
                Divider()
            }
        }
    }
}

@Composable
fun ChatListItem(chat: Chat, navController: NavController) {
    // Mostramos:
    // 1) Imagen (chat.propertyFoto)
    // 2) Barrio (chat.propertyBarrio)
    // 3) Último mensaje (chat.ultimoMensaje)
    // 4) Fecha/hora (chat.timestampUltimoMensaje)

    val fecha = remember(chat.timestampUltimoMensaje) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(chat.timestampUltimoMensaje))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("chat_room/${chat.id}") }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Foto del inmueble (si existe)
        if (chat.propertyFoto.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(chat.propertyFoto),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.propertyBarrio,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chat.ultimoMensaje.ifBlank { "— Sin mensajes —" },
                fontSize = 14.sp,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(text = fecha, fontSize = 12.sp, color = Color.Gray)
    }
}
