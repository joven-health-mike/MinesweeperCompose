/*
 * Copyright Lordinatec LLC 2024
 */

@file:OptIn(ExperimentalMaterial3Api::class)

package com.lordinatec.minesweepercompose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A top app bar with a title.
 *
 * @param text The title of the top app bar.
 *
 * @return A top app bar with the given title.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(text: String) {
    return TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), title = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = text)
            }
        })
}

/**
 * Add padding to the top of the screen to account for the status bar and top app bar.
 *
 * @param statusBarBottom The height of the status bar.
 *
 * @return A modifier that adds padding to the top of the screen.
 */
fun Modifier.topBarPadding(statusBarBottom: Dp) =
    windowInsetsPadding(WindowInsets(top = statusBarBottom + 32.dp))
