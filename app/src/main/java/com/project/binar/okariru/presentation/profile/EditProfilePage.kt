package com.project.binar.okariru.presentation.profile

import android.provider.SyncStateContract.Helpers.update
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.data.auth.dto.CustomerDTO
import com.project.binar.okariru.data.auth.repository.AuthViewModel
import com.project.binar.okariru.presentation.shared.component.AppDateTextField
import com.project.binar.okariru.presentation.shared.component.AppDropdownField
import com.project.binar.okariru.presentation.shared.component.AppTextField
import com.project.binar.okariru.presentation.shared.component.StatusPopup
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.ui.theme.ColorOnSurfaceVariant
import com.project.binar.okariru.ui.theme.ColorPrimary
import com.project.binar.okariru.ui.theme.ColorPrimaryContainer
import com.project.binar.okariru.ui.theme.ColorSurfaceVariant
import com.project.binar.okariru.ui.theme.OkariruTheme
import com.project.binar.okariru.ui.theme.Radius
import com.project.binar.okariru.ui.theme.Spacing

@Composable
fun editProfilePage(
    onBackClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
    onProfileUpdated: () -> Unit = {},
    authViewModel: AuthViewModel = sharedActivityViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val popupState by viewModel.popupState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.profileUpdated) {
        if (uiState.profileUpdated) {
            viewModel.consumeProfileUpdatedEvent()
        }
    }

    EditProfileContent(
        uiState = uiState,
        onBackClick = onBackClick,
        //pass ke viewmodel.updateProfile
        onSave = { sidName, alamat, pekerjaan, pendapatan, maritalStatus, noRekening, tempatLahir, tanggalLahir, gender ->
            viewModel.updateProfile(
                sidName, alamat, pekerjaan, pendapatan,
                maritalStatus, noRekening, tempatLahir, tanggalLahir, gender)
        }
    )

    when (val state = popupState) {
        is ProfileViewModel.PopupState.Show -> {
            StatusPopup(
                isSuccess = state.isSuccess,
                message = state.message,
                onDismiss = {
                    viewModel.dismissPopup()
                    if (state.isSuccess) onProfileUpdated()
                }
            )
        }
        ProfileViewModel.PopupState.Idle -> {
            // Tidak melakukan apa-apa jika state idle
        }
    }
}

@Composable
fun EditProfileContent(
    uiState: profileUiState,
    onBackClick: () -> Unit = {},
    onSave: (
        sidName: String, alamat: String, pekerjaan: String, pendapatan: String,
        maritalStatus: String, noRekening: String, tempatLahir: String, tanggalLahir: String, gender: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
) {
    val customer = uiState.customer
    var sidName by remember(customer) { mutableStateOf(customer?.sidName ?: "") }
    var gender by remember(customer) { mutableStateOf(customer?.gender ?: "") }
    var tempatLahir by remember(customer) { mutableStateOf(customer?.tempatLahir ?: "") }
    var tanggalLahir by remember(customer) { mutableStateOf(customer?.tanggalLahir ?: "") }
    var pekerjaan by remember(customer) { mutableStateOf(customer?.pekerjaan ?: "") }
    var pendapatan by remember(customer) { mutableStateOf(customer?.pendapatan?.toString() ?: "") }
    var maritalStatus by remember(customer) { mutableStateOf(customer?.maritalStatus ?: "") }
    var alamat by remember(customer) { mutableStateOf(customer?.alamat ?: "") }
    var noRekening by remember(customer) { mutableStateOf(customer?.noRekening ?: "") }

    Surface(
        modifier = Modifier.fillMaxSize().imePadding(),
        color = ColorSurfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.xl, vertical = Spacing.lg)
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
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
                    text = "Edit Profil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorPrimary)
                }
            } else {
                //component section caRd
                SectionCard(title = "Data Diri", icon = Icons.Filled.Badge) {
                    AppTextField(
                        value = sidName,
                        onValueChange = { sidName = it },
                        label = "Nama Tampilan",
                        leadingIcon = Icons.Filled.Badge
                    )
                    FieldSpacer()
                    AppDropdownField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = "Gender",
                        leadingIcon = Icons.Filled.Wc,
                        options = listOf("Laki-laki", "Perempuan")
                    )
                    FieldSpacer()
                    AppTextField(
                        value = tempatLahir,
                        onValueChange = { tempatLahir = it },
                        label = "Tempat Lahir",
                        leadingIcon = Icons.Filled.Cake
                    )
                    FieldSpacer()
                    AppDateTextField(
                        value = tanggalLahir,
                        onValueChange = { tanggalLahir = it },
                        label = "Tanggal Lahir (yyyy-MM-dd)",
                        leadingIcon = Icons.Filled.CalendarToday
                    )
//                    AppTextField(
//                        value = tanggalLahir,
//                        onValueChange = { tanggalLahir = it },
//                        label = "Tanggal Lahir (YYYY-MM-DD)",
//                        leadingIcon = Icons.Filled.Cake
//                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                SectionCard(title = "Pekerjaan & Status", icon = Icons.Filled.Work) {
                    AppTextField(
                        value = pekerjaan,
                        onValueChange = { pekerjaan = it },
                        label = "Pekerjaan",
                        leadingIcon = Icons.Filled.Work
                    )
                    FieldSpacer()
                    AppTextField(
                        value = pendapatan,
                        onValueChange = { pendapatan = it },
                        label = "Pendapatan",
                        leadingIcon = Icons.Filled.AttachMoney,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    FieldSpacer()
//                    AppTextField(
//                        value = maritalStatus,
//                        onValueChange = { maritalStatus = it },
//                        label = "Status Pernikahan",
//                        leadingIcon = Icons.Filled.Groups
//                    )
                    AppDropdownField(
                        value = maritalStatus,
                        onValueChange = { maritalStatus = it },
                        label = "Marital Status",
                        leadingIcon = Icons.Filled.Groups,
                        options = listOf("Belum Kawin", "Kawin", "Cerai")
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))

                SectionCard(title = "Alamat & Rekening", icon = Icons.Filled.Home) {
                    AppTextField(
                        value = alamat,
                        onValueChange = { alamat = it },
                        label = "Alamat",
                        leadingIcon = Icons.Filled.Home
                    )
                    FieldSpacer()
                    AppTextField(
                        value = noRekening,
                        onValueChange = { noRekening = it },
                        label = "No Rekening",
                        leadingIcon = Icons.Filled.AccountBalance,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
                Button(
                    onClick = {
                        onSave(sidName, alamat, pekerjaan, pendapatan, maritalStatus, noRekening, tempatLahir, tanggalLahir, gender)
                    },
                    enabled = !uiState.isSubmitting,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text("Simpan Perubahan")
                    }
                }
                
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
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
                        .size(32.dp)
                        .clip(RoundedCornerShape(Radius.sm))
                        .background(ColorPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = ColorPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorOnSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            content()
        }
    }
}

@Composable
private fun FieldSpacer() {
    Spacer(modifier = Modifier.height(Spacing.md))
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
fun editProfilePreview() {
    OkariruTheme {
        EditProfileContent(
            uiState = profileUiState(
                isLoading = false,
                customer = CustomerDTO(
                    customerId = 1,
                    userName = "budi123",
                    sidName = "Budi Santoso",
                    email = "budi.santoso@email.com",
                    nik = "1234567890123456",
                    tempatLahir = "Jakarta",
                    tanggalLahir = "1995-05-10",
                    alamat = "Jl. Merdeka No. 1",
                    pekerjaan = "Karyawan Swasta",
                    pendapatan = 5000000,
                    maritalStatus = "Belum Menikah",
                    gender = "Laki-laki",
                    noRekening = "1234567890",
                    createdAt = "2024-01-01",
                    roleCustomer = "CUSTOMER"
                )
            ),
            onBackClick = {}
        )
    }
}
