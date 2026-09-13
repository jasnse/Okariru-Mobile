package com.project.binar.okariru.presentation.reset

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.R
import com.project.binar.okariru.presentation.shared.component.AppTextField
import com.project.binar.okariru.ui.theme.OkariruTheme

@Composable
fun newPasswordPage(
    resetToken: String,
    onPasswordChangeds: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.passwordChanged) {
        if (uiState.passwordChanged) {
            // panggil onPasswordChangeds -> proses back to login di navigation
            onPasswordChangeds()
            viewModel.consumePasswordChangedEvent()
        }
    }


    newPasswordContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onChangePassword = {
            password -> viewModel.changePassword(password, resetToken)
        }

    )
}

@Composable
fun newPasswordContent(
    uiState: ForgotPasswordUiState = ForgotPasswordUiState(),
    onBackClick: () -> Unit = {},
    onChangePassword: (String) -> Unit = {},
){
    var password by remember { mutableStateOf("") }
    var confirPassword by remember { mutableStateOf("") }

    val isPasswordValid = password.isNotBlank() &&
            confirPassword.isNotBlank() &&
            password.trim() == confirPassword.trim()

    Surface(
        modifier = Modifier.fillMaxSize().imePadding(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())

        ) {
            //form OTP
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = Color(0xFFF2F2F2), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.forgot_password_rafiki),
                            contentDescription = null,
                            modifier = Modifier.size(212.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Judul & Subtitle
                Text(
                    text = "Masukan Password Baru",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Masukkan Password baru untuk ganti password",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // text field
                AppTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "New Password",
                    leadingIcon = Icons.Filled.Password,
                            isPassword = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // text field
                AppTextField(
                    value = confirPassword,
                    onValueChange = { confirPassword = it },
                    label = "Confirm New Password",
                    leadingIcon = Icons.Filled.CheckCircle,
                            isPassword = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (confirPassword.isNotEmpty() && password != confirPassword) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Password tidak sama",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onChangePassword(password) },
                    enabled = !uiState.isSubmitting && isPasswordValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.LightGray
                    )
                ) { if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Ganti Password",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }}
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewPasswordContentPreview() {
    OkariruTheme {
        newPasswordContent(
            uiState = ForgotPasswordUiState()
        )
    }
}