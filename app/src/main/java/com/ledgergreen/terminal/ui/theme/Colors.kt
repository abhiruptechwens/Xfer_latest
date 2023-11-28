package com.ledgergreen.terminal.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

private val primary = Color(0xFF009688)
private val primaryVariant = Color(0xFF00796B)
private val secondary = Color(0xFF536DFE)
private val secondaryVariant = Color(0xFF314791)
private val background = Color.White

private val successColor = Color(0xFF14C310)
private val warningColor = Color(0xFFEBC500)

val appColors: Colors = lightColors(
    primary = primary,
    primaryVariant = primaryVariant,
    onPrimary = Color.White,
    secondary = secondary,
    secondaryVariant = secondaryVariant,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    background = background,
    onBackground = Color.Black,
)

@Suppress("UnusedReceiverParameter")
val Colors.success: Color get() = successColor

@Suppress("UnusedReceiverParameter")
val Colors.warning: Color get() = warningColor
