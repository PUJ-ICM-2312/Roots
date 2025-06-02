package com.example.roots.service

import com.example.roots.model.Suscripcion
import com.example.roots.repository.SuscripcionRepository

class SuscripcionService(private val repo: SuscripcionRepository) {
    fun crear(suscripcion: Suscripcion) = repo.add(suscripcion)
    fun obtener(id: Int, onResult: (Suscripcion?) -> Unit) = repo.get(id, onResult)
    fun actualizar(suscripcion: Suscripcion) = repo.update(suscripcion)
    fun eliminar(id: Int) = repo.delete(id)
}
