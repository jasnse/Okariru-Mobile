package com.project.binar.okariru.presentation.angsuran

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.data.angsuran.dto.AngsuranDto
import com.project.binar.okariru.presentation.home.formatRupiah
import com.project.binar.okariru.presentation.shared.component.AngsuranRow
import com.project.binar.okariru.presentation.shared.component.AppCurrencyTextField
import com.project.binar.okariru.presentation.shared.component.StatusPopup
import com.project.binar.okariru.presentation.shared.component.formatNumberWithComma
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.ui.theme.ColorOnPrimary
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing

private val ColorActiveBg = Color(0xFFD1FAE5)
private val ColorActiveText = Color(0xFF059669)

@Composable
fun RincianAngsuranPage(
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: AngsuranViewModel = sharedActivityViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedPinjaman = uiState.selectedPinjaman
    var inputBayar by remember { mutableStateOf("") }

    LaunchedEffect(selectedPinjaman?.transPinjamanId) {
        // reset tiap kali ganti pinjaman, biar tidak nyangkut nilai dari pinjaman sebelumnya
        inputBayar = ""
        selectedPinjaman?.let { viewModel.getAngsuranDetail(it.transPinjamanId) }
    }

    LaunchedEffect(selectedPinjaman?.pinjamanId) {
        selectedPinjaman?.let { viewModel.getPinjamanProduct(it.pinjamanId) }
    }

    LaunchedEffect(uiState.paySuccessMessage) {
        if (uiState.paySuccessMessage != null) {
            inputBayar = ""
        }
    }

    if (selectedPinjaman == null) return

    // total outstanding (jumlah semua sisa tagihan yang belum lunas), ditampilkan sebagai info, bukan buat diedit
    val totalOutstanding = uiState.angsuranList.sumOf { it.sisaTagihan }

    if (uiState.paySuccessMessage != null) {
        val wasLunas = uiState.isLunas
        StatusPopup(
            isSuccess = true,
            message = uiState.paySuccessMessage ?: "",
            onDismiss = {
                viewModel.dismissPaySuccessMessage()
                // baru redirect balik SETELAH popup ditutup
                if (wasLunas) onBackClick()
            }
        )
    }

    RincianAngsuranContent(
        jenisPinjaman = uiState.jenisPinjaman?.takeIf { it.isNotBlank() } ?: selectedPinjaman.jenisPinjaman ?: "-",
        totalPinjaman = selectedPinjaman.nominalPinjaman,
        gagalBayarMessage = uiState.detailErrorMessage ?: "",
        rateBunga = uiState.bungaRate ?: 0.0,
        angsuranList = uiState.angsuranList,
        nominalBayar = totalOutstanding,
        inputBayar = inputBayar,
        onInputBayarChange = { inputBayar = it },
        onBayarClick = {
            inputBayar.toIntOrNull()?.let { nominal -> viewModel.bayarAngsuran(selectedPinjaman.transPinjamanId, nominal) }
        },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun RincianAngsuranContent(
    totalPinjaman: Int,
    nominalBayar: Int,
    inputBayar: String,
    rateBunga: Double,
    gagalBayarMessage: String,
    jenisPinjaman: String,
    angsuranList: List<AngsuranDto>,
    onInputBayarChange: (String) -> Unit,
    onBayarClick: () -> Unit,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val visibleAngsuran = if (isExpanded) angsuranList else angsuranList.take(3)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
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
                text = "Rincian Angsuran",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Card: Info Pinjaman
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
                        Text(text = "Nama Pinjaman", fontSize = 12.sp, color = ColorOnSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = jenisPinjaman,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Radius.pill))
                            .background(ColorActiveBg)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Aktif", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ColorActiveText)
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.md))
                        .background(Color(0xFFF8F9FA))
                        .padding(Spacing.md)
                ) {
                    Text(text = "Outstanding Pembayaran", fontSize = 12.sp, color = ColorOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatRupiah(nominalBayar),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorPrimary
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.md))
                        .background(Color(0xFFF8F9FA))
                        .padding(Spacing.md)
                ) {
                    Text(text = "Rate Bunga", fontSize = 12.sp, color = ColorOnSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$rateBunga% / bulan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Card: Jadwal Angsuran
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.lg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text(
                    text = "Jadwal Angsuran",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Detail rincian pembayaran bulan ke bulan. (sudah termasuk bunga dan biaya lainnya)",
                    fontSize = 12.sp,
                    color = ColorOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                visibleAngsuran.forEachIndexed { index, angsuran ->
                    AngsuranRow(angsuran = angsuran)
                    if (index != visibleAngsuran.lastIndex) {
                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }
                }

                if (angsuranList.size > 3) {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Radius.md))
                            .clickable { isExpanded = !isExpanded }
                            .padding(vertical = Spacing.sm),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "Sembunyikan" else "Lihat Selengkapnya",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = ColorPrimary
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = ColorPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Card: Pembayaran
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Radius.lg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text(
                    text = "Pembayaran",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Masukkan nominal untuk membayar angsuran bulan ini.",
                    fontSize = 12.sp,
                    color = ColorOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                Text(text = "Nominal Bayar", fontSize = 12.sp, color = ColorOnSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                AppCurrencyTextField(
                    value = formatNumberWithComma(inputBayar),
                    onValueChange = onInputBayarChange,
                    label = "",
                    leadingIcon = Icons.Filled.Payments
                )

                Spacer(modifier = Modifier.height(Spacing.lg))

                if(gagalBayarMessage.isNotEmpty()){
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = gagalBayarMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = onBayarClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(Radius.md),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary,
                        contentColor = ColorOnPrimary,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Payments,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(text = "Bayar Sekarang", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}


@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun RincianAngsuranContentPreview() {
    val mockAngsuran = listOf(
        AngsuranDto(1, 1, 2083333, 125000.0, 2208333, "2026-10-01", "Belum Bayar", 1, 2208333),
        AngsuranDto(2, 1, 2083333, 114583.0, 2197916, "2026-11-01", "Belum Bayar", 2, 2197916),
        AngsuranDto(3, 1, 2083333, 104166.0, 2187499, "2026-12-01", "Belum Bayar", 3, 2187499),
        AngsuranDto(4, 1, 2083333, 93750.0, 2177083, "2027-01-01", "Belum Bayar", 4, 2177083),
    )

    OkariruTheme {
        RincianAngsuranContent(
            jenisPinjaman = "Pinjaman Modal",
            totalPinjaman = 25000000,
            rateBunga = 0.5,
            angsuranList = mockAngsuran,
            nominalBayar = 8791831,
            inputBayar = "2208333",
            onInputBayarChange = {},
            onBayarClick = {},
            gagalBayarMessage = "",
            onBackClick = {},
            modifier = Modifier
        )
    }
}
