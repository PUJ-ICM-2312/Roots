package com.example.roots.util

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await

/**
 * Sube la URI (de tipo content:// o file://) a Firebase Storage y retorna la URL pública.
 * Si algo falla, lanza excepción.
 *
 * @param imageUri URI de la imagen seleccionada (por galería o cámara).
 * @param folderPath Carpeta en Storage donde guardar (por ejemplo, "avatars").
 *
 * @return String con la URL pública de descarga (https://firebasestorage.googleapis.com/…)
 */
suspend fun uploadProfileImageToFirebase(
    imageUri: Uri,
    folderPath: String = "avatars"
): String {
    // 1) Obtén la instancia de Storage
    val storage = FirebaseStorage.getInstance()

    // 2) Genera un nombre único
    val filename = "${System.currentTimeMillis()}_${imageUri.lastPathSegment}"
    val ref: StorageReference = storage.reference.child("$folderPath/$filename")

    // 3) Sube la imagen y espera a que termine
    ref.putFile(imageUri).await()

    // 4) Obtén la URL pública
    return ref.downloadUrl.await().toString()
}

