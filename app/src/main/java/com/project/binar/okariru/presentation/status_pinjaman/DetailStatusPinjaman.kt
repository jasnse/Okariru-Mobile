package com.project.binar.okariru.presentation.status_pinjaman

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.data.status_pinjaman.dto.ListPinjamanDto
import com.project.binar.okariru.presentation.home.formatRupiah
import com.project.binar.okariru.presentation.shared.component.StatusChip
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.ui.theme.ColorOnPrimary
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorOutline
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing

@Composable
fun StatusPengajuanDetailPage(
    transPinjamanId: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: StatusPinjamanViewModel = sharedActivityViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val item = uiState.items.firstOrNull { it.transPinjamanId == transPinjamanId }

    if (item != null) {
        StatusPengajuanDetailContent(
            item = item,
            modifier = modifier,
            onBackClick = onBackClick,
        )
    } else {
        // data belum ke-load / belum ditemukan
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun StatusPengajuanDetailContent(
    item: ListPinjamanDto,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "Status Pengajuan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
        }
        HorizontalDivider(color = Color(0xFFEEEEEE))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg)
        ) {
            // Card: Loan Id + Jenis Pinjaman + Jumlah
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.lg),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "LOAN ID",
                                fontSize = 11.sp,
                                color = ColorOnSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.kodeTransaksi,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        StatusChip(status = item.statusPengajuan)
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(Spacing.md))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(text = "Jenis Pinjaman", fontSize = 12.sp, color = ColorOnSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.jenisPinjaman ?: "-",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Jumlah", fontSize = 12.sp, color = ColorOnSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatRupiah(item.nominalPinjaman),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = ColorPrimary
                            )
                        }
                    }
                }
            }

            // Card: Proses Pengajuan (timeline)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.lg),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Text(
                        text = "Proses Pengajuan",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))

                    val steps = buildProcessSteps(item)
                    steps.forEachIndexed { index, step ->
                        ProcessStepRow(step = step, isLast = index == steps.lastIndex)
                    }
                }
            }
        }
    }
}

private enum class StepState { DONE, CURRENT, PENDING }

private data class ProcessStep(
    val title: String,
    val subtitle: String,
    val state: StepState,
)


//state untuk progress bar
private fun buildProcessSteps(item: ListPinjamanDto): List<ProcessStep> {
    val status = item.statusPengajuan?.lowercase()

    val terkirimDone = item.tanggalPengajuan != null
    val reviewDone = item.tanggalReview != null || status in listOf("direview", "disetujui", "ditolak")
    val approvalDone = status in listOf("dicairkan","disetujui", "ditolak")
    val approvalCurrent = status == "direview"
    val pencairanDone = status == "dicairkan"

    return listOf(
        ProcessStep(
            title = "Pengajuan Terkirim",
            subtitle = item.tanggalPengajuan ?: "-",
            state = if (terkirimDone) StepState.DONE else StepState.PENDING,
        ),
        ProcessStep(
            title = "Verifikasi Pengajuan",
            subtitle = item.tanggalReview ?: "Menunggu verifikasi",
            state = when {
                reviewDone -> StepState.DONE
                terkirimDone -> StepState.CURRENT
                else -> StepState.PENDING
            },
        ),
        ProcessStep(
            title = "Persetujuan Akhir",
            subtitle = when {
                status == "ditolak" -> "Pengajuan ditolak"
                approvalDone -> item.tanggalApproval ?: "-"
                approvalCurrent -> "Estimasi selesai dalam 2 jam."
                else -> "Menunggu verifikasi selesai"
            },
            state = when {
                approvalDone -> StepState.DONE
                approvalCurrent -> StepState.CURRENT
                else -> StepState.PENDING
            },
        ),
        ProcessStep(
            title = "Pencairan Dana",
            subtitle = if (pencairanDone) "Dana telah dicairkan" else "Menunggu persetujuan akhir",
            state = when {
                pencairanDone -> StepState.DONE
                status == "disetujui" -> StepState.CURRENT
                else -> StepState.PENDING
            },
        ),
    )
}

//tempat buat tampilin stepIndicator
@Composable
private fun ProcessStepRow(step: ProcessStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            StepIndicator(state = step.state)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(36.dp)
                        .background(if (step.state == StepState.DONE) ColorPrimary else ColorOutline)
                )
            }
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(modifier = Modifier.padding(bottom = if (!isLast) Spacing.lg else Spacing.none)) {
            Text(
                text = step.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = when (step.state) {
                    StepState.PENDING -> ColorOnSurfaceVariant
                    StepState.CURRENT -> ColorPrimary
                    StepState.DONE -> MaterialTheme.colorScheme.onSurface
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = step.subtitle,
                fontSize = 12.sp,
                color = ColorOnSurfaceVariant
            )
        }
    }
}

@Composable
private fun StepIndicator(state: StepState) {
    when (state) {
        StepState.DONE -> Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(ColorPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = ColorOnPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
        StepState.CURRENT -> Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(2.dp, ColorPrimary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ColorPrimary)
            )
        }
        StepState.PENDING -> Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(ColorOutline)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StatusPengajuanDetailPagePreview() {

    val mockPinjaman = ListPinjamanDto(
        kodeTransaksi = "LN-20231024-001",
        statusPengajuan = "direview", // "direview", "disetujui", "ditolak", "dicairkan"
        jenisPinjaman = "Pinjaman Multiguna",
        nominalPinjaman = 50000000,
        tanggalPengajuan = "24 Okt 2023, 10:30 WIB",
        tanggalReview = "24 Okt 2023, 11:00 WIB",
        tanggalApproval = null,
        transPinjamanId = 1,
        customerId = 11,
        customerName = "Agus",
        pinjamanId = 1,
        tenor = 1,
        noteMarketing = null,
        noteBm = null,
        noteBackOffice = null,
        lastUpdate = null,
        lastUpdateBy = null,
    )
    OkariruTheme {
        StatusPengajuanDetailContent(
            item = mockPinjaman,
            onBackClick = {}
        )
    }
}