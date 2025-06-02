package com.example.roots.model


data class Inmueble(
    val id: String = "",
    val direccion: String = "",
    val barrio: String = "",
    val ciudad: String = "",
    val precio: Float = 0f,
    val estrato: Int = 0,
    val numBanos: Int = 0,
    val numParqueaderos: Int = 0,
    val numHabitaciones: Int = 0,
    val metrosCuadrados: Float = 0f,
    val mensualidadAdministracion: Float = 0f,
    val antiguedad: Int = 0,
    val numFavoritos: Int = 0,
    val descripcion: String = "",
    val fotos: List<String> = emptyList(),
    val fechaPublicacion: Long = 0L,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val tipoPublicacion: TipoPublicacion = TipoPublicacion.Venta,
    val tipoInmueble: TipoInmueble = TipoInmueble.Apartamento
)
