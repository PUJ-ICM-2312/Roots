package com.example.roots.repository

import com.example.roots.model.Chat
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("chats")

    fun add(chat: Chat) {
        collection.document(chat.id.toString()).set(chat)
    }

    fun get(id: Int, onResult: (Chat?) -> Unit) {
        collection.document(id.toString()).get().addOnSuccessListener {
            onResult(it.toObject(Chat::class.java))
        }
    }

    fun update(chat: Chat) {
        collection.document(chat.id.toString()).set(chat)
    }

    fun delete(id: Int) {
        collection.document(id.toString()).delete()
    }
}
