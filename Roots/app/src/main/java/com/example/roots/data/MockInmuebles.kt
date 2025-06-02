package com.example.roots.data

import com.example.roots.model.`Inmueble.kt`
import com.example.roots.model.TipoInmueble
import com.example.roots.model.TipoPublicacion
import com.example.roots.R
import java.time.LocalDateTime

object MockInmuebles {
    val sample = listOf(
        `Inmueble.kt`(
            id = 1,
            direccion = "Cra 50 #20-30",
            precio = 350_000_000f,
            estrato = 4,
            numBaños = 2,
            numParqueaderos = 2,
            numHabitaciones = 3,
            metrosCuadrados = 85f,
            barrio = "La Castellana",
            ciudad = "Bogotá",
            descripcion = "Apartamento amplio con buena iluminación",
            fotos = listOf(
                R.drawable.inmueble1,
                R.drawable.inmueble3,
                R.drawable.inmueble6
            ),
            tipoPublicacion = TipoPublicacion.Venta,
            tipoInmueble = TipoInmueble.Apartamento,
            numFavoritos = 0,
            mensualidadAdministracion = 150_000f,
            antiguedad = 8,
            fechaPublicacion = System.currentTimeMillis(),
            latitud = 4.654321,
            longitud = -74.056789
        ),
        `Inmueble.kt`(
            id = 2,
            direccion = "Cl 85 #15-10, Bogotá",
            precio = 450_000_000f,
            estrato = 5,
            numBaños = 3,
            numParqueaderos = 1,
            numHabitaciones = 4,
            metrosCuadrados = 120f,
            barrio = "El Country",
            ciudad = "Bogotá",
            descripcion = "Penthouse con terraza",
            fotos = listOf(
                R.drawable.inmueble2,
                R.drawable.inmueble4,
                R.drawable.inmueble6
            ),
            tipoPublicacion = TipoPublicacion.Venta,
            tipoInmueble = TipoInmueble.Penthouse,
            numFavoritos = 0,
            mensualidadAdministracion = 250_000f,
            antiguedad = 18,
            fechaPublicacion = System.currentTimeMillis(),
            latitud = 4.679012,
            longitud = -74.073456
        ),
        `Inmueble.kt`(
            id                         = 3,
            direccion                  = "Cra 7 #112-25",
            precio                     = 480_000_000f,
            estrato                    = 5,
            numBaños                   = 2,
            numParqueaderos = 1,
            numHabitaciones            = 3,
            metrosCuadrados            = 95f,
            barrio = "Usaquén",
            ciudad = "Bogotá",
            descripcion                = "Apartamento luminoso con balcón y vista al parque.",
            fotos                      = listOf(
                R.drawable.inmueble2,
                R.drawable.inmueble6,
                R.drawable.inmueble1
            ),
            tipoPublicacion            = TipoPublicacion.Venta,
            tipoInmueble               = TipoInmueble.Apartamento,
            numFavoritos               = 4,
            mensualidadAdministracion  = 220_000f,
            antiguedad = 1,
            fechaPublicacion = System.currentTimeMillis(),
            latitud                    = 4.7195,
            longitud                   = -74.0383
        ),

        `Inmueble.kt`(
            id                         = 4,
            direccion                  = "Cl 85 #20-50",
            precio                     = 3_200_000f,
            estrato                    = 4,
            numBaños                   = 1,
            numParqueaderos = 3,
            numHabitaciones            = 1,
            metrosCuadrados            = 42f,
            barrio = "Chapinero",
            ciudad = "Bogotá",
            descripcion                = "Apartaestudio moderno en zona gastronómica.",
            fotos                      = listOf(
                R.drawable.inmueble1,
                R.drawable.inmueble2,
                R.drawable.inmueble3
            ),
            tipoPublicacion            = TipoPublicacion.Arriendo,
            tipoInmueble               = TipoInmueble.Apartaestudio,
            numFavoritos               = 7,
            mensualidadAdministracion  = 180_000f,
            antiguedad = 28,
            fechaPublicacion = System.currentTimeMillis(),
            latitud                    = 4.6644,
            longitud                   = -74.0554
        ),

        `Inmueble.kt`(
            id                         = 5,
            direccion                  = "Av El Dorado #30-15",
            precio                     = 650_000_000f,
            estrato                    = 3,
            numBaños                   = 2,
            numParqueaderos = 1,
            numHabitaciones            = 2,
            metrosCuadrados            = 78f,
            barrio = "Teusaquillo",
            ciudad = "Bogotá",
            descripcion                = "Cómodo dúplex cerca de la zona universitaria.",
            fotos                      = listOf(
                R.drawable.inmueble4,
                R.drawable.inmueble5,
                R.drawable.inmueble6
            ),
            tipoPublicacion            = TipoPublicacion.Venta,
            tipoInmueble               = TipoInmueble.Casa,
            numFavoritos               = 2,
            mensualidadAdministracion  = 0f,
            antiguedad = 8,
            fechaPublicacion = System.currentTimeMillis(),
            latitud                    = 4.6374,
            longitud                   = -74.0895
        ),

        `Inmueble.kt`(
            id                         = 6,
            direccion                  = "Cll 140 #10-20",
            precio                     = 250_000_000f,
            estrato                    = 3,
            numBaños                   = 1,
            numParqueaderos = 1,
            numHabitaciones            = 2,
            metrosCuadrados            = 60f,
            barrio = "Suba",
            ciudad = "Bogotá",
            descripcion                = "Local comercial en sector residencial, alto tránsito.",
            fotos                      = listOf(
                R.drawable.inmueble1,
                R.drawable.inmueble4,
                R.drawable.inmueble5
            ),
            tipoPublicacion            = TipoPublicacion.Venta,
            tipoInmueble               = TipoInmueble.LocalComercial,
            numFavoritos               = 5,
            mensualidadAdministracion  = 150_000f,
            antiguedad = 20,
            fechaPublicacion = System.currentTimeMillis(),
            latitud                    = 4.7450,
            longitud                   = -74.0931
        ),

        `Inmueble.kt`(
            id                         = 7,
            direccion                  = "Av de Las Américas #68-40",
            precio                     = 1_800_000f,
            estrato                    = 2,
            numBaños                   = 1,
            numParqueaderos = 0,
            numHabitaciones            = 1,
            metrosCuadrados            = 35f,
            barrio = "Kennedy",
            ciudad = "Bogotá",
            descripcion                = "Habitación amoblada en casa familiar, ideal para estudiantes.",
            fotos                      = listOf(
                R.drawable.inmueble4,
                R.drawable.inmueble3,
                R.drawable.inmueble2
            ),
            tipoPublicacion            = TipoPublicacion.Arriendo,
            tipoInmueble               = TipoInmueble.Apartamento,
            numFavoritos               = 10,
            mensualidadAdministracion  = 100_000f,
            antiguedad = 15,
            fechaPublicacion = System.currentTimeMillis(),
            latitud                    = 4.6147,
            longitud                   = -74.1450
        )
        // … más inmuebles de prueba
    )
}
