package com.mibaldi.fitapp.ui.common
import android.app.Activity.RESULT_OK
import android.provider.MediaStore
import android.provider.DocumentsContract
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.content.Intent
import com.mibaldi.fitapp.ui.profile.ACTIVITY_CHOOSE_FILE
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


fun getPath(context: Context, uri: Uri): String? {

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return Environment.getExternalStorageDirectory().path + "/" + split[1]
            }
            // TODO handle non-primary volumes
        } else if (isDownloadsDocument(uri)) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"),
                java.lang.Long.valueOf(id)
            )
            return getDataColumn(context, contentUri, null, null)
        } else if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])
            return getDataColumn(context, contentUri, selection, selectionArgs)
        }// MediaProvider
        // DownloadsProvider
    }
    // File
    // MediaStore (and general)
    return null
}

fun getDataColumn(
    context: Context,
    uri: Uri?,
    selection: String?,
    selectionArgs: Array<String>?
): String? {
    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)
    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor!!.moveToFirst()) {
            val index = cursor!!.getColumnIndexOrThrow(column)
            return cursor!!.getString(index)
        }
    } finally {
        if (cursor != null)
            cursor!!.close()
    }
    return null
}

fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.getAuthority()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.getAuthority()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.getAuthority()
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.getAuthority()
}


fun getFile(context:Context,requestCode: Int,resultCode: Int,data:Intent,nextFlow:(String)-> Unit){
    context.run {
        if (requestCode == ACTIVITY_CHOOSE_FILE) {
            if (resultCode == RESULT_OK) {
                try {
                    val uri = data.data
                    val mimeType = contentResolver.getType(uri!!)
                    var filename = ""
                    if (mimeType == null) {
                        val path = getPath(this, uri)
                        val file = File(path)
                        filename = file.name
                    } else {
                        val returnUri = data.data
                        val returnCursor =
                            contentResolver.query(returnUri!!, null, null, null, null)
                        returnCursor?.let {
                            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                            returnCursor.moveToFirst()
                            filename = returnCursor.getString(nameIndex)
                            val size = returnCursor.getLong(sizeIndex).toString()
                            returnCursor.close()
                        }

                    }
                    val fileSave = getExternalFilesDir(null)
                    val sourcePath = getExternalFilesDir(null).toString()
                    val newFile = "$sourcePath/$filename"
                    try {
                        copyFileStream(File(newFile), uri, this)
                        nextFlow(newFile)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

}

private fun copyFileStream(dest: File, uri: Uri, context: Context) {
    var inputStream: InputStream? = null
    var os: OutputStream? = null
    try {
        inputStream = context.contentResolver.openInputStream(uri)
        os = FileOutputStream(dest)
        val buffer = ByteArray(1024)
        var length: Int

        do {
            length = inputStream!!.read(buffer)
            os.write(buffer, 0, length)
        }while (length > 0 )

    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inputStream!!.close()
        os!!.close()
    }
}