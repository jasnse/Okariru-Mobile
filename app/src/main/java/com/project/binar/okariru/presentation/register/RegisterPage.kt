package com.project.binar.okariru.presentation.register


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.data.auth.repository.AuthViewModel
import com.project.binar.okariru.presentation.shared.component.AppButton
import com.project.binar.okariru.presentation.shared.component.AppButtonVariant
import com.project.binar.okariru.presentation.shared.component.AppDateTextField
import com.project.binar.okariru.presentation.shared.component.AppDropdownField
import com.project.binar.okariru.presentation.shared.component.AppTextField
import com.project.binar.okariru.presentation.shared.component.StatusPopup
import com.project.binar.okariru.ui.theme.OkariruTheme


@Composable
fun RegisterPage(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val form by viewModel.formState.collectAsState()
    val currentStep by viewModel.currentStep.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val popupState by viewModel.popupStates.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp)
    ) {
        Text(
            text = "Daftar Akun",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Indikator step (tab bar) — aktif merah + garis bawah merah, non-aktif hitam/abu
        TabRow(
            selectedTabIndex = currentStep,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            viewModel.steps.forEachIndexed { index, namaStep ->
                val isSelected = currentStep == index
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.goToStep(index) },
                    text = {
                        Text(
                            text = namaStep,
                            fontSize = 12.sp,
                            maxLines = 1,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Column {
            when (currentStep) {
                0 -> StepAkun(form, viewModel::updateForm)
                1 -> StepPersonal(form, viewModel::updateForm)
                2 -> StepFinancial(form, viewModel::updateForm)
            }
        }

        if (uiState is RegisterUiState.Error) {
            Text(
                text = (uiState as RegisterUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (uiState is RegisterUiState.Success) {
            Text(
                text = "Registrasi berhasil! Silakan login.",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            LaunchedEffectOnSuccess(onRegisterSuccess)
        }

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp, start = 15.dp, end = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                AppButton(
                    text = "Kembali",
                    variant = AppButtonVariant.Secondary,
                    onClick = { viewModel.previousStep() },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            if (currentStep < viewModel.steps.lastIndex) {
                AppButton(
                    text = "Lanjut",
                    onClick = { viewModel.nextStep() },
                    modifier = Modifier.weight(1f)
                )
            } else {
                AppButton(
                    text = "Daftar",
                    onClick = { viewModel.register() },
                    isLoading = uiState is RegisterUiState.Loading,
                    modifier = Modifier.weight(1f)
                )
            }

            when (val state = popupState) {
                is PopupState.Show -> {
                    StatusPopup(
                        isSuccess = state.isSuccess,
                        message = state.message,
                        onDismiss = {
                            viewModel.dismissPopup()
                            if (state.isSuccess) onBackToLogin()
                        }
                    )
                }
                PopupState.Idle -> {
                    // Tidak melakukan apa-apa jika state idle
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sudah punya akun? Masuk", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun LaunchedEffectOnSuccess(onSuccess: () -> Unit) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        onSuccess()
    }
}

@Composable
private fun StepAkun(
    form: RegisterFormState,
    update: ((RegisterFormState) -> RegisterFormState) -> Unit
) {
    Column {
        AppTextField(
            value = form.userName,
            onValueChange = { v -> update { it.copy(userName = v) } },
            label = "Username",
            leadingIcon = Icons.Filled.Person
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.email,
            onValueChange = { v -> update { it.copy(email = v) } },
            label = "Email",
            leadingIcon = Icons.Filled.Email
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.password,
            onValueChange = { v -> update { it.copy(password = v) } },
            label = "Password",
            leadingIcon = Icons.Filled.Lock,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.confirmPassword,
            onValueChange = { v -> update { it.copy(confirmPassword = v) } },
            label = "Confirm Password",
            leadingIcon = Icons.Filled.LockOpen,
            isPassword = true
        )
    }
}

@Composable
private fun StepPersonal(
    form: RegisterFormState,
    update: ((RegisterFormState) -> RegisterFormState) -> Unit
) {
    Column {
        AppTextField(
            value = form.sidName,
            onValueChange = { v -> update { it.copy(sidName = v) } },
            label = "SID Name",
            leadingIcon = Icons.Filled.Badge
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.nik,
            onValueChange = { v -> update { it.copy(nik = v) } },
            label = "NIK",
            leadingIcon = Icons.Filled.CreditCard,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.tempatLahir,
            onValueChange = { v -> update { it.copy(tempatLahir = v) } },
            label = "Tempat Lahir",
            leadingIcon = Icons.Filled.Place
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppDateTextField(
            value = form.tanggalLahir,
            onValueChange = { v -> update { it.copy(tanggalLahir = v) } },
            label = "Tanggal Lahir (yyyy-MM-dd)",
            leadingIcon = Icons.Filled.CalendarToday
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppDropdownField(
            value = form.gender,
            onValueChange = { v -> update { it.copy(gender = v) } },
            label = "Gender",
            leadingIcon = Icons.Filled.Wc,
            options = listOf("Laki-laki", "Perempuan")
        )
    }
}

@Composable
private fun StepFinancial(
    form: RegisterFormState,
    update: ((RegisterFormState) -> RegisterFormState) -> Unit
) {
    Column {
        AppTextField(
            value = form.alamat,
            onValueChange = { v -> update { it.copy(alamat = v) } },
            label = "Alamat",
            leadingIcon = Icons.Filled.Home
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.pekerjaan,
            onValueChange = { v -> update { it.copy(pekerjaan = v) } },
            label = "Pekerjaan",
            leadingIcon = Icons.Filled.Work
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.pendapatan,
            onValueChange = { v -> update { it.copy(pendapatan = v) } },
            label = "Pendapatan",
            leadingIcon = Icons.Filled.AttachMoney,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppDropdownField(
            value = form.maritalStatus,
            onValueChange = { v -> update { it.copy(maritalStatus = v) } },
            label = "Status Kawin",
            leadingIcon = Icons.Filled.Favorite,
            options = listOf("Belum Kawin", "Kawin", "Cerai")
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppTextField(
            value = form.noRekening,
            onValueChange = { v -> update { it.copy(noRekening = v) } },
            label = "No. Rekening",
            leadingIcon = Icons.Filled.AccountBalance,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterPage() {
    OkariruTheme {
        RegisterPage()
    }
}

@Preview(showBackground = true)
@Composable

fun PreviewStepPersonal() {
    OkariruTheme {
        var formState by remember { mutableStateOf(RegisterFormState()) }
        StepPersonal(
            form = formState,
            update = {}
        )
    }
}