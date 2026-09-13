package com.project.binar.okariru.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlignVerticalBottom
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: AppRoute,
    val labelRes: String,
    val iconRes: ImageVector,
) {

    HOME(HomeRoute, "Home", Icons.Outlined.Home),
//    AUTH(LoginRoute, "auth", Icons.Outlined.AlignVerticalBottom),
    PINJAMAN(PinjamanRoute, "pinjaman", Icons.Outlined.Money),
    PROFILE(ProfileRoute, "profile", Icons.Outlined.Person);


}