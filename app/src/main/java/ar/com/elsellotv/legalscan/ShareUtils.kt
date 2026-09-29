package ar.com.elsellotv.legalscan

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * El pdfUri que llega acá ya es un Uri de nuestro propio FileProvider (ver PdfCache.kt),
 * así que cualquier app puede abrirlo una vez que le otorgamos el permiso de lectura.
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
        } catch (e: SecurityException) {
            Toast.makeText(context, context.getString(R.string.share_error), Toast.LENGTH_SHORT).show()
        }
    }

    fun shareViaEmail(context: Context, pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        safeStart(context, Intent.createChooser(intent, context.getString(R.string.share_email)))
    }

    fun shareGeneric(context: Context, pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        safeStart(context, Intent.createChooser(intent, context.getString(R.string.share_other)))
    }

    private fun safeStart(context: Context, chooserIntent: Intent) {
        try {
            context.startActivity(chooserIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, context.getString(R.string.share_error), Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(context, context.getString(R.string.share_error), Toast.LENGTH_SHORT).show()
        }
    }
}
