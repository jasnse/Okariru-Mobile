@file:OptIn(ExperimentalTestApi::class)

package com.project.binar.okariru.auth

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.project.binar.okariru.MainActivity
import com.project.binar.okariru.data.auth.local.AuthSessionLocalDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.rules.ExternalResource
import org.junit.runner.RunWith
import kotlin.getValue

private const val STEP_DELAY_MILLIS = 2_000L
private const val NETWORK_TIMEOUT_MILLIS = 15_000L
private const val UNKNOWN_EMAIL = "unknown.user@masesas.test"
private const val WRONG_PASSWORD = "wrong-password"
private const val DEBUG_USERNAME = "Jason123"
private const val DEBUG_PASSWORD = "54321"

private const val USERNAME_FIELD_TAG = "login_username_field"
private const val PASSWORD_FIELD_TAG = "login_password_field"

class ClearAuthSessionRule : ExternalResource() {

    private val localDataSource by lazy {
        AuthSessionLocalDataSource(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    override fun before() = runBlocking { localDataSource.clear() }

    override fun after() = runBlocking { localDataSource.clear() }
}

private typealias HomeComposeRule =
        AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

private fun pause() = Thread.sleep(STEP_DELAY_MILLIS)

private fun inputOf(tag: String): SemanticsMatcher = hasSetTextAction() and hasAnyAncestor(hasTestTag(tag))

private fun HomeComposeRule.string(resId: Int): String = activity.getString(resId)

private fun HomeComposeRule.tapLoginButton() {
    onNodeWithText("Sign In").performClick()
}

private fun HomeComposeRule.fillLoginForm(username: String, password: String) {
    onNodeWithTag(USERNAME_FIELD_TAG).performTextInput(username)
    onNodeWithTag(PASSWORD_FIELD_TAG).performTextInput(password)
}

private fun HomeComposeRule.awaitText(text: String) {
    waitUntilAtLeastOneExists(hasText(text), NETWORK_TIMEOUT_MILLIS)
}

@RunWith(Enclosed::class)
class LoginScreenUiTest {

    @RunWith(AndroidJUnit4::class)
    class Login {
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
        fun openMainScreenWhenLogginInWithDebugCredential(){
            composeRule.awaitText("Sign In")
            pause()

            composeRule.fillLoginForm(DEBUG_USERNAME, DEBUG_PASSWORD)
            pause()

            composeRule.tapLoginButton()
            pause()

            composeRule.waitUntilAtLeastOneExists(
                hasText("PLAFON", substring = true),
                NETWORK_TIMEOUT_MILLIS,
            )
            pause()
        }
    }

}