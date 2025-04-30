package com.example.roots.data

import com.example.roots.model.Inmueble
import com.example.roots.model.TipoInmueble
import com.example.roots.model.TipoPublicacion

object MockInmuebles {
    val sample = listOf(
        Inmueble(
            id = 1,
            direccion = "Cra 50 #20-30, Bogotá",
            precio = 350_000_000f,
            estrato = 4,
            numBaños = 2,
            numHabitaciones = 3,
            metrosCuadrados = 85f,
            descripcion = "Apartamento amplio con buena iluminación",
            fotos = listOf("https://.../img1.jpg"),
            tipoPublicacion = TipoPublicacion.Venta,
            tipoInmueble = TipoInmueble.Apartamento,
            numFavoritos = 0,
            mensualidadAdministracion = 150_000f,
            latitud = 4.654321,
            longitud = -74.056789
        ),
        Inmueble(
            id = 2,
            direccion = "Cl 85 #15-10, Bogotá",
            precio = 450_000_000f,
            estrato = 5,
            numBaños = 3,
            numHabitaciones = 4,
            metrosCuadrados = 120f,
            descripcion = "Penthouse con terraza",
            fotos = listOf("https://.../img2.jpg"),
            tipoPublicacion = TipoPublicacion.Venta,
            tipoInmueble = TipoInmueble.Penthouse,
            numFavoritos = 0,
            mensualidadAdministracion = 250_000f,
            latitud = 4.679012,
            longitud = -74.073456
        )
        // … más inmuebles de prueba
    )
}
