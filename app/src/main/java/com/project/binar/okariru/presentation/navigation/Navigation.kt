package com.project.binar.okariru.presentation.navigation

import android.R.attr.onClick
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.project.binar.okariru.R
import com.project.binar.okariru.data.auth.repository.AuthUiState
import com.project.binar.okariru.data.status_pinjaman.dto.ListPinjamanDto
import com.project.binar.okariru.presentation.Pinjaman.PengajuanPinjamanPage
import com.project.binar.okariru.presentation.angsuran.DaftarPengajuanAngsuranPage
import com.project.binar.okariru.presentation.angsuran.RincianAngsuranPage
import com.project.binar.okariru.presentation.home.HomePage
import com.project.binar.okariru.presentation.login.LoginPage
import com.project.binar.okariru.presentation.profile.ProfilePage
import com.project.binar.okariru.presentation.profile.editProfilePage
import com.project.binar.okariru.presentation.register.RegisterPage
import com.project.binar.okariru.presentation.reset.ForgotPasswordPage
import com.project.binar.okariru.presentation.reset.newPasswordPage
import com.project.binar.okariru.presentation.reset.otpPage
import com.project.binar.okariru.presentation.shared.component.AppButton
import com.project.binar.okariru.presentation.shared.component.AppButtonVariant
import com.project.binar.okariru.presentation.shared.component.FloatingNavItem
import com.project.binar.okariru.presentation.shared.sharedActivityViewModel
import com.project.binar.okariru.presentation.status_pinjaman.DaftarPengajuanPage
import com.project.binar.okariru.presentation.status_pinjaman.StatusPengajuanDetailPage
import com.project.binar.okariru.presentation.status_pinjaman.StatusPinjamanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authState: AuthUiState
) {
    val destinations = remember { TopLevelDestination.entries }
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val currentTab = destinations.firstOrNull { destination ->
        currentDestination?.hasRoute(destination.route::class) == true
    }

    // kalau sesi habis di tengah2 -> langsung ke login screen (logout otomatis)
    LaunchedEffect(authState.isLoggedOut, currentDestination) {
        if (!authState.isLoggedOut) return@LaunchedEffect

        val alreadyOnAuthRoute = currentDestination?.hasRoute(LoginRoute::class) == true ||
                currentDestination?.hasRoute(RegisterRoute::class) == true ||
                currentDestination?.hasRoute(ForgotPasswordRoute::class) == true ||
                currentDestination?.hasRoute(OtpRoute::class) == true ||
                currentDestination?.hasRoute(ResetPasswordFormRoute::class) == true

        if (!alreadyOnAuthRoute) {
            navController.navigate(AuthGraph) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
//        topBar = {
//            TopAppBar(
//                title = { Text(text = "") },
//                actions = {
//                    IconButton(onClick = {
//
//                    }) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
//                            contentDescription = "Log out Button"
//                        )
//                    }
//                }
//            )
//        },
        bottomBar = {
            val isAuthRoute = currentDestination?.hasRoute(LoginRoute::class) == true ||
                    currentDestination?.hasRoute(RegisterRoute::class) == true ||
                    currentDestination?.hasRoute(ForgotPasswordRoute::class) == true ||
                    currentDestination?.hasRoute(OtpRoute::class) == true ||
                    currentDestination?.hasRoute(ResetPasswordFormRoute::class) == true ||
                    currentDestination?.hasRoute(EditProfileRoute::class) == true

            if (!isAuthRoute) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(28.dp),
                                clip = false
                            ),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    )  {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            destinations.forEach { destination ->
                                FloatingNavItem(
                                    icon = destination.iconRes,
                                    label = destination.labelRes,
                                    selected = destination == currentTab,
                                    onClick = { navController.navigateToTab(destination) }
                                )
                            }
                        }
                    }
                }
            }

        },
           ) { innerPadding ->
        if (authState.isRestoringSession) {
            // Tunggu sesi tersimpan selesai dibaca dulu, supaya startDestination di bawah
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            NavHost(
                navController = navController,
                startDestination = remember {
                    if (authState.isLoggedIn) HomeRoute else AuthGraph
                },
                modifier = Modifier.padding(innerPadding),
                enterTransition = { fadeIn(tween(220)) },
                exitTransition = { fadeOut(tween(180)) }
            ) {
                authgraph(navController)

                composable<HomeRoute> {
                    HomePage(
                        onPinjamanClick = {navController.navigateToTab(TopLevelDestination.PINJAMAN)},
                        onStatusPinjamanClick = { navController.navigate(StatusPinjamanRoute) },
                        onPembayaranClick = { navController.navigate(PembayaranRoute) }
                    )
                }

                composable<PinjamanRoute> {
                    PengajuanPinjamanPage(
                        onBackClick = { navController.navigateToTab(TopLevelDestination.HOME) },
                        onSuccess = { navController.navigateToTab(TopLevelDestination.HOME) }
                    )
                }

                composable<ProfileRoute> {
                    ProfilePage(
                        onEditProfileClick = {
                            navController.navigate(EditProfileRoute)
                        }
                    )
                }

                composable<EditProfileRoute> {
                    editProfilePage(
                        onBackClick = { navController.popBackStack() },
                        onProfileUpdated = { navController.navigateToTab(TopLevelDestination.PROFILE) }
                    )
                }

                composable<StatusPinjamanRoute> {
                    DaftarPengajuanPage(
                        onBackClick = {navController.popBackStack()},
                        onItemClick = {pinjaman -> navController.navigate(DetailStatusPinjamanRoute(pinjaman.transPinjamanId))}
                    )
                }

                composable<DetailStatusPinjamanRoute> { sharedState ->
                    val route = sharedState.toRoute<DetailStatusPinjamanRoute>()
                    val viewModel: StatusPinjamanViewModel = sharedActivityViewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    val sharedItemData = uiState.items.firstOrNull { it.transPinjamanId == route.transPinjamanId }

                    if (sharedItemData != null) {
                        StatusPengajuanDetailPage(
                            item = sharedItemData,
                            modifier = Modifier.fillMaxSize(),
                            onBackClick = { navController.popBackStack() }
                        )
                    } else {
                        // Tampilkan indikator Loading atau Error State jika data belum ditemukan/loading
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                composable<PembayaranRoute> {
                    DaftarPengajuanAngsuranPage(
                        onBackClick = { navController.popBackStack() },
                                onItemClick = {seeDetail: ListPinjamanDto -> navController.navigate(
                                    DetailAngsuranRoute(seeDetail.transPinjamanId))}
                    )
                }

                composable<DetailAngsuranRoute>() {
                    RincianAngsuranPage(
                        onBackClick = { navController.popBackStack() },
                    )
                }
            }
        }
    }


}


fun NavGraphBuilder.authgraph(navController: NavHostController) {
    navigation<AuthGraph>(startDestination = LoginRoute) {

        composable<LoginRoute> {
            LoginPage(
                onRegisterClick = {
                    navController.navigate(RegisterRoute)
                },
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo<AuthGraph> { inclusive = true }
                    }
                },
                onResetPassword = {
                    navController.navigate(ForgotPasswordRoute)
                }


            )
        }
        composable<RegisterRoute> {
            RegisterPage(
                onRegisterSuccess = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable<ForgotPasswordRoute> {
            ForgotPasswordPage(
                //launched effect
                onOtpSent = { email ->
                    navController.navigate(OtpRoute(email = email))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<OtpRoute> { ambilEmail -> val emailPass = ambilEmail.toRoute<OtpRoute>()
            otpPage(
                //launched effect
                emailOtp = emailPass.email,
                onBackClick = { navController.popBackStack() },
                onOtpVerified = {resetToken, email ->
                    navController.navigate(ResetPasswordFormRoute(resetToken = resetToken, email = email))
                },


            )
        }
        composable<ResetPasswordFormRoute>{ ambilResetToken -> val restToken = ambilResetToken.toRoute<ResetPasswordFormRoute>()
            newPasswordPage(
                resetToken = restToken.resetToken,
                onBackClick = { navController.popBackStack() },
                 onPasswordChangeds = {
                    navController.navigate(LoginRoute) {
                        popUpTo<AuthGraph> { inclusive = true }
                    }
                }
            )
        }
    }

}

private fun NavHostController.navigateToTab(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}