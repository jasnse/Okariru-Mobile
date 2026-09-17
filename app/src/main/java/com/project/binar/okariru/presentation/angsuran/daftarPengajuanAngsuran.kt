package com.project.binar.okariru.presentation.angsuran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.data.status_pinjaman.dto.ListPinjamanDto
import com.project.binar.okariru.presentation.shared.component.PengajuanCard
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Spacing

@Composable
fun DaftarPengajuanAngsuranPage(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onItemClick: (ListPinjamanDto) -> Unit = {},
    viewModel: AngsuranViewModel = sharedActivityViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getListPinjamanActive()
    }

    DaftarPengajuanAngsuranContent(
        modifier = modifier,
        items = uiState.items,
        onBackClick = onBackClick,
        onItemClick = { item ->
            viewModel.selectPinjaman(item)
            onItemClick(item)
        },
        onRetry = { viewModel.getListPinjamanActive() }
    )
}

@Composable
fun DaftarPengajuanAngsuranContent(
    modifier: Modifier = Modifier,
    items: List<ListPinjamanDto> = emptyList(),
    onBackClick: () -> Unit = {},
    onItemClick: (ListPinjamanDto) -> Unit = {},
    onRetry: () -> Unit = {}
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
                text = "Daftar Pinjaman Aktif",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ColorPrimary
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

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

@Preview(showBackground = true)
@Composable
fun DaftarPengajuanAngsuranContentPreview() {
    val mockItems = listOf(
        ListPinjamanDto(
            transPinjamanId = 1,
            kodeTransaksi = "TRX-2026-00001",
            customerId = 1,
            customerName = "Budi Santoso",
            pinjamanId = 1,
            nominalPinjaman = 5000000,
            tenor = 6,
            statusPengajuan = "Dicairkan"
        ),
        ListPinjamanDto(
            transPinjamanId = 2,
            kodeTransaksi = "TRX-2026-00001",
            customerId = 1,
            customerName = "Budi Santoso",
            pinjamanId = 1,
            nominalPinjaman = 5000000,
            tenor = 6,
            statusPengajuan = "Direview"
        )
    )
    OkariruTheme {
        DaftarPengajuanAngsuranContent(
            items = mockItems,
            onBackClick = {},
            onItemClick = {}
        )
    }
}
