package com.example.roots.service

import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository
import com.google.firebase.firestore.FirebaseFirestore

class InmuebleService(private val repo: InmuebleRepository) {

    fun crear(inmueble: Inmueble, onResult: (String?) -> Unit) {
        repo.add(inmueble, onResult)
    }

    fun obtener(id: String, onResult: (Inmueble?) -> Unit) {
        repo.get(id, onResult)
    }

    fun actualizar(inmueble: Inmueble, onResult: (Boolean) -> Unit) {
        repo.update(inmueble, onResult)
    }

    fun eliminar(id: String, onResult: (Boolean) -> Unit) {
        repo.delete(id, onResult)
    }
    fun getPropertiesOfUser(
        userId: String,
        onResult: (List<Inmueble>) -> Unit
    ) {
        // Asegúrate de que en Firestore en cada documento de "inmuebles"
        // tengas un campo llamado "usuarioId" con el UID del propietario.
        FirebaseFirestore.getInstance()
            .collection("inmuebles")
            .whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { snap ->
                val lista = snap.documents.mapNotNull { doc ->
                    // Convierte cada documento a Inmueble. Agregamos .copy(id = doc.id)
                    doc.toObject(Inmueble::class.java)?.copy(id = doc.id)
                }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}
