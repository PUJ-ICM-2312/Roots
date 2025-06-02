package com.example.roots.service

import com.example.roots.model.Usuario
import com.example.roots.repository.UsuarioRepository

class UsuarioService(private val repo: UsuarioRepository) {
    fun crear(usuario: Usuario) = repo.add(usuario)
    fun obtener(id: String, onResult: (Usuario?) -> Unit) = repo.get(id, onResult)
    fun actualizar(usuario: Usuario) = repo.update(usuario)
    fun eliminar(id: String) = repo.delete(id)
}
