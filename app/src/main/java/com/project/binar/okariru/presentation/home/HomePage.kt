package com.project.binar.okariru.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.presentation.shared.component.FloatingNavItem
import com.project.binar.okariru.presentation.shared.component.MainFeatureCard
import com.project.binar.okariru.presentation.shared.component.PromoCard
import com.project.binar.okariru.ui.theme.OkariruTheme
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HomePage(
    viewModel: HomeViewModel = hiltViewModel(),
    onPinjamanClick: () -> Unit = {},
    onPembayaranClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onStatusPinjamanClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        uiState = uiState,
        onPinjamanClick = onPinjamanClick,
        onPembayaranClick = onPembayaranClick,
        onProfileClick = onProfileClick,
        onNotificationClick = onNotificationClick,
        onStatusPinjamanClick = onStatusPinjamanClick,
    )
}

fun formatRupiah(amount: Number): String =
    "Rp " + NumberFormat.getNumberInstance(Locale("in", "ID")).format(amount)

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onPinjamanClick: () -> Unit = {},
    onPembayaranClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onStatusPinjamanClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // --- TOP APP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when {
                            uiState.isLoading -> "Memuat..."
                            uiState.SisaPlafond != null -> ("Hello.. ${uiState.userName}" )
                            else -> {"Failed"}
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "Notifikasi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // --- PLAFON PINJAMAN ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PLAFON PINJAMAN ANDA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                            )
                            Icon(
                                imageVector = Icons.Filled.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = when {
                                uiState.isLoading -> "Memuat..."
                                uiState.SisaPlafond != null -> formatRupiah(uiState.SisaPlafond)
                                else -> "Rp 0"
                            },
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- KARTU AJUKAN PINJAMAN ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onPinjamanClick
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Ajukan Pinjaman",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Proses cepat, cair dalam 5 menit",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- LAYANAN CEPAT ---
                Text(
                    text = "Layanan Cepat",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MainFeatureCard(
                        title = "Status Pinjaman",
                        icon = Icons.Filled.FactCheck,
                        modifier = Modifier.weight(1f),
                        onClick = onStatusPinjamanClick
                    )
                    MainFeatureCard(
                        title = "Pembayaran",
                        icon = Icons.Filled.History,
                        modifier = Modifier.weight(1f),
                        onClick = onPembayaranClick
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- PROMO & INFO ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Promo & Info",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Lihat Semua",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PromoCard(
                        title = "Diskon Pinjaman 0% 🎉",
                        description = "Nikmati bebas biaya admin untuk pengajuan pinjaman pertama minggu ini.",
                        backgroundColor = Color(0xFFFFEBEE),
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight()
                    )
                    PromoCard(
                        title = "Cashback Pembayaran Tagihan",
                        description = "Dapatkan cashback hingga Rp 50.000 setiap pembayaran lewat aplikasi.",
                        backgroundColor = Color(0xFFFFCDD2),
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight()
                    )
                    PromoCard(
                        title = "Undang Teman & Raih Bonus",
                        description = "Bagikan kode referralmu dan dapatkan keuntungan ekstra.",
                        backgroundColor = Color(0xFFFFECB3),
                        modifier = Modifier
                            .width(240.dp)
                            .fillMaxHeight()
                    )
                }

                // Ruang kosong supaya konten terakhir tidak tertutup kartu navigasi mengambang
                Spacer(modifier = Modifier.height(96.dp))
            }
        }


    }
}


@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    OkariruTheme {
        HomeContent(
            uiState = HomeUiState(isLoading = false, SisaPlafond = 67676767)
        )
    }
}
