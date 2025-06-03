package com.example.roots.repository

import com.example.roots.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioRepository {
    private val db = FirebaseFirestore
        .getInstance()
        .collection("usuarios")

    /**
     * Agrega un nuevo usuario (con ID ya definido) en Firestore.
     * Si el campo usuario.id está en blanco, retorna onResult(false) inmediatamente.
     */
    fun add(usuario: Usuario, onResult: (Boolean) -> Unit) {
        if (usuario.id.isBlank()) {
            onResult(false)
            return
        }
        db.document(usuario.id)
            .set(usuario) // guarda todo el objeto, incluidas las listas vacías
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Elimina el documento cuyo ID es 'id' de la colección "usuarios".
     * Si 'id' está en blanco, retorna onResult(false) inmediatamente.
     */
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

    /**
     * Sobrescribe TODO el documento con el objeto Usuario completo.
     * Así Firestore almacenará los campos básicos + las listas (MutableList).
     */
    fun update(usuario: Usuario, onResult: (Boolean) -> Unit) {
        if (usuario.id.isBlank()) {
            onResult(false)
            return
        }
        db.document(usuario.id)
            .set(usuario) // <— reemplaza todo el documento con el objeto Usuario
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Obtiene un Usuario por su ID. Si 'id' está en blanco, devuelve null.
     * En caso de éxito, convierte el snapshot a Usuario y asigna el ID real.
     */
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
}
