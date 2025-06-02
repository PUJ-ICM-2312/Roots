package com.example.roots.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

object LoginService {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmailPassword(
        email: String,
        password: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onError("No se pudo obtener el usuario actual")
                    }
                } else {
                    onError(task.exception?.message ?: "Error desconocido")
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logout() {
        auth.signOut()
    }
}
