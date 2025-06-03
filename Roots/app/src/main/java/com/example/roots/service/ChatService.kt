// ChatService.kt
package com.example.roots.service

import com.example.roots.model.Chat
import com.example.roots.model.Mensaje
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatService(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val chatsCol = db.collection("chats")

    /**
     * 1) Busca un chat existente entre userA y userB para propertyId.
     * 2) Si existe, retorna su chatId a través de onResult.
     * 3) Si no existe, crea uno nuevo con los datos denormalizados (foto, barrio) y retorna el nuevo ID.
     */
    fun createOrGetChat(
        currentUserId: String,
        ownerUserId: String,
        propertyId: String,
        propertyFoto: String,
        propertyBarrio: String,
        onResult: (String?) -> Unit
    ) {
        // 1) Query: chats donde 'propertyId' == propertyId AND 'participantes' array-contains currentUserId
        // Luego verificamos si en esa lista de resultados alguno tenga participantes = [currentUserId, ownerUserId] (u orden inverso).
        chatsCol
            .whereEqualTo("propertyId", propertyId)
            .whereArrayContains("participantes", currentUserId)
            .get()
            .addOnSuccessListener { querySnap ->
                // Buscamos si existe un doc en el que también contenga ownerUserId
                val existing = querySnap.documents.firstOrNull { doc ->
                    val lista = doc.get("participantes") as? List<*>
                    lista?.contains(ownerUserId) == true
                }
                if (existing != null) {
                    // Ya existe, devolvemos su ID
                    onResult(existing.id)
                } else {
                    // No existe: creamos nuevo chat
                    val nuevoDoc = chatsCol.document()
                    val now = System.currentTimeMillis()
                    val nuevoChat = Chat(
                        id = nuevoDoc.id,
                        participantes = listOf(currentUserId, ownerUserId),
                        propertyId = propertyId,
                        propertyFoto = propertyFoto,
                        propertyBarrio = propertyBarrio,
                        fechaCreacion = now,
                        ultimoMensaje = "",
                        timestampUltimoMensaje = now
                    )
                    nuevoDoc.set(nuevoChat)
                        .addOnSuccessListener { onResult(nuevoDoc.id) }
                        .addOnFailureListener { onResult(null) }
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    /**
     * Retorna un Query para obtener todos los chats del usuario dado,
     * ordenados por timestampUltimoMensaje DESC.
     */
    fun getChatsForUser(userId: String): Query {
        return FirebaseFirestore.getInstance()
            .collection("chats")
            .whereArrayContains("participantes", userId)
    }


    /**
     * Envía un mensaje a /chats/{chatId}/messages y actualiza el campo 'ultimoMensaje'
     * y 'timestampUltimoMensaje' en el documento principal del chat.
     */
    fun sendMessage(
        chatId: String,
        mensaje: Mensaje,
        remitenteId: String,
        onResult: (Boolean) -> Unit
    ) {
        val chatDoc = chatsCol.document(chatId)

        // 1) Actualizar último mensaje + timestamp
        val updateData = mapOf(
            "ultimoMensaje" to mensaje.contenido,
            "timestampUltimoMensaje" to mensaje.timestamp,
            "vistoPor" to mapOf(
                mensaje.idEmisor to true,
                // El otro usuario (receptor) a false:
                mensaje.idReceptor to false
            )

        )


        chatDoc.update(updateData)
            .addOnFailureListener { /* ignoramos error de update parcial */ }

        // 2) Insertar mensaje en subcolección
        chatDoc.collection("messages")
            .add(mensaje.copy(id = "")) // Firestore asigna ID automáticamente
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }



    /**
     * Retorna un Query que devuelve todos los mensajes de un chat ordenados por timestamp ASC.
     */
    fun getMessagesOfChat(chatId: String): Query {
        return chatsCol.document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
    }

    fun marcarComoLeido(chatId: String, userId: String) {
        val chatRef = db.collection("chats").document(chatId)
        chatRef.update("vistoPor.$userId", true)
    }

}
