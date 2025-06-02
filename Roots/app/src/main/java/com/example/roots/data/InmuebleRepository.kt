package com.example.roots.data

import androidx.compose.runtime.mutableStateListOf
import com.example.roots.model.`Inmueble.kt`

object InmuebleRepository {
    // Estado observable para Compose
    private val `_inmueble.kts` = mutableStateListOf<`Inmueble.kt`>().apply {
        addAll(MockInmuebles.sample)   // tu lista inicial de mocks
    }

    // Lectura pública
    val `inmueble.kts`: List<`Inmueble.kt`> get() = `_inmueble.kts`

    // Agrega uno nuevo
    fun add(inm: `Inmueble.kt`) {
        `_inmueble.kts`.add(inm)
    }

    // Genera un nuevo ID incremental
    fun nextId(): Int =
        (`_inmueble.kts`.maxOfOrNull { it.id } ?: 0) + 1
}
