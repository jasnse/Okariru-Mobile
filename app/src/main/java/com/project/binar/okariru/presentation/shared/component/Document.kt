package com.project.binar.okariru.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.binar.okariru.presentation.Pinjaman.DocumentUploadItem
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.ColorPrimaryContainer
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing




@Composable
fun DocumentUploadCard(
    item: DocumentUploadItem,
    onPickFromCamera: () -> Unit,
    onPickFromFile: () -> Unit,
) {
    val accentColor = if (item.isRequired) ColorPrimary else ColorOnSurfaceVariant
    var showPickerDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(if (item.isRequired) ColorPrimaryContainer else Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Column {
                    Row {
                        Text(
                            text = item.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item.isRequired) {
                            Text(
                                text = " *",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Text(
                        text = if (item.isRequired) "Wajib diunggah" else "Opsional",
                        fontSize = 12.sp,
                        color = ColorOnSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            UploadFileButton(
                fileName = item.fileName,
                accentColor = accentColor,
                onClick = { showPickerDialog = true }
            )
        }
    }
    if (showPickerDialog) {
        AlertDialog(
            onDismissRequest = { showPickerDialog = false },
            title = { Text("Pilih sumber dokumen") },
            text = { Text("Ambil foto langsung dari kamera, atau pilih file yang sudah tersimpan.") },
            confirmButton = { TextButton(onClick = {
                    showPickerDialog = false
                    onPickFromCamera()
                }) { Text("Kamera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPickerDialog = false
                    onPickFromFile()
                }) { Text("File") }
            }
        )
    }
}



@Composable
fun UploadFileButton(
    fileName: String?,
    accentColor: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .dashedBorder(color = accentColor.copy(alpha = 0.5f), radius = Radius.md)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.md),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.UploadFile,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = fileName ?: "Unggah File",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            maxLines = 1
        )
    }
}

fun Modifier.dashedBorder(color: Color, radius: androidx.compose.ui.unit.Dp) = this.drawBehind {
    val stroke = Stroke(
        width = 1.5.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(radius.toPx())
    )
}



@Preview(showBackground = true, name = "KARTU - Wajib Belum Unggah")
@Composable
private fun DocumentUploadCardRequiredPreview() {
    OkariruTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DocumentUploadCard(
                item = DocumentUploadItem(
                    icon = Icons.Filled.Badge,
                    title = "KTP",
                    isRequired = true
                ),
                onPickFromCamera = {},
                onPickFromFile = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "KARTU - Opsional Sudah Unggah")
@Composable
private fun DocumentUploadCardUploadedPreview() {
    OkariruTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DocumentUploadCard(
                item = DocumentUploadItem(
                    icon = Icons.Filled.Badge,
                    title = "Kartu Keluarga (KK)",
                    isRequired = false,
                    fileName = "kk_terbaru_2024.pdf"
                ),
                onPickFromCamera = {},
                onPickFromFile = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "TOMBOL - Kosong & Terisi")
@Composable
private fun UploadFileButtonPreview() {
    OkariruTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UploadFileButton(
                fileName = null,
                accentColor = ColorPrimary,
                onClick = {}
            )
            UploadFileButton(
                fileName = "dokumen_gaji.pdf",
                accentColor = ColorPrimary,
                onClick = {}
            )
        }
    }
}