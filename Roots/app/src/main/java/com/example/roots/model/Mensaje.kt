package com.example.roots.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Mensaje(
    val id: String = "",
    val idChat: String = "",
    val idEmisor: String = "",
    val contenido: String = "",
    val timestamp: Long = 0L,
    val leidoPor: List<String> = emptyList(),
    val idReceptor: String = ""

)
