package com.example.roots.repository

import com.example.roots.model.Mensaje
import com.google.firebase.firestore.FirebaseFirestore

class MensajeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("mensajes")

    fun add(mensaje: Mensaje) {
        collection.document(mensaje.id).set(mensaje)
    }

    fun get(id: String, onResult: (Mensaje?) -> Unit) {
        collection.document(id).get().addOnSuccessListener {
            onResult(it.toObject(Mensaje::class.java))
        }
    }

    fun update(mensaje: Mensaje) {
        collection.document(mensaje.id).set(mensaje)
    }

    fun delete(id: String) {
        collection.document(id).delete()
    }
}
