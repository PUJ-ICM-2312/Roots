package com.example.roots.model

data class Suscripcion(
    val id: String = "",
    val usuarioId: String = "",
    val planId: String = "",
    val fechaInicio: Long = 0L,
    val mesesContratados: Int = 0,
    val fechaExpiracion: Long = 0L // ej. fechaInicio + mesesContratados * 30 dias
)
