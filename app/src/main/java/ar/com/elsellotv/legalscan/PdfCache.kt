package ar.com.elsellotv.legalscan

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * El PDF que entrega ML Kit vive en un proveedor de contenido que solo LegalScan puede leer.
 * Para poder compartirlo con otra app (WhatsApp, correo, etc.) hay que copiarlo primero a la
 * caché propia y exponerlo con nuestro propio FileProvider — sin este paso, Android rechaza
 * el permiso al abrir el archivo en la app de destino.
 */
object PdfCache {

    fun store(context: Context, source: Uri): Uri {
        val dir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        dir.listFiles()?.forEach { it.delete() }

        val destination = File(dir, "legalscan_${System.currentTimeMillis()}.pdf")
        context.contentResolver.openInputStream(source)?.use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        } ?: error("No se pudo abrir el PDF generado")

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", destination)
    }
}
