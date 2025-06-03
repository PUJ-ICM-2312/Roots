package com.example.roots.model

data class Usuario(
    val id: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val fotoPath: String = "",
    val celular: String = "",
    val cedula: String = "",
    val publicados: MutableList<Inmueble> = mutableListOf(),
    val favoritos: MutableList<Inmueble> = mutableListOf(),
    val tarjetas: MutableList<Tarjeta> = mutableListOf(),
    val suscripciones: MutableList<Suscripcion> = mutableListOf(),
    val chatIds: MutableList<String> = mutableListOf()
) {
    fun puedePublicar(): Boolean {
        return suscripciones.any { it.activo }
    }
}
