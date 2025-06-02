package com.example.roots.service

import com.example.roots.model.Tarjeta
import com.example.roots.repository.TarjetaRepository

class TarjetaService(private val repo: TarjetaRepository) {
    fun crear(tarjeta: Tarjeta) = repo.add(tarjeta)
    fun obtener(id: Int, onResult: (Tarjeta?) -> Unit) = repo.get(id, onResult)
    fun actualizar(tarjeta: Tarjeta) = repo.update(tarjeta)
    fun eliminar(id: Int) = repo.delete(id)
}
