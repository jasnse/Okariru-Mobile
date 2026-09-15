package com.project.binar.okariru.data.document

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

//ubah uri file (yang di upload oleh user ke mobile menjadi multipart yang bisa di accept sama BE nya

fun Context.uriToMultipart(uri: Uri, partName: String = "file"): MultipartBody.Part {
    val fileName = queryFileName(uri) ?: "upload_${System.currentTimeMillis()}"
    val tempFile = File(cacheDir, fileName)
    contentResolver.openInputStream(uri)?.use { input ->
        tempFile.outputStream().use { output -> input.copyTo(output) }
    }
    val mimeType = contentResolver.getType(uri)?.toMediaTypeOrNull()
    val requestBody = tempFile.asRequestBody(mimeType)
    return MultipartBody.Part.createFormData(partName, fileName, requestBody)
}

private fun Context.queryFileName(uri: Uri): String? {
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && nameIndex >= 0) return cursor.getString(nameIndex)
    }
    return null
}