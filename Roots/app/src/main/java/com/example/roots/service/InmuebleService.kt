package com.example.roots.service

import com.example.roots.model.Inmueble
import com.example.roots.model.TipoInmueble
// import com.example.roots.model.TipoPublicacion // No es necesario si comparas strings directamente
import com.example.roots.repository.InmuebleRepository
import com.example.roots.screens.FilterState // Asegúrate que la ruta es correcta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Calendar
import java.util.concurrent.TimeUnit

class InmuebleService(private val repo: InmuebleRepository) { // repo no se usa en getFilteredInmuebles directamente, pero podría usarse para otras cosas

    // Tus funciones existentes: crear, obtener, actualizar, eliminar, getPropertiesOfUser...
    fun crear(inmueble: Inmueble, onResult: (String?) -> Unit) {
        repo.add(inmueble, onResult)
    }

    fun obtener(id: String, onResult: (Inmueble?) -> Unit) {
        repo.get(id, onResult)
    }

    fun actualizar(inmueble: Inmueble, onResult: (Boolean) -> Unit) {
        repo.update(inmueble, onResult)
    }

    fun eliminar(id: String, onResult: (Boolean) -> Unit) {
        repo.delete(id, onResult)
    }

    fun getPropertiesOfUser(userId: String, onResult: (List<Inmueble>) -> Unit) {
        FirebaseFirestore.getInstance().collection("inmuebles")
            .whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { snap ->
                val lista = snap.documents.mapNotNull { doc ->
                    doc.toObject(Inmueble::class.java)?.copy(id = doc.id)
                }
                onResult(lista)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }


    fun getFilteredInmuebles(filters: FilterState, onResult: (List<Inmueble>) -> Unit) {
        var query: Query = FirebaseFirestore.getInstance().collection("inmuebles")

        if (filters.selectedEstado != "Todos") {
            query = query.whereEqualTo("estado", filters.selectedEstado)
        }

        if (filters.selectedTipoOferta.isNotBlank() && (filters.selectedTipoOferta == "Venta" || filters.selectedTipoOferta == "Arriendo")) {
            query = query.whereEqualTo("tipoPublicacion", filters.selectedTipoOferta)
        }

        if (filters.selectedTipoInmueble.isNotEmpty()) {
            val tipoInmuebleValues = filters.selectedTipoInmueble.mapNotNull { TipoInmueble.fromString(it)?.name }
            if (tipoInmuebleValues.isNotEmpty()) {
                query = query.whereIn("tipoInmueble", tipoInmuebleValues.take(30)) // Firestore whereIn limit (actual es 30)
            }
        }

        val habitacionNumbers = filters.selectedHabitaciones.mapNotNull { it.split(" ")[0].toIntOrNull() }
        val hasPlusHabitaciones = filters.selectedHabitaciones.any { it.contains("+") }
        val plusHabitacionesNum = filters.selectedHabitaciones.find { it.contains("+") }?.split("+")?.get(0)?.toIntOrNull()

        if (hasPlusHabitaciones && plusHabitacionesNum != null) {
            query = query.whereGreaterThanOrEqualTo("numHabitaciones", plusHabitacionesNum)
            // Si también hay números específicos, Firestore no permite OR en diferentes campos o mezclar whereIn con rangos en el mismo campo de forma simple
            // Esta lógica prioriza el "X+" si está presente.
        } else if (habitacionNumbers.isNotEmpty()) {
            query = query.whereIn("numHabitaciones", habitacionNumbers.take(30))
        }


        val banioNumbers = filters.selectedBanios.mapNotNull { it.split(" ")[0].toIntOrNull() }
        val hasPlusBanios = filters.selectedBanios.any { it.contains("+") }
        val plusBaniosNum = filters.selectedBanios.find { it.contains("+") }?.split("+")?.get(0)?.toIntOrNull()

        if (hasPlusBanios && plusBaniosNum != null) {
            query = query.whereGreaterThanOrEqualTo("numBanos", plusBaniosNum)
        } else if (banioNumbers.isNotEmpty()) {
            query = query.whereIn("numBanos", banioNumbers.take(30))
        }

        if (filters.selectedEstratos.isNotEmpty()) {
            val estratoNumbers = filters.selectedEstratos.mapNotNull {
                val parts = it.split(" ")
                if (parts.size > 1 && parts[0] == "Estrato") parts[1].toIntOrNull()
                else if (it == "Campestre") 0 // Asumiendo 0 para campestre, o el valor que uses
                else null
            }
            if (estratoNumbers.isNotEmpty()) {
                query = query.whereIn("estrato", estratoNumbers.take(30))
            }
        }


        filters.precioMin.toDoubleOrNull()?.let { if (it > 0) query = query.whereGreaterThanOrEqualTo("precio", it) }
        filters.precioMax.toDoubleOrNull()?.let { if (it > 0 && it >= (filters.precioMin.toDoubleOrNull() ?: 0.0) ) query = query.whereLessThanOrEqualTo("precio", it) }


        if (filters.selectedAntiguedad != "Todos") {
            when (filters.selectedAntiguedad) {
                "Menores a 1 año" -> query = query.whereEqualTo("antiguedad", 0)
                "de 1 a 8 años" -> {
                    query = query.whereGreaterThanOrEqualTo("antiguedad", 1)
                    query = query.whereLessThanOrEqualTo("antiguedad", 8)
                }
                "De 9 a 15 años" -> {
                    query = query.whereGreaterThanOrEqualTo("antiguedad", 9)
                    query = query.whereLessThanOrEqualTo("antiguedad", 15)
                }
                "De 16 a 30 años" -> {
                    query = query.whereGreaterThanOrEqualTo("antiguedad", 16)
                    query = query.whereLessThanOrEqualTo("antiguedad", 30)
                }
                "Mas de 30 años" -> query = query.whereGreaterThan("antiguedad", 30)
            }
        }

        filters.areaMin.toDoubleOrNull()?.let { if (it > 0) query = query.whereGreaterThanOrEqualTo("metrosCuadrados", it) }
        filters.areaMax.toDoubleOrNull()?.let { if (it > 0 && it >= (filters.areaMin.toDoubleOrNull() ?: 0.0)) query = query.whereLessThanOrEqualTo("metrosCuadrados", it) }


        if (filters.parqueadero) {
            query = query.whereGreaterThanOrEqualTo("numParqueaderos", 1)
        }

        if (filters.selectedFecha != "Todos") {
            val calendar = Calendar.getInstance()
            // Reset time to start of day for consistent comparisons
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val targetTimeMillis = when (filters.selectedFecha) {
                "Hoy" -> calendar.timeInMillis
                "Desde ayer" -> { calendar.add(Calendar.DAY_OF_YEAR, -1); calendar.timeInMillis }
                "Ultima semana" -> { calendar.add(Calendar.DAY_OF_YEAR, -7); calendar.timeInMillis }
                "Ultimos 15 dias" -> { calendar.add(Calendar.DAY_OF_YEAR, -15); calendar.timeInMillis }
                "Ultimos 30 dias" -> { calendar.add(Calendar.DAY_OF_YEAR, -30); calendar.timeInMillis }
                "Ultimos 40 dias" -> { calendar.add(Calendar.DAY_OF_YEAR, -40); calendar.timeInMillis }
                else -> -1L
            }
            if (targetTimeMillis != -1L) {
                query = query.whereGreaterThanOrEqualTo("fechaPublicacion", targetTimeMillis)
            }
        }

        // Firestore no soporta queries "not-in" de forma nativa y eficiente para "ocultar vistos" a gran escala.
        // Esto usualmente se maneja obteniendo una lista de IDs vistos por el usuario y filtrando en el cliente,
        // o si la lista de vistos es pequeña, se puede usar múltiples `whereNotEqualTo`.
        // Por ahora, el filtro `ocultarVistos` no se aplica en la query a Firestore.

        query.get()
            .addOnSuccessListener { documents ->
                val propertyList = documents.mapNotNull { document ->
                    document.toObject(Inmueble::class.java)?.copy(id = document.id)
                }
                // Aquí se podría aplicar el filtro "ocultarVistos" si tienes los IDs
                // val finalProperties = if (filters.ocultarVistos) {
                //    propertyList.filterNot { it.id in listaDeIdsVistosPorElUsuario }
                // } else {
                //    propertyList
                // }
                // onResult(finalProperties)
                onResult(propertyList)
            }
            .addOnFailureListener { exception ->
                // Log.e("InmuebleService", "Error fetching filtered inmuebles", exception)
                onResult(emptyList())
            }
    }
}