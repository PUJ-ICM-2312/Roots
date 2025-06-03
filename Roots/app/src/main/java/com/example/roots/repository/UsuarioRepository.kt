package com.example.roots.repository

import com.example.roots.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {
    private val db = FirebaseFirestore.getInstance().collection("usuarios")

    fun add(usuario: Usuario, onResult: (Boolean) -> Unit) {
        if (usuario.id.isBlank()) {
            onResult(false)
            return
        }
        db.document(usuario.id)
            .set(usuario)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun delete(id: String, onResult: (Boolean) -> Unit) {
        if (id.isBlank()) {
            onResult(false)
            return
        }
        db.document(id)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getById(id: String, onResult: (Usuario?) -> Unit) {
        if (id.isBlank()) {
            onResult(null)
            return
        }
        db.document(id)
            .get()
            .addOnSuccessListener { snap ->
                val u = snap.toObject(Usuario::class.java)
                onResult(u?.copy(id = snap.id))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun update(usuario: Usuario, onResult: (Boolean) -> Unit) {
        if (usuario.id.isBlank()) {
            onResult(false)
            return
        }
        val data = hashMapOf(
            "nombres" to usuario.nombres,
            "apellidos" to usuario.apellidos,
            "correo" to usuario.correo,
            "fotoPath" to usuario.fotoPath,
            "celular" to usuario.celular,
            "cedula" to usuario.cedula
        )
        db.document(usuario.id)
            .update(data as Map<String, Any>)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}