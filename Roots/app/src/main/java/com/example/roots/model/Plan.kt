package com.example.roots.model

data class Plan(
    val id: String = "",
    val nombre: String = "",        // ej. "Básico", "Premium"
    val descripcion: String = "",   // ej. "1 mes de publicación sin límites"
    val precioMensual: Double = 0.0 // ej. 19.99
)