package com.example.roots.service

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.roots.model.Inmueble
import com.example.roots.model.TipoInmueble
// import com.example.roots.model.TipoPublicacion // No es necesario si comparas strings directamente
import com.example.roots.repository.InmuebleRepository
import com.example.roots.screens.FilterState // Asegúrate que la ruta es correcta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
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

    @OptIn(UnstableApi::class)
    fun getFilteredInmuebles(filters: FilterState, onResult: (List<Inmueble>) -> Unit) {
        // Loguear el contenido de 'filters' para ver qué valores trae
        Log.d("InmuebleService", "getFilteredInmuebles: filtros = $filters")

        var query: Query = FirebaseFirestore.getInstance().collection("inmuebles")

        if (filters.selectedEstado != "Todos") {
            query = query.whereEqualTo("estado", filters.selectedEstado)
            Log.d("InmuebleService", "  -> filtro estado = ${filters.selectedEstado}")
        }

        if (filters.selectedTipoOferta.isNotBlank() &&
            (filters.selectedTipoOferta == "Venta" || filters.selectedTipoOferta == "Arriendo")
        ) {
            query = query.whereEqualTo("tipoPublicacion", filters.selectedTipoOferta)
            Log.d("InmuebleService", "  -> filtro tipoPublicacion = ${filters.selectedTipoOferta}")
        }

        if (filters.selectedTipoInmueble.isNotEmpty()) {
            val tipoInmuebleValues =
                filters.selectedTipoInmueble.mapNotNull { TipoInmueble.fromString(it)?.name }
            if (tipoInmuebleValues.isNotEmpty()) {
                query = query.whereIn("tipoInmueble", tipoInmuebleValues.take(30))
                Log.d("InmuebleService", "  -> filtro tipoInmueble IN $tipoInmuebleValues")
            }
        }

        val habitacionNumbers =
            filters.selectedHabitaciones.mapNotNull { it.split(" ")[0].toIntOrNull() }
        val hasPlusHabitaciones = filters.selectedHabitaciones.any { it.contains("+") }
        val plusHabitacionesNum = filters.selectedHabitaciones
            .find { it.contains("+") }
            ?.split("+")
            ?.get(0)
            ?.toIntOrNull()

        if (hasPlusHabitaciones && plusHabitacionesNum != null) {
            query = query.whereGreaterThanOrEqualTo("numHabitaciones", plusHabitacionesNum)
            Log.d("InmuebleService", "  -> filtro numHabitaciones >= $plusHabitacionesNum")
        } else if (habitacionNumbers.isNotEmpty()) {
            query = query.whereIn("numHabitaciones", habitacionNumbers.take(30))
            Log.d("InmuebleService", "  -> filtro numHabitaciones IN $habitacionNumbers")
        }

        val banioNumbers = filters.selectedBanios.mapNotNull { it.split(" ")[0].toIntOrNull() }
        val hasPlusBanios = filters.selectedBanios.any { it.contains("+") }
        val plusBaniosNum = filters.selectedBanios
            .find { it.contains("+") }
            ?.split("+")
            ?.get(0)
            ?.toIntOrNull()

        if (hasPlusBanios && plusBaniosNum != null) {
            query = query.whereGreaterThanOrEqualTo("numBanos", plusBaniosNum)
            Log.d("InmuebleService", "  -> filtro numBanos >= $plusBaniosNum")
        } else if (banioNumbers.isNotEmpty()) {
            query = query.whereIn("numBanos", banioNumbers.take(30))
            Log.d("InmuebleService", "  -> filtro numBanos IN $banioNumbers")
        }

        if (filters.selectedEstratos.isNotEmpty()) {
            val estratoNumbers = filters.selectedEstratos.mapNotNull {
                val parts = it.split(" ")
                if (parts.size > 1 && parts[0] == "Estrato") parts[1].toIntOrNull()
                else if (it == "Campestre") 0
                else null
            }
            if (estratoNumbers.isNotEmpty()) {
                query = query.whereIn("estrato", estratoNumbers.take(30))
                Log.d("InmuebleService", "  -> filtro estrato IN $estratoNumbers")
            }
        }

        filters.precioMin.toDoubleOrNull()?.let {
            if (it > 0) {
                query = query.whereGreaterThanOrEqualTo("precio", it)
                Log.d("InmuebleService", "  -> filtro precio >= $it")
            }
        }
        filters.precioMax.toDoubleOrNull()?.let {
            if (it > 0 && it >= (filters.precioMin.toDoubleOrNull() ?: 0.0)) {
                query = query.whereLessThanOrEqualTo("precio", it)
                Log.d("InmuebleService", "  -> filtro precio <= $it")
            }
        }

        if (filters.selectedAntiguedad != "Todos") {
            when (filters.selectedAntiguedad) {
                "Menores a 1 año" -> {
                    query = query.whereEqualTo("antiguedad", 0)
                    Log.d("InmuebleService", "  -> filtro antiguedad = 0")
                }

                "de 1 a 8 años" -> {
                    query = query.whereGreaterThanOrEqualTo("antiguedad", 1)
                    query = query.whereLessThanOrEqualTo("antiguedad", 8)
                    Log.d("InmuebleService", "  -> filtro antiguedad entre 1 y 8")
                }

                "De 9 a 15 años" -> {
                    query = query.whereGreaterThanOrEqualTo("antiguedad", 9)
                    query = query.whereLessThanOrEqualTo("antiguedad", 15)
                    Log.d("InmuebleService", "  -> filtro antiguedad entre 9 y 15")
                }

                "De 16 a 30 años" -> {
                    query = query.whereGreaterThanOrEqualTo("antiguedad", 16)
                    query = query.whereLessThanOrEqualTo("antiguedad", 30)
                    Log.d("InmuebleService", "  -> filtro antiguedad entre 16 y 30")
                }

                "Mas de 30 años" -> {
                    query = query.whereGreaterThan("antiguedad", 30)
                    Log.d("InmuebleService", "  -> filtro antiguedad > 30")
                }
            }
        }

        filters.areaMin.toDoubleOrNull()?.let {
            if (it > 0) {
                query = query.whereGreaterThanOrEqualTo("metrosCuadrados", it)
                Log.d("InmuebleService", "  -> filtro metrosCuadrados >= $it")
            }
        }
        filters.areaMax.toDoubleOrNull()?.let {
            if (it > 0 && it >= (filters.areaMin.toDoubleOrNull() ?: 0.0)) {
                query = query.whereLessThanOrEqualTo("metrosCuadrados", it)
                Log.d("InmuebleService", "  -> filtro metrosCuadrados <= $it")
            }
        }

        if (filters.parqueadero) {
            query = query.whereGreaterThanOrEqualTo("numParqueaderos", 1)
            Log.d("InmuebleService", "  -> filtro numParqueaderos >= 1")
        }

        if (filters.selectedFecha != "Todos") {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val targetTimeMillis = when (filters.selectedFecha) {
                "Hoy" -> calendar.timeInMillis.also {
                    Log.d(
                        "InmuebleService",
                        "  -> calculando fechaPublicacion >= inicio de Hoy ($it)"
                    )
                }

                "Desde ayer" -> calendar.apply { add(Calendar.DAY_OF_YEAR, -1) }.timeInMillis.also {
                    Log.d(
                        "InmuebleService",
                        "  -> calculando fechaPublicacion >= inicio de Ayer ($it)"
                    )
                }

                "Ultima semana" -> calendar.apply {
                    add(
                        Calendar.DAY_OF_YEAR,
                        -7
                    )
                }.timeInMillis.also {
                    Log.d(
                        "InmuebleService",
                        "  -> calculando fechaPublicacion >= hace 7 días ($it)"
                    )
                }

                "Ultimos 15 dias" -> calendar.apply {
                    add(
                        Calendar.DAY_OF_YEAR,
                        -15
                    )
                }.timeInMillis.also {
                    Log.d(
                        "InmuebleService",
                        "  -> calculando fechaPublicacion >= hace 15 días ($it)"
                    )
                }

                "Ultimos 30 dias" -> calendar.apply {
                    add(
                        Calendar.DAY_OF_YEAR,
                        -30
                    )
                }.timeInMillis.also {
                    Log.d(
                        "InmuebleService",
                        "  -> calculando fechaPublicacion >= hace 30 días ($it)"
                    )
                }

                "Ultimos 40 dias" -> calendar.apply {
                    add(
                        Calendar.DAY_OF_YEAR,
                        -40
                    )
                }.timeInMillis.also {
                    Log.d(
                        "InmuebleService",
                        "  -> calculando fechaPublicacion >= hace 40 días ($it)"
                    )
                }

                else -> -1L
            }

        }
    }

    suspend fun getAllInmuebles(): List<Inmueble> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("inmuebles")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Inmueble::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}