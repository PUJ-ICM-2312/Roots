package com.example.roots.model

data class Plan(
    val id: String = "",
    val nombre: String = "",
    val precio: Float = 0f,
    val moneda: String = "",
    val duracionDias: Int = 0,
    val maxPublicaciones: Int = 0,
    val maxDiasPublicacion: Int = 0,
    val sinAnuncios: Boolean = false,
    val inmueblesDestacados: Boolean = false,
    val primerLugarBusqueda: Boolean = false,
    val estadisticasAvanzadas: Boolean = false,
    val soportePrioritario: Boolean = false,
    val estado: EstadoPlan = EstadoPlan.ACTIVO
)
