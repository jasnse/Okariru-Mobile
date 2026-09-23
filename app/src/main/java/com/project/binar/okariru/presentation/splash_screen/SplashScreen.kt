package com.project.binar.okariru.presentation.splash_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.binar.okariru.R
import com.project.binar.okariru.ui.theme.OkariruTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashContent(
    onSplashFinished: () -> Unit = {} // Callback jika animasi selesai dan siap pindah layar
) {
    var showSubtitle by remember { mutableStateOf(false) }

    // 1. Controller Animasi untuk Scale (Zoom In) dan Alpha (Opacity)
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run animasi scale & alpha secara bersamaan untuk Logo/Judul
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800)
            )
        }

        // 2. Delay singkat sebelum menampilkan Subtitle
        delay(600)
        showSubtitle = true

        // 3. Tahan sejenak lalu trigger callback selesainya splash
        delay(1800)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scale.value) // Menerapkan animasi scale
                .alpha(alpha.value) // Menerapkan animasi transparansi
        ) {
            // Un-comment jika ikon/logo sudah siap:
            Image(
                painter = painterResource(id = R.drawable.logo_aja_putih_mobile), // Ganti dengan R.drawable.logo_putih_mobile
                contentDescription = "Logo Okariru",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Okariru",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Subtitle muncul dengan animasi Fade-In bertahap
            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(animationSpec = tween(durationMillis = 800))
            ) {
                Text(
                    text = "Solusi Pinjaman Terpercaya",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashContentPreview() {
    // Sesuaikan nama Theme project Anda jika ada (misal: OkariruTheme)
    OkariruTheme {
        Surface {
            SplashContent()
        }
    }
}