package com.example.roots.service

import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository

class InmuebleService(private val repo: InmuebleRepository) {

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
}
