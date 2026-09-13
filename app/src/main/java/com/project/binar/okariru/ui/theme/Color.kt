package com.project.binar.okariru.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.project.binar.okariru.R

/**
 * Token warna aplikasi, dibaca langsung dari res/values/colors.xml
 * supaya satu sumber warna dengan FE (bukan hex hardcode di Kotlin).
 */
val ColorPrimary: Color @Composable get() = colorResource(R.color.red_700)
val ColorOnPrimary: Color @Composable get() = colorResource(R.color.whiteColor)
val ColorPrimaryContainer: Color @Composable get() = colorResource(R.color.red_100)
val ColorOnPrimaryContainer: Color @Composable get() = colorResource(R.color.red_900)

val ColorSecondary: Color @Composable get() = colorResource(R.color.red_400)
val ColorOnSecondary: Color @Composable get() = colorResource(R.color.whiteColor)

val ColorError: Color @Composable get() = colorResource(R.color.red_600)
val ColorOnError: Color @Composable get() = colorResource(R.color.whiteColor)

val ColorBackground: Color @Composable get() = colorResource(R.color.whiteColor)
val ColorOnBackground: Color @Composable get() = colorResource(R.color.grey_900)

val ColorSurface: Color @Composable get() = colorResource(R.color.whiteColor)
val ColorOnSurface: Color @Composable get() = colorResource(R.color.grey_900)
val ColorSurfaceVariant: Color @Composable get() = colorResource(R.color.grey_100)
val ColorOnSurfaceVariant: Color @Composable get() = colorResource(R.color.grey_600)

val ColorOutline: Color @Composable get() = colorResource(R.color.grey_400)
