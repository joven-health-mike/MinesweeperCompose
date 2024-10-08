/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.views

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.lordinatec.minesweepercompose.R

/**
 * App icon vector composable
 *
 * @param size size of the icon (1:1 aspect ratio)
 */
@Composable
fun AppIcon(size: Dp, tint: Color = MaterialTheme.colorScheme.primary) {
    Icon(
        painterResource(R.drawable.android_icon_foreground_minesweeper),
        "app icon",
        tint = tint,
        modifier = Modifier
            .width(size)
            .height(size)
    )
}
