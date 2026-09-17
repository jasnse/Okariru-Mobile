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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.binar.okariru.data.status_pinjaman.dto.ListPinjamanDto
import com.project.binar.okariru.presentation.home.formatRupiah
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing

@Composable
fun PengajuanCard(
    item: ListPinjamanDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            // Top Row: Code & Status Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.kodeTransaksi,
                    fontSize = 13.sp,
                    color = ColorOnSurfaceVariant
                )
                StatusChip(status = item.statusPengajuan)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title & Date
            Text(
                text = item.jenisPinjaman?: "-",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.tanggalPengajuan?: "-",
                fontSize = 13.sp,
                color = ColorOnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.md))
            HorizontalDivider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(Spacing.md))

            // Bottom Row: Nominal & Action Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Jumlah",
                        fontSize = 12.sp,
                        color = ColorOnSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatRupiah(item.nominalPinjaman),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.statusPengajuan.equals("ditolak", ignoreCase = true)) ColorOnSurfaceVariant else ColorPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(bottom = 2.dp)

                ) {
                    Text(
                        text = "Lihat Detail",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorPrimary
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = ColorPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String?) {
    val (backgroundColor, textColor) = when (status?.lowercase()) {
        "direview" -> Color(0xFFFFF3CD) to Color(0xFFD97706)
        "disetujui" -> Color(0xFFD1FAE5) to Color(0xFF059669)
        "ditolak" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
        "dicairkan" -> Color(0xFFD1FAE5) to Color(0xFF059669)
        "lunas" -> Color(0xFFD1FAE5) to Color(0xFF059669)
        else -> Color(0xFFFFF3CD) to Color(0xFFD97706)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.pill))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status ?: "Pengajuan",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}