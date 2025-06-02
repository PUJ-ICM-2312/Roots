package com.example.roots.repository

import com.example.roots.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("usuarios")

    fun add(usuario: Usuario) {
        collection.document(usuario.id).set(usuario)
    }

    fun get(id: String, onResult: (Usuario?) -> Unit) {
        collection.document(id.toString()).get().addOnSuccessListener {
            onResult(it.toObject(Usuario::class.java))
        }
    }

    fun update(usuario: Usuario) {
        collection.document(usuario.id).set(usuario)
    }

    fun delete(id: String) {
        collection.document(id).delete()
    }
}
