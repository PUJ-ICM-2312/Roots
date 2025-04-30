package com.example.roots.model

data class Usuario(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val fotoPath: String,   // ruta local a la imagen de perfil
    val celular: String,
    val cedula: String
    // más adelante: val publicados: List<Int>, val favoritos: List<Int>
)
