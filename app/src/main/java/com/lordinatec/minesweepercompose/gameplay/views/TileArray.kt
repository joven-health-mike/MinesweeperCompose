/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import kotlinx.coroutines.delay

/**
 * A 2D array of tiles.
 *
 * @param width The width of the array.
 * @param height The height of the array.
 * @param transposed Whether the array should be transposed.
 * @param tileViewFactory The factory to create the tile views.
 */
@Composable
fun TileArray(
    viewModel: GameViewModel,
    width: Int,
    height: Int,
    transposed: Boolean,
    tileViewFactory: TileViewFactory
) {
    if (transposed) {
        TransposedTileArray(viewModel, width, height, tileViewFactory)
    } else {
        RegularTileArray(viewModel, width, height, tileViewFactory)
    }
}

/**
 * A 2D array of tiles where width and height are transposed.
 */
@Composable
private fun TransposedTileArray(
    viewModel: GameViewModel, width: Int, height: Int, tileViewFactory: TileViewFactory
) {
    Row {
        for (currHeight in 0 until height) {
            Column {
                for (currWidth in 0 until width) {
                    MaybeShowTile(currHeight * width + currWidth, viewModel, tileViewFactory)
                }
            }
        }
    }
}

/**
 * A 2D array of tiles where width and height are not transposed.
 */
@Composable
private fun RegularTileArray(
    viewModel: GameViewModel, width: Int, height: Int, tileViewFactory: TileViewFactory
) {
    Column {
        for (currHeight in 0 until height) {
            Row {
                for (currWidth in 0 until width) {
                    MaybeShowTile(currHeight * width + currWidth, viewModel, tileViewFactory)
                }
            }
        }
    }
}

/**
 * Animate the tile view if it is visible.
 */
@Composable
private fun MaybeShowTile(index: Int, viewModel: GameViewModel, tileViewFactory: TileViewFactory) {
    val gameState by viewModel.uiState.collectAsState()
    var visible by remember { mutableStateOf(true) }
    if (gameState.newGame) {
        LaunchedEffect(Unit) {
            visible = false
            delay(50L * index)
            visible = true
        }
    } else if (!visible) {
        visible = true
    }
    if (visible) {
        tileViewFactory.CreateTileView(currIndex = index)
    }
}
