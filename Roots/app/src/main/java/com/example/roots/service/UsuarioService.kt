package com.example.roots.service

import com.example.roots.model.Usuario
import com.example.roots.repository.UsuarioRepository
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioService(private val repo: UsuarioRepository) {
    private val db = FirebaseFirestore.getInstance()

    fun crear(usuario: Usuario, onResult: (Boolean) -> Unit) {
        repo.add(usuario, onResult)
    }

    fun obtener(
        userId: String,
        onResult: (Usuario?) -> Unit
    ) {
        db.collection("usuarios")
            .document(userId)
            .get()
            .addOnSuccessListener { snap ->
                val usuario = snap.toObject(Usuario::class.java)
                onResult(usuario?.copy(id = snap.id))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Al llamar a 'actualizar', se sobrescribe TODO el documento en Firestore
     * con el Usuario completo (incluyendo las listas mutables).
     */
    fun actualizar(
        usuario: Usuario,
        onResult: (Boolean) -> Unit
    ) {
        if (usuario.id.isBlank()) {
            onResult(false)
            return
        }
        repo.update(usuario, onResult)
    }

    fun eliminar(id: String, onResult: (Boolean) -> Unit) {
        repo.delete(id, onResult)
    }
}
