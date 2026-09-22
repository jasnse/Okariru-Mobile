package com.project.binar.okariru

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.project.binar.okariru.core.security.RootChecker
import com.project.binar.okariru.data.auth.repository.AuthViewModel
import com.project.binar.okariru.presentation.navigation.ExampleNavHost
import com.project.binar.okariru.presentation.security.RootedDeviceScreen
import com.project.binar.okariru.ui.theme.OkariruTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var rootChecker: RootChecker

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Dipanggil setelah pengguna merespons dialog permission (diizinkan atau ditolak)
            startRouting()
        }

    private fun requestNotificationPermission() {
        val granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (granted) {
            startRouting()
        } else {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun startRouting() {
    }

    private val authViewModel: AuthViewModel by viewModels()

    //, mis. "okariru://status-pinjaman/123" dari tap notifikasi
    private var pendingDeepLink by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // wajib sebelum super.onCreate()
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        requestNotificationPermission()
        pendingDeepLink = intent.takeIf { it.data != null }

        setContent {
            OkariruTheme {
                if (rootChecker.isDeviceRooted()) {
                    RootedDeviceScreen(onExit = { finishAffinity() })
                } else {
                    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
                    ExampleNavHost(
                        modifier = Modifier.padding(),
                        authState = authState,
                        pendingDeepLink = pendingDeepLink?.data,
                        onDeepLinkHandled = { pendingDeepLink = null },
                    )
                }
            }
        }
    }

    //  jadi tap notifikasi saat app masih
    // hidup di background bakal lewat sini
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingDeepLink = intent.takeIf { it.data != null }
    }
}
