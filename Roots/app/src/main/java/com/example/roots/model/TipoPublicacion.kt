package com.example.roots.model

enum class TipoPublicacion(val label: String) {
    Venta("Venta"),
    Arriendo("Arriendo"),
    Temporada("Temporada"),
    Permuta("Permuta");

    companion object {
        fun fromString(value: String): TipoPublicacion? = try {
            valueOf(value)
        } catch (e: IllegalArgumentException) {
            null // O manejar el error de otra forma
        }
    }
}