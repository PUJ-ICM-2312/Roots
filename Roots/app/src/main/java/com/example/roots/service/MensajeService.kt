package com.example.roots.service

import com.example.roots.model.Mensaje
import com.example.roots.repository.MensajeRepository

class MensajeService(private val repo: MensajeRepository) {
    fun crear(mensaje: Mensaje) = repo.add(mensaje)
    fun obtener(id: Int, onResult: (Mensaje?) -> Unit) = repo.get(id, onResult)
    fun actualizar(mensaje: Mensaje) = repo.update(mensaje)
    fun eliminar(id: Int) = repo.delete(id)
}
