package com.example.roots.model

data class Chat(
    val id: String = "",
    val participantes: List<String> = emptyList(), // UIDs
    val fechaCreacion: Long = 0L,
    val ultimoMensaje: String = "",
    val timestampUltimoMensaje: Long = 0L
)
