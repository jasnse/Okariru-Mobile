package com.project.binar.okariru.data.document.dto

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.serialization.Serializable
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Serializable
data class DocumentUploadResponseDto(
    val dokumenId: Int? = null,
    val namaFile: String? = null,
    val pathfile: String? = null,
)

