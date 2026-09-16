package com.project.binar.okariru.presentation.status_pinjaman


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.data.status_pinjaman.dto.listPinjamanDto
import com.project.binar.okariru.presentation.home.formatRupiah
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing

@Composable
fun DaftarPengajuanPage(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onItemClick: (listPinjamanDto) -> Unit = {},
    viewModel: StatusPinjamanViewModel = sharedActivityViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadListPinjaman()
    }

    DaftarPengajuanContent(
        modifier = modifier,
        items = uiState.items,
        onBackClick = onBackClick,
        onItemClick = onItemClick,
    )
}

@Composable
fun DaftarPengajuanContent(
    modifier: Modifier = Modifier,
    items: List<listPinjamanDto> = emptyList(),
    onBackClick: () -> Unit = {},
    onItemClick: (listPinjamanDto) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
    ) {
        // App Bar / Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(color = MaterialTheme.colorScheme.surface, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Text(
                text = "Daftar Pengajuan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // List Pengajuan
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
            contentPadding = PaddingValues(bottom = Spacing.xl)
        ) {
            items(items, key = { it.transPinjamanId }) { item ->
                PengajuanCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun PengajuanCard(
    item: listPinjamanDto,
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

@Preview(showBackground = true)
@Composable
private fun PreviewDaftarPengajuanPage() {
    val dummyData = listOf(
        listPinjamanDto(
            transPinjamanId = 1,
            kodeTransaksi = "TRX-2026-00001",
            customerId = 1,
            customerName = "Budi Santoso",
            pinjamanId = 1,
            nominalPinjaman = 5000000,
            tenor = 6,
            statusPengajuan = "Direview"
        ),
        listPinjamanDto(
            transPinjamanId = 2,
            kodeTransaksi = "TRX-2026-00002",
            customerId = 1,
            customerName = "Budi Santoso",
            pinjamanId = 1,
            nominalPinjaman = 25000000,
            tenor = 12,
            statusPengajuan = "Disetujui"
        ),
        listPinjamanDto(
            transPinjamanId = 3,
            kodeTransaksi = "TRX-2026-00003",
            customerId = 1,
            customerName = "Budi Santoso",
            pinjamanId = 2,
            nominalPinjaman = 10000000,
            tenor = 12,
            statusPengajuan = "Ditolak"
        )
    )

    OkariruTheme {
        DaftarPengajuanContent(items = dummyData)
    }
}