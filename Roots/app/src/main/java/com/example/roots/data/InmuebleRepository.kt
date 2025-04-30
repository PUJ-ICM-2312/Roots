package com.example.roots.data

import androidx.compose.runtime.mutableStateListOf
import com.example.roots.model.Inmueble

object InmuebleRepository {
    // Estado observable para Compose
    private val _inmuebles = mutableStateListOf<Inmueble>().apply {
        addAll(MockInmuebles.sample)   // tu lista inicial de mocks
    }

    // Lectura pública
    val inmuebles: List<Inmueble> get() = _inmuebles

    // Agrega uno nuevo
    fun add(inm: Inmueble) {
        _inmuebles.add(inm)
    }

    // Genera un nuevo ID incremental
    fun nextId(): Int =
        (_inmuebles.maxOfOrNull { it.id } ?: 0) + 1
}
