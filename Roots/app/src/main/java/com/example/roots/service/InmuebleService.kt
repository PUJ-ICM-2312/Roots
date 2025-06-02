package com.example.roots.service

import com.example.roots.model.Inmueble
import com.example.roots.repository.InmuebleRepository

class InmuebleService(private val repo: InmuebleRepository) {
    fun crear(inmueble: Inmueble) = repo.add(inmueble)
    fun obtener(id: Int, onResult: (Inmueble?) -> Unit) = repo.get(id, onResult)
    fun actualizar(inmueble: Inmueble) = repo.update(inmueble)
    fun eliminar(id: Int) = repo.delete(id)
}
