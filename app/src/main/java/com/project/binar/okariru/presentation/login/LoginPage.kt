package com.project.binar.okariru.presentation.login

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.data.auth.repository.AuthViewModel
import com.project.binar.okariru.presentation.shared.component.AppButton
import com.project.binar.okariru.presentation.shared.component.AppButtonVariant
import com.project.binar.okariru.presentation.shared.component.AppTextField
import com.project.binar.okariru.presentation.shared.component.StatusPopup
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.project.binar.okariru.ui.theme.ColorOnSurface
import com.project.binar.okariru.ui.theme.OkariruTheme

@Composable
fun LoginPage(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = sharedActivityViewModel(),
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onResetPassword: () -> Unit = {}
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val popupState by viewModel.popupState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        // Header merah dengan lambang aplikasi
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.38f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_putih_mobile),
                    contentDescription = null,
                    modifier = Modifier.size(169.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary) // Opsional jika ingin mewarnai ulang gambar
                )
            }

//            Text(
//                text = "Okariru",
//                fontSize = 26.sp,
//                fontWeight = FontWeight.Bold,
//                color = MaterialTheme.colorScheme.onPrimary
//            )

        }

        // Form Login
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.62f),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                }
                AppTextField(
                    modifier = Modifier.testTag("login_username_field"),
                    value = username,
                    onValueChange = { username = it },
                    label = "Username",
                    leadingIcon = Icons.Filled.Person
                )

                Spacer(modifier = Modifier.height(16.dp))


                AppTextField(
                    modifier = Modifier.testTag("login_password_field"),
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .clickable(
                                onClick = onResetPassword
                            ),
                        text = "Lupa Password",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(18.dp))

                AppButton(
                    text = "Sign In",
                    variant = AppButtonVariant.Primary,
                    onClick = { viewModel.login(username, password) },
                    isLoading = uiState.isSubmitting
                )

                when (val state = popupState) {
                    is AuthViewModel.PopupState.Show -> {
                        StatusPopup(
                            isSuccess = state.isSuccess,
                            message = state.message,
                            onDismiss = {
                                viewModel.dismissPopup()
                                if (state.isSuccess) onLoginSuccess()
                            }
                        )
                    }
                    AuthViewModel.PopupState.Idle -> {
                        // Tidak melakukan apa-apa jika state idle
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray, // Atau gunakan theme color
                        thickness = 1.dp
                    )

                    Text(
                        text = "ATAU",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )

                    Divider(
                        modifier = Modifier.weight(1f),
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }


                AppButton(
                    text = "Register",
                    variant = AppButtonVariant.Secondary,
                    onClick = onRegisterClick
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPagePreview() {
    // Ganti dengan nama AppTheme proyek Anda jika ada
    OkariruTheme {
        LoginPage(
            onLoginSuccess = {},
            onRegisterClick = {}
        )
    }
}