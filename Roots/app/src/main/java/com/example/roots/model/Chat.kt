package com.example.roots.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Chat(
    val id: String = "",
    val participantes: List<String> = emptyList(), // UIDs
    val propertyId: String = "",                        // ID del inmueble
    val propertyFoto: String = "",                      // URL de la primera foto
    val propertyBarrio: String = "",                    // barrio del inmueble

    val fechaCreacion: Long = 0L,
    val ultimoMensaje: String = "",
    val timestampUltimoMensaje: Long = 0L
)
