package ar.com.elsellotv.legalscan

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * El Uri del PDF lo entrega ML Kit ya respaldado por su propio content provider,
 * así que solo hace falta reenviarlo con permiso de lectura: no copiamos el
 * archivo a ningún almacenamiento propio de la app.
 */
object ShareUtils {

    fun shareViaWhatsApp(context: Context, pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
        }
    }

    fun shareViaEmail(context: Context, pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_email)))
    }

    fun shareGeneric(context: Context, pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_other)))
    }
}
