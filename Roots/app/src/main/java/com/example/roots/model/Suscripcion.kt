package com.example.roots.model

data class Suscripcion(
    val id: Int = 0,
    val fechaInicio: String = "",  // Guardar como ISO date en Firestore (usar LocalDate si luego lo transformas)
    val fechaFin: String = "",
    val activo: Boolean = false,
    val renovable: Boolean = false,
    val metodoPago: MetodoPago = MetodoPago.TARJETA,
    val ultimaFacturaId: String = "",
    val plan: Plan? = null  // relación explícita si se carga el plan completo
)
