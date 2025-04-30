package com.example.roots.model

data class Inmueble(
    val id: Int,
    val direccion: String,
    val precio: Float,
    val estrato: Int,
    val numBaños: Int,
    val numParqueaderos: Int,
    val numHabitaciones: Int,
    val metrosCuadrados: Float,
    val barrio: String,
    val ciudad: String,
    val descripcion: String,
    val fotos: List<String>,
    val tipoPublicacion: TipoPublicacion,
    val tipoInmueble: TipoInmueble,
    val numFavoritos: Int = 0,
    val mensualidadAdministracion: Float = 0f,
    val latitud: Double,
    val longitud: Double
)
