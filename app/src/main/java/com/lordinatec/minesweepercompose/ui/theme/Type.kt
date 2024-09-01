/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
    labelSmall = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
)
