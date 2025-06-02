package com.example.roots.repository

import com.example.roots.model.Suscripcion
import com.google.firebase.firestore.FirebaseFirestore

class SuscripcionRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("suscripciones")

    fun add(suscripcion: Suscripcion) {
        collection.document(suscripcion.id.toString()).set(suscripcion)
    }

    fun get(id: String, onResult: (Suscripcion?) -> Unit) {
        collection.document(id.toString()).get().addOnSuccessListener {
            onResult(it.toObject(Suscripcion::class.java))
        }
    }

    fun update(suscripcion: Suscripcion) {
        collection.document(suscripcion.id.toString()).set(suscripcion)
    }

    fun delete(id: String) {
        collection.document(id.toString()).delete()
    }
}
