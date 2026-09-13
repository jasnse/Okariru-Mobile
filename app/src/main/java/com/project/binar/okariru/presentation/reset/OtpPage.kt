package com.project.binar.okariru.presentation.reset

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun otpPage(
    emailOtp: String,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onOtpVerified: (resetToken: String, email: String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
//    onVerifyClick: (String) -> Unit = {},
//    onResendOtpClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isTimerRunning by remember { mutableStateOf(true) }
    var timerSeconds by remember { mutableIntStateOf(55) }


    LaunchedEffect(key1 = isTimerRunning, key2 = timerSeconds) {
        if (isTimerRunning && timerSeconds > 0) {
            delay(1000L.milliseconds)
            timerSeconds--
        } else if (timerSeconds == 0) {
            isTimerRunning = false
            timerSeconds = 60
        }
    }

    LaunchedEffect(uiState.resetToken) {
        uiState.resetToken?.let {
            onOtpVerified(it, emailOtp)
            viewModel.consumeResetTokenEvent()
        }
    }


    OtpContent(
        uiState = uiState,
        isTimerRunning = isTimerRunning,
        timerSeconds = timerSeconds,
        onResendClick = {
            isTimerRunning = true
            viewModel.sendOtp(emailOtp) },
        onVerifyClick = {
                otp -> viewModel.validateOtp(emailOtp, otp = otp)
        },
        onBackClick = onBackClick
    )

}

@Composable
fun OtpContent(
    uiState: ForgotPasswordUiState = ForgotPasswordUiState(),
    isTimerRunning: Boolean = false,
    timerSeconds: Int = 60,
    onBackClick: () -> Unit = {},
    onResendClick: () -> Unit = {},
    onVerifyClick: (String) -> Unit = {},
){
    //variable
    var otpCode by remember { mutableStateOf("") }
    val otpLength = 5
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                //Image Illustration
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

                Spacer(modifier = Modifier.height(52.dp))

                Text(
                    text = "Enter Your OTP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    contentAlignment = Alignment.Center
                ){
                    BasicTextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .size(19.dp),
                        value = otpCode,
                        onValueChange = {if (it.length <= otpLength ) otpCode = it},
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        cursorBrush = SolidColor(Color.Transparent)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        for (i in 0 until 5) {
                            val char = otpCode.getOrNull(i)?.toString() ?: ""
                            val inputBgColor = null
                            Box(
                                modifier = Modifier
                                    .size(53.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Gray)
                                    .clickable {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                               },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (char.isNotEmpty()) char else "—",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (char.isNotEmpty()) Color.White else Color.White
                                )
                            }
                            if (i < 4) {
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isTimerRunning) "Resend code in ${timerSeconds}s" else "Resend code",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isTimerRunning) {
                            //call api
                            onResendClick()
                        }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Pesan Error
                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = { onVerifyClick(otpCode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        text = "Verify",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OtpPagePReview() {
    OtpContent(
        uiState = ForgotPasswordUiState(),
        isTimerRunning = true,
        timerSeconds = 45
    )
}
