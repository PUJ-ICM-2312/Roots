package com.example.roots.repository

import com.example.roots.model.Mensaje
import com.google.firebase.firestore.FirebaseFirestore

class MensajeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("mensajes")

    fun add(mensaje: Mensaje) {
        collection.document(mensaje.id.toString()).set(mensaje)
    }

    fun get(id: Int, onResult: (Mensaje?) -> Unit) {
        collection.document(id.toString()).get().addOnSuccessListener {
            onResult(it.toObject(Mensaje::class.java))
        }
    }

    fun update(mensaje: Mensaje) {
        collection.document(mensaje.id.toString()).set(mensaje)
    }

    fun delete(id: Int) {
        collection.document(id.toString()).delete()
    }
}
