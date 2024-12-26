package `in`.levelup.pdfreader.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun getFileNameFromUri(uri: Uri, context: Context): String {
    var fileName = "Unknown"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                fileName = cursor.getString(nameIndex)
            }
        }
    }
    return fileName.substringBeforeLast(".")
}