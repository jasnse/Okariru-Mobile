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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.project.binar.okariru.presentation.status_pinjaman.DaftarPengajuanPage
import com.project.binar.okariru.presentation.status_pinjaman.StatusPengajuanDetailPage
import android.net.Uri
import com.project.binar.okariru.presentation.landing_page.HomeContentUnauthenticated
import com.project.binar.okariru.presentation.splash_screen.SplashContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authState: AuthUiState,
    pendingDeepLink: Uri? = null,
    onDeepLinkHandled: () -> Unit = {},
) {
    val destinations = remember { TopLevelDestination.entries }
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val currentTab = destinations.firstOrNull { destination ->
        currentDestination?.hasRoute(destination.route::class) == true
    }

    // Jeda splash screen ke login page
    var minSplashElapsed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(2575)
        minSplashElapsed = true
    }

    // NavHost selalu mulai dari AuthGraph (netral) -- begitu sesi selesai di-restore dan
    // ternyata user memang sudah login, pindahin ke Home sekali di sini
    LaunchedEffect(authState.isRestoringSession) {
        if (authState.isRestoringSession) return@LaunchedEffect
        if (authState.isLoggedIn) {
            navController.navigate(HomeRoute) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // kalau sesi habis di tengah2 -> langsung ke login screen (logout otomatis)
    LaunchedEffect(authState.isLoggedOut, currentDestination) {
        if (!authState.isLoggedOut) return@LaunchedEffect

        val alreadyOnAuthRoute = currentDestination?.hasRoute(LoginRoute::class) == true ||
                currentDestination?.hasRoute(RegisterRoute::class) == true ||
                currentDestination?.hasRoute(ForgotPasswordRoute::class) == true ||
                currentDestination?.hasRoute(OtpRoute::class) == true ||
                currentDestination?.hasRoute(ResetPasswordFormRoute::class) == true ||
                currentDestination?.hasRoute(LandingRoute::class) == true

        if (!alreadyOnAuthRoute) {
            navController.navigate(AuthGraph) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // tangani deep link dari tap notifikasi push, contoh "okariru://status-pinjaman/123"
    LaunchedEffect(pendingDeepLink, authState.isLoggedIn, authState.isRestoringSession) {
        val uri = pendingDeepLink ?: return@LaunchedEffect
        if (authState.isRestoringSession || !authState.isLoggedIn) return@LaunchedEffect

        val transPinjamanId = uri.takeIf { it.host == "status-pinjaman" }
            ?.lastPathSegment
            ?.toIntOrNull()

        if (transPinjamanId != null) {
            navController.navigate(DetailStatusPinjamanRoute(transPinjamanId))
        }
        onDeepLinkHandled()
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
            val exclude = authState.isRestoringSession || !minSplashElapsed ||
                    currentDestination?.hasRoute(LoginRoute::class) == true ||
                    currentDestination?.hasRoute(RegisterRoute::class) == true ||
                    currentDestination?.hasRoute(ForgotPasswordRoute::class) == true ||
                    currentDestination?.hasRoute(OtpRoute::class) == true ||
                    currentDestination?.hasRoute(ResetPasswordFormRoute::class) == true ||
                    currentDestination?.hasRoute(EditProfileRoute::class) == true ||
                    currentDestination?.hasRoute(LandingRoute::class) == true ||

                    currentDestination?.hasRoute(StatusPinjamanRoute::class) == true||
                    currentDestination?.hasRoute(DetailStatusPinjamanRoute::class) == true||
                    currentDestination?.hasRoute(PembayaranRoute::class) == true||
                    currentDestination?.hasRoute(DetailAngsuranRoute::class) == true

            if (!exclude) {
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
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = AuthGraph,
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

                composable<DetailStatusPinjamanRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<DetailStatusPinjamanRoute>()
                    StatusPengajuanDetailPage(
                        transPinjamanId = route.transPinjamanId,
                        modifier = Modifier.fillMaxSize(),
                        onBackClick = { navController.popBackStack() }
                    )
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

            if (authState.isRestoringSession || !minSplashElapsed) {
                // nutup NavHost yang di belakang selama splash masih tampil
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    SplashContent()
                }
            }
        }
    }


}


fun NavGraphBuilder.authgraph(navController: NavHostController) {
    navigation<AuthGraph>(startDestination = LandingRoute) {

        composable<LandingRoute> {
            HomeContentUnauthenticated (
                goToLogin = {
                    navController.navigate(LoginRoute)
                }
            )
        }

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