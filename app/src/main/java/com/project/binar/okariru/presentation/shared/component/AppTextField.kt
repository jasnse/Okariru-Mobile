package com.project.binar.okariru.presentation.shared.component


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

fun formatNumberWithComma(input: String): String {
    // Hapus semua karakter selain angka agar tidak error
    val cleanString = input.replace(Regex("[^\\d]"), "")
    if (cleanString.isEmpty()) return ""

    // Ubah ke Long lalu format menggunakan Locale US (menggunakan koma) atau Indonesia (titik)
    val parsed = cleanString.toLongOrNull() ?: 0L
    return NumberFormat.getNumberInstance(Locale.US).format(parsed)
}

@Composable
fun AppCurrencyTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit, // Ini akan mengembalikan nilai angka murni (tanpa koma) ke ViewModel
    label: String,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value, // Nilai yang sudah berformat dari luar
        onValueChange = { input ->
            // 1. Bersihkan string dari koma/karakter non-digit
            val cleanValue = input.replace(Regex("[^\\d]"), "")
            // 2. Kembalikan nilai murni ke ViewModel/State
            onValueChange(cleanValue)
        },
        label = { Text(label) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null) },
        // Set keyboard menjadi angka agar nyaman saat input nominal
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

