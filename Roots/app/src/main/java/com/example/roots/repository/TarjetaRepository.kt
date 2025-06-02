package com.example.roots.repository

import com.example.roots.model.Tarjeta
import com.google.firebase.firestore.FirebaseFirestore

class TarjetaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("tarjetas")

    fun add(tarjeta: Tarjeta) {
        collection.document(tarjeta.id.toString()).set(tarjeta)
    }

    fun get(id: Int, onResult: (Tarjeta?) -> Unit) {
        collection.document(id.toString()).get().addOnSuccessListener {
            onResult(it.toObject(Tarjeta::class.java))
        }
    }

    fun update(tarjeta: Tarjeta) {
        collection.document(tarjeta.id.toString()).set(tarjeta)
    }

    fun delete(id: Int) {
        collection.document(id.toString()).delete()
    }
}
