package com.project.binar.okariru.presentation.shared.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateTextField(
    modifier: Modifier = Modifier,
    value: String, // Format "yyyy-MM-dd"
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector = Icons.Filled.CalendarToday
) {
    // State untuk mengatur apakah DatePickerDialog sedang muncul atau tidak
    var showDialog by remember { mutableStateOf(false) }

    // Konversi nilai string saat ini keMillis jika ada isinya untuk inisialisasi DatePicker
    val initialSelectedDateMillis_conv = remember(value) {
        try {
            if (value.isNotBlank()) {
                val localDate = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } else {
                System.currentTimeMillis()
            }
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    val maxDateMillis = remember {
        LocalDate.now()
            .atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    }

    //saat ui ke reset -> pilihan tanggal gak ikut ke reset juga
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis_conv,

        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= maxDateMillis
            }

            override fun isSelectableYear(utcTimeYear: Int): Boolean {
                return utcTimeYear <= LocalDate.now().year
            }
        }
    )

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {}, // agar user tidak bisa mengetik manual secara bebas
            readOnly = true,    // keyboard tidak muncul
            label = { Text(label) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary,

                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            enabled = false, // Dinonaktifkan interaksi ketiknya, diganti dengan aksi klik di Box
            modifier = Modifier.fillMaxWidth()
        )

        // Lapisan transparan di atas TextField untuk mendeteksi klik
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDialog = true }
        )
    }

// tampilan pop up date picker
    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            // Ubah milidetik dari DatePicker menjadi format String "yyyy-MM-dd"
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            onValueChange(selectedDate.format(formatter))
                        }
                    }
                ) {
                    Text("OK")
                }
            },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal")
                    }
                }
        ) {
            //set selected date (state remember)
            DatePicker(state = datePickerState)
        }
    }
}