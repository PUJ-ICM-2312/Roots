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
                // snapshot.id es el mismo que userId
                onResult(usuario?.copy(id = snap.id))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun actualizar(
        usuario: Usuario,
        onResult: (Boolean) -> Unit
    ) {
        if (usuario.id.isBlank()) {
            // No podemos actualizar si no tenemos un ID válido
            onResult(false)
            return
        }
        val dataMap = hashMapOf(
            "nombres" to usuario.nombres,
            "apellidos" to usuario.apellidos,
            "correo" to usuario.correo,
            "fotoPath" to usuario.fotoPath,
            "celular" to usuario.celular,
            "cedula" to usuario.cedula
        )
        db.collection("usuarios")
            .document(usuario.id)
            .update(dataMap as Map<String, Any>)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
    fun eliminar(id: String, onResult: (Boolean) -> Unit) {
        repo.delete(id, onResult)
    }
}
