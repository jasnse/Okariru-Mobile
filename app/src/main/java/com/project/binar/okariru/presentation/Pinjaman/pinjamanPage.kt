package com.project.binar.okariru.presentation.Pinjaman

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.project.binar.okariru.ui.theme.Radius
import java.io.File

@Composable
fun pinjaman() {
    val context = LocalContext.current
    var message by remember { mutableStateOf<String?>(null) }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    var captureUri by remember { mutableStateOf<Uri?>(null) }
    val cameraCancelled = "Camera Cancel"
    val cameraSaved = "Camera saved"

    val takePicture = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture() ) {
        saved -> previewUri = captureUri.takeIf { saved }
        message = if (saved) cameraSaved else cameraCancelled
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val uri = createCaptureUri(context)
            captureUri = uri
            takePicture.launch(uri)
        } else {
            message = "Camera permission denied"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        //ini untuk tampilin image nya
        previewUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "take picture from camera",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(Radius.md))
            )
        }
        Button(
            onClick = {
                val permissionStatus = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                )

                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    val uri = createCaptureUri(context)
                    captureUri = uri
                    takePicture.launch(uri)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        ) {
            Text("Request Camera Permission")
        }
    }

}


fun createCaptureUri(context: Context): Uri {
    val directory = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File.createTempFile("capture_", ".jpg", directory)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}