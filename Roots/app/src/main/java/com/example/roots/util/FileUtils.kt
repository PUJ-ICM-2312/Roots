import android.content.Context
import android.net.Uri
import java.io.File
import java.util.UUID

fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
    // Carpeta “images” dentro de filesDir
    val imagesDir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
    // Nombre único
    val filename = "${UUID.randomUUID()}.jpg"
    val destFile = File(imagesDir, filename)

    return try {
        context.contentResolver.openInputStream(imageUri)?.use { input ->
            destFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        // Devuelve la ruta absoluta
        destFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
