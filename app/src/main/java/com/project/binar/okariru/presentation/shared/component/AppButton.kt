package com.project.binar.okariru.presentation.shared.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.binar.okariru.ui.theme.ColorBackground
import com.project.binar.okariru.ui.theme.ColorOnBackground
import com.project.binar.okariru.ui.theme.OkariruTheme


enum class AppButtonVariant {
    Primary,
    Secondary  
}

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    height: Dp = 52.dp,
    variant: AppButtonVariant = AppButtonVariant.Primary
) {
    val containerColor = if (variant == AppButtonVariant.Primary) {
        MaterialTheme.colorScheme.primary // warna background merah
    } else {
        ColorBackground // warna background putih
    }

    val contentColor = if (variant == AppButtonVariant.Primary) {
        MaterialTheme.colorScheme.onPrimary //warna text putih
    } else {
        ColorOnBackground //  warna teks grey_900 / gelap
    }

    val borderStroke = if (variant == AppButtonVariant.Secondary) {
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary) // Border garis merah
    } else {
        null
    }

    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),

        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = borderStroke,

    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun AppButtonPreviewP(){
    OkariruTheme {
        AppButton(
            text = "Primary",
            variant = AppButtonVariant.Primary,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AppButtonPreviewS(){
    OkariruTheme {
        AppButton(
            text = "Secondary",
            variant = AppButtonVariant.Secondary,
            onClick = {}
        )
    }
}