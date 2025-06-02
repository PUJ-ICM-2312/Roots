package com.example.roots.repository

import com.example.roots.model.Inmueble
import com.google.firebase.firestore.FirebaseFirestore

class InmuebleRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("inmuebles")

    fun add(inmueble: Inmueble, onResult: (String?) -> Unit) {
        // 1) Generamos un nuevo ID “vacío”
        val docRef = collection.document()
        val nuevoId = docRef.id

        // 2) Creamos una copia del inmueble con ese ID
        val inmuebleConId = inmueble.copy(id = nuevoId)

        // 3) Guardamos en /inmuebles/{nuevoId}
        docRef.set(inmuebleConId)
            .addOnSuccessListener {
                onResult(nuevoId)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun get(id: String, onResult: (Inmueble?) -> Unit) {
        collection.document(id).get()
            .addOnSuccessListener { snap ->
                onResult(snap.toObject(Inmueble::class.java))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun update(inmueble: Inmueble, onResult: (Boolean) -> Unit) {
        if (inmueble.id.isBlank()) {
            onResult(false)
            return
        }
        collection.document(inmueble.id)
            .set(inmueble)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun delete(id: String, onResult: (Boolean) -> Unit) {
        collection.document(id)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getAll(onResult: (List<Inmueble>) -> Unit) {
        collection.get().addOnSuccessListener { result ->
            val list = result.mapNotNull { it.toObject(Inmueble::class.java) }
            onResult(list)
        }
    }

}
