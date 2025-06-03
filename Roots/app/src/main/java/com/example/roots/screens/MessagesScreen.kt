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
    import android.util.Log
    import androidx.compose.foundation.shape.CircleShape

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

        var selectedTab by remember { mutableStateOf(0) } // 0 = Mis inmuebles, 1 = Mis mensajes enviados

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
                    Log.e("CHAT_FIRESTORE", "SnapshotListener error: ${error.message}", error)
                    Toast.makeText(context, "Error al cargar chats: ${error.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        val chat = doc.toObject(Chat::class.java)
                        chat?.copy(id = doc.id)
                    }
                    allChats = lista
                } else {
                    Toast.makeText(context, "Snapshot vacío", Toast.LENGTH_SHORT).show()
                }
            }

            onDispose {
                listenerChats?.remove()
            }
        }

        // ─── FILTRAR CHATS ───
        val chatsAMostrar by derivedStateOf {
            if (selectedTab == 0) {
                val misIds = misPropiedades.map { it.id }.toSet()
                if (filtros.isEmpty()) {
                    allChats.filter { it.propertyId in misIds }
                } else {
                    allChats.filter { it.propertyId in filtros }
                }
            } else {
                val misIds = misPropiedades.map { it.id }.toSet()
                allChats.filter { it.propertyId !in misIds }
            }
        }


        Scaffold(
            topBar = {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Mis inmuebles") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Mis mensajes enviados") }
                    )
                }
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ─── LISTA HORIZONTAL DE PROPIEDADES PROPIAS (FILTROS) ───
                if (selectedTab == 0 && misPropiedades.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(vertical = 8.dp)
                    ) {
                        items(misPropiedades) { prop ->
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
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    if (prop.fotos.isNotEmpty()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(prop.fotos.first()),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(50)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(50))
                                                .background(Color.LightGray),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Sin foto", fontSize = 12.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = prop.barrio,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                }

                            }
                        }
                    }
                }

                // ─── LISTADO VERTICAL DE CHATS ───
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(chatsAMostrar) { chat ->
                        ChatListItem(chat = chat, navController = navController, currentUserId = currentUserId)
                        Divider()
                    }
                }
            }
        }

    }

    @Composable
    fun ChatListItem(chat: Chat, navController: NavController, currentUserId: String) {
        val fecha = remember(chat.timestampUltimoMensaje) {
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(chat.timestampUltimoMensaje))
        }

        val noLeido = chat.vistoPor[currentUserId] != true
        val receptorId = chat.participantes.firstOrNull { it != currentUserId } ?: return

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val receptorId = chat.participantes.firstOrNull { it != currentUserId } ?: return@clickable
                    navController.navigate("chat_room/${chat.id}/$receptorId")
                }

                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (chat.propertyFoto.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(chat.propertyFoto),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(50)),
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
                Text(text = chat.propertyBarrio, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = chat.ultimoMensaje.ifBlank { "— Sin mensajes —" },
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = fecha, fontSize = 12.sp, color = Color.Gray)
                if (noLeido) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                    )
                }
            }
        }
    }
