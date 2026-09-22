@file:OptIn(ExperimentalTestApi::class)

package com.project.binar.okariru.auth

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.project.binar.okariru.MainActivity
// ClearAuthSessionRule sudah satu package (com.project.binar.okariru.auth), tidak perlu di-import
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith


private const val STEP_DELAY_MILLIS = 1_000L
private const val NETWORK_TIMEOUT_MILLIS = 15_000L

private typealias RegisterComposeRule =
        AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

private fun pause() = Thread.sleep(STEP_DELAY_MILLIS)

private fun RegisterComposeRule.typeInto(tag: String, text: String) {
    onNodeWithTag(tag).performTextInput(text)
}

private fun RegisterComposeRule.awaitText(text: String, substring: Boolean = false) {
    waitUntilAtLeastOneExists(hasText(text, substring = substring), NETWORK_TIMEOUT_MILLIS)
}

// buka dropdown lewat label yang lagi tampil, lalu pilih salah satu opsinya
private fun RegisterComposeRule.selectDropdown(label: String, option: String) {
    onNodeWithText(label).performClick()
    onNodeWithText(option).performClick()
}

@RunWith(Enclosed::class)
class RegisterScreenUiTest {

    @RunWith(AndroidJUnit4::class)
    class Register {
        @get:Rule(order = 0)
        val permissionRule: GrantPermissionRule =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS)
            else
                GrantPermissionRule.grant()

        @get:Rule(order = 1)
        val clearAuthSessionRule = ClearAuthSessionRule()

        @get:Rule(order = 2)
        val composeRule = createAndroidComposeRule<MainActivity>()

        @Test
        fun completeRegistrationFlowShowsSuccessPopup() {
            // data unik tiap run, biar gak nabrak "sudah terdaftar" pas dijalanin berkali-kali
            val unique = System.currentTimeMillis().toString().takeLast(8)

            // Landing -> Login
            composeRule.awaitText("Masuk")
            pause()
            composeRule.onNodeWithText("Masuk").performClick()

            // Login -> Register
            composeRule.awaitText("Register")
            pause()
            composeRule.onNodeWithText("Register").performClick()

            // Step 1: Akun
            composeRule.awaitText("Daftar Akun")
            pause()
            composeRule.typeInto("register_username_field", "qauser$unique")
            composeRule.typeInto("register_email_field", "qauser$unique@example.test")
            composeRule.typeInto("register_password_field", "password123")
            composeRule.typeInto("register_confirm_password_field", "password123")
            composeRule.onNodeWithText("Lanjut").performClick()
            pause()

            // Step 2: Personal
            composeRule.typeInto("register_sidname_field", "QA User $unique")
            composeRule.typeInto("register_nik_field", "16".padEnd(16, unique.last()))
            composeRule.typeInto("register_tempat_lahir_field", "Jakarta")
            // tanggal lahir: buka date picker, langsung konfirmasi tanggal default
            composeRule.onNodeWithText("Tanggal Lahir (yyyy-MM-dd)").performClick()
            composeRule.awaitText("OK")
            composeRule.onNodeWithText("OK").performClick()
            composeRule.selectDropdown("Gender", "Laki-laki")
            composeRule.onNodeWithText("Lanjut").performClick()
            pause()

            // Step 3: Financial
            composeRule.typeInto("register_alamat_field", "Jl. Merdeka No. $unique")
            composeRule.typeInto("register_pekerjaan_field", "QA Tester")
            composeRule.typeInto("register_pendapatan_field", "5000000")
            composeRule.selectDropdown("Status Kawin", "Belum Kawin")
            composeRule.typeInto("register_no_rekening_field", unique.padStart(10, '0'))
            pause()

            composeRule.onNodeWithText("Daftar")
                .performScrollTo()
                .performClick()

            composeRule.waitUntilAtLeastOneExists(
                hasText("Registrasi berhasil", substring = true),
                NETWORK_TIMEOUT_MILLIS,
            )
            pause()
        }
    }
}