package com.project.binar.okariru.presentation.shared.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.binar.okariru.data.angsuran.dto.AngsuranDto
import com.project.binar.okariru.presentation.home.formatRupiah
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing

@Composable
fun statusAngsuranColors(status: String): Pair<Color, Color> = when (status.lowercase()) {
    "lunas" -> Color(0xFFD1FAE5) to Color(0xFF059669)
    "kurang bayar" -> Color(0xFFFEE2E2) to Color(0xFFDC2626)
    else -> Color(0xFFFFF3CD) to Color(0xFFD97706) // "belum bayar"
}

val NAMA_BULAN = listOf("Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des")

fun formatTanggalJatuhTempo(tanggalIso: String): String {
    val bagian = tanggalIso.split("-")
    if (bagian.size != 3) return tanggalIso
    val bulanIndex = bagian[1].toIntOrNull()?.minus(1) ?: return tanggalIso
    val tanggal = bagian[2].toIntOrNull() ?: return tanggalIso
    if (bulanIndex !in NAMA_BULAN.indices) return tanggalIso
    return "Jatuh tempo: $tanggal ${NAMA_BULAN[bulanIndex]} ${bagian[0]}"
}

@Composable
fun AngsuranRow(angsuran: AngsuranDto) {
    val (statusBg, statusText) = statusAngsuranColors(angsuran.statusAngsuran)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.md))
            .background(Color(0xFFF8F9FA))
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // badge nomor tenor
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ColorPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${angsuran.tenor}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
        }

        Spacer(modifier = Modifier.width(Spacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Bulan ke-${angsuran.tenor}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formatTanggalJatuhTempo(angsuran.tanggalJatuhTempo),
                fontSize = 11.sp,
                color = ColorOnSurfaceVariant
            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = "Pokok ${formatRupiah(angsuran.jumlahPokok)}  •  Bunga ${formatRupiah(angsuran.jumlahBunga.toLong())}",
//                fontSize = 11.sp,
//                color = ColorOnSurfaceVariant
//            )
        }

        Spacer(modifier = Modifier.width(Spacing.sm))

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (angsuran.statusAngsuran.equals("Kurang Bayar", ignoreCase = true)) {
                    formatRupiah(angsuran.sisaTagihan)
                } else {
                    formatRupiah(angsuran.totalAngsuran)
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(Radius.pill))
                    .background(statusBg)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = angsuran.statusAngsuran, fontSize = 10.sp, fontWeight = FontWeight.Medium, color = statusText)
            }
        }
    }
}

@Preview(showBackground = true, name = "Belum Bayar")
@Composable
private fun AngsuranRowBelumBayarPreview() {
    val mockAngsuran = AngsuranDto(
        angsuranId = 1,
        transPinjamanId = 101,
        jumlahPokok = 2083333,
        jumlahBunga = 125000.0,
        totalAngsuran = 2208333,
        tanggalJatuhTempo = "2026-10-01",
        statusAngsuran = "Belum Bayar",
        tenor = 1,
        sisaTagihan = 2208333
    )

    OkariruTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AngsuranRow(angsuran = mockAngsuran)
        }
    }
}

@Preview(showBackground = true, name = "Lunas")
@Composable
private fun AngsuranRowLunasPreview() {
    val mockAngsuran = AngsuranDto(
        angsuranId = 2,
        transPinjamanId = 101,
        jumlahPokok = 2083333,
        jumlahBunga = 114583.0,
        totalAngsuran = 2197916,
        tanggalJatuhTempo = "2026-11-01",
        statusAngsuran = "Lunas",
        tenor = 2,
        sisaTagihan = 0
    )

    OkariruTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AngsuranRow(angsuran = mockAngsuran)
        }
    }
}

@Preview(showBackground = true, name = "Kurang Bayar")
@Composable
private fun AngsuranRowKurangBayarPreview() {
    val mockAngsuran = AngsuranDto(
        angsuranId = 3,
        transPinjamanId = 101,
        jumlahPokok = 2083333,
        jumlahBunga = 104166.0,
        totalAngsuran = 2187499,
        tanggalJatuhTempo = "2026-12-01",
        statusAngsuran = "Kurang Bayar",
        tenor = 3,
        sisaTagihan = 500000
    )

    OkariruTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AngsuranRow(angsuran = mockAngsuran)
        }
    }
}