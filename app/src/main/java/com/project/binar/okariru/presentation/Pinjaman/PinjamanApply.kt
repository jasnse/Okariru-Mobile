package com.project.binar.okariru.presentation.Pinjaman

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.presentation.home.HomeViewModel
import com.project.binar.okariru.presentation.home.formatRupiah
import com.project.binar.okariru.presentation.shared.component.AppButton
import com.project.binar.okariru.presentation.shared.component.AppButtonVariant
import com.project.binar.okariru.presentation.shared.component.AppDropdownField
import com.project.binar.okariru.presentation.shared.component.AppTextField
import com.project.binar.okariru.presentation.shared.component.DocumentUploadCard
import com.project.binar.okariru.presentation.shared.component.SimulationHighlightRow
import com.project.binar.okariru.presentation.shared.component.SimulationRow
import com.project.binar.okariru.presentation.shared.component.SimulationSection
import com.project.binar.okariru.presentation.shared.component.StatusPopup
import com.project.binar.okariru.presentation.shared.component.StepIndicator
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.ui.theme.ColorOnPrimary
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorOutline
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.ColorPrimaryContainer
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing
import android.Manifest
import android.content.Context
import android.provider.OpenableColumns
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.FileProvider
import java.io.File
import java.math.BigDecimal

data class LoanTypeOption(
    val id: Int,
    val title: String,
    val description: String,
    val bunga: Double?,
    val biayaLainnya: Double?,
)

data class LoanSimulation(
    val pokokPerBulan: Long,
    val bungaPerBulan: Long,
    val biayaLainnya: Long,
    val totalPerBulan: Long,
    val totalPengembalian: Long,
)

data class DocumentUploadItem(
    val icon: ImageVector,
    val title: String,
    val fileUri: Uri? = null,
    val isRequired: Boolean,
    val fileName: String? = null,
)

private fun calculateSimulation(nominal: Int, tenor: Int, bungaPercent: Double, biayaLainnya: Long): LoanSimulation {
    if (tenor <= 0) return LoanSimulation(0, 0, 0, 0, 0)
    val pokok = nominal.toLong() / tenor    //total angsuran POKOK bulan
    val bunga = (nominal * (bungaPercent / 100)).toLong() //bunga perbulan
    val biayaLainnyaPerBulan = biayaLainnya / tenor //biaya lainnya dicicil merata tiap bulan
    val totalPerBulan = pokok + bunga + biayaLainnyaPerBulan //total angusran per bulan
    val totalPengembalian = totalPerBulan * tenor //total seluruh utang, biaya lainnya sudah termasuk di totalPerBulan
    return LoanSimulation(pokok, bunga, biayaLainnyaPerBulan, totalPerBulan, totalPengembalian)
}

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var fileName = "Dokumen Terunggah"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            fileName = cursor.getString(nameIndex)
        }
    }
    return fileName
}

fun createCaptureUri(context: Context): Uri {
    val directory = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File.createTempFile("capture_", ".jpg", directory)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}



@Composable
fun PengajuanPinjamanPage(
    modifier: Modifier = Modifier,
    viewModel: PinjamanViewModel = sharedActivityViewModel(),
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {},
) {
    val form by viewModel.formState.collectAsStateWithLifecycle()
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val loanTypes by viewModel.loanTypes.collectAsStateWithLifecycle()
    val isLoadingLoanTypes by viewModel.isLoadingLoanTypes.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val popupState by viewModel.popupState.collectAsStateWithLifecycle()
    val documents by viewModel.documents.collectAsStateWithLifecycle()

    //reset input kalau keluar halaman
    DisposableEffect(Unit) {
        onDispose { viewModel.resetForm() }
    }



    PengajuanPinjamanContent(
        modifier = modifier,
        form = form,
        currentStep = currentStep,
        steps = viewModel.steps,
        loanTypes = loanTypes,
        isLoadingLoanTypes = isLoadingLoanTypes,
        uiState = uiState,
        onUpdateForm = viewModel::updateForm,
        onGoToStep = viewModel::goToStep,
        onPreviousStep = viewModel::previousStep,
        onNextStep = viewModel::nextStep,
        onSubmit = viewModel::submitForm,
        onBackClick = onBackClick,
        documents = documents,
        onDocumentPicked = viewModel::updateDocumentUri,
    )

    when (val state = popupState) {
        is PopupState.Show -> {
            StatusPopup(
                isSuccess = state.isSuccess,
                message = state.message,
                onDismiss = {
                    viewModel.dismissPopup()
                    if (state.isSuccess) onSuccess()
                }
            )
        }
        PopupState.Idle -> Unit
    }
}

@Composable
fun PengajuanPinjamanContent(
    modifier: Modifier = Modifier,
    form: PinjamanFormState = PinjamanFormState(),
    currentStep: Int = 0,
    steps: List<String> = listOf("Tipe Pinjaman", "Simulasi", "Dokumen"),
    loanTypes: List<LoanTypeOption> = emptyList(),
    isLoadingLoanTypes: Boolean = false,
    uiState: PinjamanUiState = PinjamanUiState.Idle,
    onUpdateForm: (((PinjamanFormState) -> PinjamanFormState)) -> Unit = {},
    onGoToStep: (Int) -> Unit = {},
    onPreviousStep: () -> Unit = {},
    onNextStep: () -> Unit = {},
    onSubmit: () -> Unit = {},
    onBackClick: () -> Unit = {},
    documents: List<DocumentUploadItem> = emptyList(),
    onDocumentPicked: (index: Int, uri: Uri, fileName: String) -> Unit = { _, _, _ -> },
) {
    val isLoanTypeSelected = form.selectedLoanTypeId != 0
    val minNominal = 1_000
    val maxNominal = form.sisaPlafond ?: 0L
    val isNominalFilled = form.nominal > 0
    val isNominalTooLow = form.nominal in 1 until  minNominal
    val isNominalTooHigh = maxNominal > 0 && form.nominal > maxNominal
    val isTenorFilled = form.tenor.isNotBlank()
    val isDocumentUploaded = documents.filter { it.isRequired }.all {it.fileUri != null}



    val isDetailValid = isLoanTypeSelected
    val isSimulasiValid = isNominalFilled && !isNominalTooLow && !isNominalTooHigh && isTenorFilled

    val canProceed = when (currentStep) {
        0 -> isDetailValid
        1 -> isSimulasiValid
        else -> true
    }
    val canSubmit = isDetailValid && isSimulasiValid && isDocumentUploaded

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
            .verticalScroll(rememberScrollState())
    ) {
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
                text = "Pengajuan Pinjaman",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        StepIndicator(
            steps = steps,
            currentStep = currentStep,
            onStepClick = onGoToStep
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        Column {
            when (currentStep) {
                0 -> StepDetail(
                    form = form,
                    update = onUpdateForm,
                    loanTypes = loanTypes,
                    isLoadingLoanTypes = isLoadingLoanTypes
                )
                1 -> StepSimulasi(
                    form = form,
                    update = onUpdateForm,
                    loanTypes = loanTypes,
                )
                2 -> StepDokumen(
                    documents = documents,
                    onDocumentPicked = onDocumentPicked
                )
            }
        }

        if (uiState is PinjamanUiState.Error) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 15.dp, end = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                AppButton(
                    text = "Kembali",
                    variant = AppButtonVariant.Secondary,
                    onClick = onPreviousStep,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            if (currentStep < steps.lastIndex) {
                AppButton(
                    text = "Lanjut",
                    onClick = onNextStep,
                    enabled = canProceed,
                    modifier = Modifier.weight(1f)
                )
            } else {
                AppButton(
                    text = "Submit",
                    onClick = onSubmit,
                    enabled = canSubmit,
                    isLoading = uiState is PinjamanUiState.Loading,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
private fun StepDetail(
    form: PinjamanFormState,
    update: ((PinjamanFormState) -> PinjamanFormState) -> Unit,
    loanTypes: List<LoanTypeOption>,
    isLoadingLoanTypes: Boolean
) {
    Column (modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pilih Jenis Pinjaman",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (isLoadingLoanTypes) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            loanTypes.forEach { option ->
                val isSelected = form.selectedLoanTypeId == option.id
                LoanTypeCard(
                    option = option,
                    isSelected = isSelected,
                    onClick = { update {
                        it.copy(
                        selectedLoanTypeId = option.id,
                        selectedLoanTypeName = option.title) } }
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
        }
    }
}

@Composable
private fun StepSimulasi(
    form: PinjamanFormState,
    loanTypes: List<LoanTypeOption>,
    update: ((PinjamanFormState) -> PinjamanFormState) -> Unit,) {

    val nominal = form.nominal
    val selected = loanTypes.firstOrNull { it.id == form.selectedLoanTypeId }
    val tenor = Regex("\\d+").find(form.tenor)?.value?.toIntOrNull() ?: 0
    val bungaPercent = selected?.bunga ?: 0.0
    val biayaLainnya = (selected?.biayaLainnya?: 0).toLong()
    val simulation = calculateSimulation(nominal, tenor, bungaPercent, biayaLainnya)
    val bungaFormatted = "${bungaPercent}%"


    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        Text(
            text = "Simulasi Pinjaman",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        AppTextField(
            value = if (form.nominal == 0) "" else form.nominal.toString(),
            onValueChange = { v -> update { it.copy(nominal = v.toIntOrNull() ?: 0) } },
            label = "Nominal Pinjaman",
            leadingIcon = Icons.Filled.AttachMoney,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        val maxNominal = form.sisaPlafond ?: 0L
        val nominalHint = when {
            form.nominal <= 0 -> "Nominal pinjaman wajib diisi"
            form.nominal < 1_000 -> "Minimal pengajuan Rp 1.000"
            maxNominal > 0 && form.nominal > maxNominal -> "Melebihi sisa plafon (${formatRupiah(maxNominal)})"
            else -> null
        }
        if (nominalHint != null) {
            Text(
                text = nominalHint,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        AppDropdownField(
            value = form.tenor,
            onValueChange = { v -> update { it.copy(tenor = v) } },
            label = "Tenor Pinjaman",
            leadingIcon = Icons.Filled.DateRange,
            options = listOf("6 Bulan", "12 Bulan", "18 Bulan", "24 Bulan")
        )

        SimulationSection(title = "Ringkasan Pinjaman") {
            SimulationRow(label = "Jenis Pinjaman", value = form.selectedLoanTypeName ?: "-")
            SimulationRow(
                label = "Nominal Pengajuan",
                value = formatRupiah(nominal.toLong()),
                valueColor = ColorPrimary,
                valueSize = 20.sp
            )
            SimulationRow(
                label = "Tenor",
                value = form.tenor,
                icon = Icons.Filled.CalendarToday
            )
        }

        SimulationSection(title = "Rincian Angsuran (Estimasi)") {
            SimulationRow(
                label = "Angsuran Pokok / Bulan",
                value = formatRupiah(simulation.pokokPerBulan)
            )
            SimulationRow(
                label = "Bunga / Bulan (${bungaFormatted})",
                value = formatRupiah(simulation.bungaPerBulan)
            )
            SimulationRow(
                label = "Biaya Lainnya",
                value = formatRupiah(simulation.biayaLainnya)
            )
            HorizontalDivider(color = ColorOutline.copy(alpha = 0.3f))
            SimulationHighlightRow(
                label = "Total Angsuran / Bulan",
                value = formatRupiah(simulation.totalPerBulan)
            )
            SimulationRow(
                label = "Total Pengembalian",
                value = formatRupiah(simulation.totalPengembalian)
            )
        }
    }

}

@Composable
private fun StepDokumen(
    documents: List<DocumentUploadItem>,
    onDocumentPicked: (index: Int, uri: Uri, fileName: String) -> Unit,
) {
    val missingDocumentNames = documents.filter { it.isRequired && it.fileUri == null }.map { it.title }
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        Column {
            Text(
                text = "Unggah Dokumen",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pastikan dokumen terlihat jelas dan dapat dibaca.",
                fontSize = 13.sp,
                color = ColorOnSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        documents.forEachIndexed { index, doc ->
            val context = LocalContext.current
            var captureUri by remember { mutableStateOf<Uri?>(null) }

            //ambil gambar dari file HP -> Outputnya Uri -> ubah jadi multipart pake konteks -> Post BE
            val filePickerLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { uri -> uri?.let { onDocumentPicked(index, it, getFileNameFromUri(context, it)) } }

            //ambil gambar pake kamera -> output nya Uri -> ubah jadi multipart pake konteks -> Post BE
            val takePictureLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.TakePicture()
            ) { saved ->
                if (saved) captureUri?.let { onDocumentPicked(index, it, getFileNameFromUri(context, it)) }
            }

            val cameraPermissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                if (granted) {
                    val uri = createCaptureUri(context)
                    captureUri = uri
                    takePictureLauncher.launch(uri)
                }
            }

            DocumentUploadCard(
                item = doc,
                onPickFromCamera = {
                    val granted = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (granted) {
                        val uri = createCaptureUri(context)
                        captureUri = uri
                        takePictureLauncher.launch(uri)
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                onPickFromFile = { filePickerLauncher.launch("*/*") },
            )
        }

        if (missingDocumentNames.isNotEmpty()){
            Text(
                text = "Dokumen wajib belum diupload: ${missingDocumentNames.joinToString(", ")}",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewPengajuanPinjamanContent() {
    OkariruTheme {
        val dummyLoanTypes = listOf(
            LoanTypeOption(1, "Pinjaman Kilat", "SUper cepat", 1.0, 10_000.00),
            LoanTypeOption(2, "Pinjaman Modal", "pinjam Modal", 0.5, 10_000.00)
        )
        PengajuanPinjamanContent(
            loanTypes = dummyLoanTypes,
            form = PinjamanFormState(selectedLoanTypeId = 1)
        )
    }
}

@Composable
private fun LoanTypeCard(
    option: LoanTypeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ColorPrimary else ColorOutline,
                shape = RoundedCornerShape(Radius.lg)
            ),
        shape = RoundedCornerShape(Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ColorPrimaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ColorPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = null,
                    tint = ColorPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = option.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) ColorPrimary else Color.Transparent)
                            .border(1.dp, if (isSelected) ColorPrimary else ColorOutline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Dipilih",
                                tint = ColorOnPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                Text(
                    text = option.description,
                    fontSize = 13.sp,
                    color = ColorOnSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                    LoanStatChip(
                        icon = Icons.Filled.Percent,
                        label = option.bunga?.let { "${it}%/bln" } ?: "-"
                    )
                    LoanStatChip(
                        icon = Icons.Filled.Receipt,
                        label = option.biayaLainnya?.let { formatRupiah(it.toLong()) } ?: "-"
                    )
                }
            }
        }
    }
}

@Composable
private fun LoanStatChip(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.pill))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ColorPrimaryContainer, RoundedCornerShape(Radius.pill))
            .padding(horizontal = Spacing.sm, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = ColorPrimary, modifier = Modifier.size(12.dp))
        Text(text = label, fontSize = 12.sp, color = ColorPrimary)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStepDetail() {
    OkariruTheme {
        var formState by remember { mutableStateOf(PinjamanFormState()) }
        val dummyLoanTypes = listOf(
            LoanTypeOption(1, "Pinjaman Kilat", "SUper cepat", 1.0, 10_000.00),
            LoanTypeOption(2, "Pinjaman Modal", "pinjam Modal", 0.5, 10_000.00)
        )
        StepDetail(
            form = formState,
            update = { updateBlock -> formState = updateBlock(formState) },
            loanTypes = dummyLoanTypes,
            isLoadingLoanTypes = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StepSimulasiPreview() {
    val dummyLoanTypes = listOf(
        LoanTypeOption(
            id = 1,
            title = "Pinjaman Kilat",
            description = "Super cepat",
            bunga = 1.0,
            biayaLainnya = 10000.0
        )
    )
    var formState by remember {
        mutableStateOf(
            PinjamanFormState(
                selectedLoanTypeId = 1,
                selectedLoanTypeName = "Pinjaman Kilat",
                nominal = 5000000,
                tenor = "12 Bulan"
            )
        )
    }
    OkariruTheme {
        StepSimulasi(
            form = formState,
            update = { updateBlock -> formState = updateBlock(formState) },
            loanTypes = dummyLoanTypes
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StepDokumenPreview() {
    OkariruTheme {
        StepDokumen(
            documents = listOf(
                DocumentUploadItem(Icons.Filled.Badge, "KTP", isRequired = true),
                DocumentUploadItem(Icons.Filled.Payments, "Slip Gaji", isRequired = true),
            ),
            onDocumentPicked = { _, _, _ -> }
        )
    }
}