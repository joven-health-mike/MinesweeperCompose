/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel
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
    viewModel: GameViewModel,
    width: Int,
    height: Int,
    tileViewFactory: TileViewFactory
) {
    val gameState by viewModel.uiState.collectAsState()
    Row {
        for (currHeight in 0 until height) {
            Column {
                for (currWidth in 0 until width) {
                    val currIndex = xyToIndex(currWidth, currHeight)
                    var visible by remember { mutableStateOf(true) }
                    if (gameState.newGame) {
                        LaunchedEffect(Unit) {
                            visible = false
                            delay(100L * currIndex)
                            visible = true
                        }
                    }
                    if (visible) {
                        tileViewFactory.CreateTileView(currIndex = currIndex)
                    }
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
    viewModel: GameViewModel,
    width: Int,
    height: Int,
    tileViewFactory: TileViewFactory
) {
    val gameState by viewModel.uiState.collectAsState()
    Column {
        for (currHeight in 0 until height) {
            Row {
                for (currWidth in 0 until width) {
                    val currIndex = xyToIndex(currWidth, currHeight)
                    var visible by remember { mutableStateOf(true) }
                    if (gameState.newGame) {
                        LaunchedEffect(Unit) {
                            visible = false
                            delay(50L * currIndex)
                            visible = true
                        }
                    }
                    if (visible) {
                        tileViewFactory.CreateTileView(currIndex = currIndex)
                    }
                }
            }
        }
    }
}