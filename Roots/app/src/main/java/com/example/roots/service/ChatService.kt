package com.example.roots.service

import com.example.roots.model.Chat
import com.example.roots.repository.ChatRepository

class ChatService(private val repo: ChatRepository) {
    fun crear(chat: Chat) = repo.add(chat)
    fun obtener(id: Int, onResult: (Chat?) -> Unit) = repo.get(id, onResult)
    fun actualizar(chat: Chat) = repo.update(chat)
    fun eliminar(id: Int) = repo.delete(id)
}
