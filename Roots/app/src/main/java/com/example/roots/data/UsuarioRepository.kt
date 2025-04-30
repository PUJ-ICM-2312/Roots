package com.example.roots.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.roots.model.Usuario

object UsuarioRepository {
    private var nextId = 1

    // estado observable en Compose
    var usuario by mutableStateOf(
        Usuario(
            id         = nextId++,
            nombres    = "Diego",
            apellidos  = "Cortés Acevedo",
            correo     = "diegocortes@gmail.com",
            fotoPath   = "",           // vacío = sin foto aún
            celular    = "3012345678",
            cedula     = "1234567890"
        )
    )

        private set

    fun updateUsuario(nuevo: Usuario) {
        usuario = nuevo
    }
}
