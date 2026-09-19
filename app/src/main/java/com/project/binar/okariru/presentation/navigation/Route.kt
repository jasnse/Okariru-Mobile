package com.project.binar.okariru.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LandingRoute : AppRoute

@Serializable
data object HomeRoute : AppRoute

@Serializable
data object AuthGraph : AppRoute
@Serializable
data object LoginRoute : AppRoute
@Serializable
data object RegisterRoute : AppRoute


@Serializable
data object ForgotPasswordRoute : AppRoute
@Serializable
data class  OtpRoute (val email: String) : AppRoute
@Serializable
data class ResetPasswordFormRoute(val resetToken: String, val email: String) : AppRoute



//@Serializable
//data object LoanGraph : AppRoute
@Serializable
data object PinjamanRoute : AppRoute

//@Serializable
//data object ProfileGraph : AppRoute
@Serializable
data object ProfileRoute : AppRoute

@Serializable
data object EditProfileRoute : AppRoute

@Serializable
data object StatusPinjamanRoute : AppRoute

@Serializable
data class DetailStatusPinjamanRoute(val transPinjamanId: Int) : AppRoute

@Serializable
data object PembayaranRoute : AppRoute

@Serializable
data class DetailAngsuranRoute(val transPinjamanId: Int) : AppRoute

//@Serializable
//data object PembayaranGraph : AppRoute