package com.example.roots.model

enum class TipoInmueble(val label: String) {
    Apartamento("Apartamento"),
    Casa("Casa"),
    Penthouse("Penthouse"),
    LocalComercial("Local Comercial"),
    Lote("Lote"),
    Apartaestudio("Apartaestudio");

    companion object {
        fun fromString(value: String): TipoInmueble? {
            // Normaliza el string para que coincida con los nombres de los enums
            // Ejemplo: "Casa Campestre" -> "CasaCampestre"
            val formattedValue = value.replace(" ", "")
            return entries.find { it.name.equals(formattedValue, ignoreCase = true) }
        }
    }
}


