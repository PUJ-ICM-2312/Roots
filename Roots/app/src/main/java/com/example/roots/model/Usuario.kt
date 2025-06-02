package com.example.roots.model

data class Usuario(
    val id: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val fotoPath: String = "",
    val celular: String = "",
    val cedula: String = "",
    val publicados: List<`Inmueble.kt`> = emptyList(),
    val favoritos: List<`Inmueble.kt`> = emptyList(),
    val tarjetas: List<Tarjeta> = emptyList(),
    val suscripciones: List<Suscripcion> = emptyList(),
    val chatIds: List<String> = emptyList()
) {
    fun puedePublicar(): Boolean {
        return suscripciones.any { it.activo }
    }
}
