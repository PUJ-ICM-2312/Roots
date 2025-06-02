package com.example.roots.repository

import com.example.roots.model.Inmueble
import com.google.firebase.firestore.FirebaseFirestore

class InmuebleRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("inmuebles")

    fun add(inmueble: Inmueble) {
        collection.document(inmueble.id.toString()).set(inmueble)
    }

    fun get(id: Int, onResult: (Inmueble?) -> Unit) {
        collection.document(id.toString()).get().addOnSuccessListener {
            onResult(it.toObject(Inmueble::class.java))
        }
    }

    fun update(inmueble: Inmueble) {
        collection.document(inmueble.id.toString()).set(inmueble)
    }

    fun delete(id: Int) {
        collection.document(id.toString()).delete()
    }

    fun getAll(onResult: (List<Inmueble>) -> Unit) {
        collection.get().addOnSuccessListener { result ->
            val list = result.mapNotNull { it.toObject(Inmueble::class.java) }
            onResult(list)
        }
    }

}
