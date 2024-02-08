package uz.raytel.raytel.utils

import android.content.ContentResolver.SCHEME_CONTENT
import android.content.ContentResolver.SCHEME_FILE
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.UUID

internal object FileUtils {
    private const val SUBSTRING_START_INDEX = 0
    private const val SUBSTRING_END_INDEX = 48
    private const val OPEN_FILE_DESCRIPTOR_MODE = "r"

    @Suppress("NestedBlockDepth")
    private fun cloneFile(context: Context, uri: Uri): File? {
        return try {
            context.contentResolver.openFileDescriptor(uri, OPEN_FILE_DESCRIPTOR_MODE)?.use {
                val temporaryFile = createFile(
                    context = context,
                    extension = uri.getExtension(context)!!
                )
                val inputStream: InputStream = FileInputStream(it.fileDescriptor)
                BufferedInputStream(inputStream).use { iStream ->
                    temporaryFile.outputStream().use { outStream -> iStream.copyTo(outStream) }
                }
                temporaryFile
            }
        } catch (_: Exception) {
            null
        }
    }

    fun getRealImageUriFromTemporaryUri(context: Context, uri: Uri): Uri =
        Uri.fromFile(cloneFile(context, uri))

    private fun createFile(
        context: Context,
        extension: String
    ): File {
        val storageDir: File = context.externalCacheDir ?: context.cacheDir
        if (!storageDir.exists()) storageDir.mkdirs()
        val name = context.packageName + getRandomUUID()
        return File(storageDir, "$name.$extension")
    }

    private fun getRandomUUID(): String {
        return (UUID.randomUUID().toString() + UUID.randomUUID().toString()).replace("-", "")
            .substring(SUBSTRING_START_INDEX, SUBSTRING_END_INDEX)
    }

    private fun Uri.getExtension(context: Context): String? {
        return when (this.scheme) {
            SCHEME_CONTENT -> MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(this.getMime(context))

            SCHEME_FILE -> File(this.path!!).extension
            else -> MimeTypeMap.getFileExtensionFromUrl(this.toString())
        }
    }

    private fun Uri.getMime(context: Context): String {
        return when {
            scheme == SCHEME_FILE -> MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                MimeTypeMap.getFileExtensionFromUrl(
                    this.toString()
                )
            )

            scheme == SCHEME_CONTENT -> context.contentResolver.getType(this)
            URLUtil.isValidUrl(this.toString()) -> MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(this.path)
                )

            else -> ""
        } ?: throw NullPointerException("There is not mime type")
    }
}
