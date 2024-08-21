/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex

@Composable
fun TileArray(width: Int, height: Int, transposed: Boolean, tileViewFactory: TileViewFactory) {
    if (transposed) {
        TransposedTileArray(width, height, tileViewFactory)
    } else {
        RegularTileArray(width, height, tileViewFactory)
    }
}

@Composable
private fun TransposedTileArray(width: Int, height: Int, tileViewFactory: TileViewFactory) {
    Row {
        for (currHeight in 0 until height) {
            Column {
                for (currWidth in 0 until width) {
                    val currIndex = xyToIndex(currWidth, currHeight)
                    tileViewFactory.CreateTileView(currIndex = currIndex)
                }
            }
        }
    }
}

@Composable
private fun RegularTileArray(width: Int, height: Int, tileViewFactory: TileViewFactory) {
    Column {
        for (currHeight in 0 until height) {
            Row {
                for (currWidth in 0 until width) {
                    val currIndex = xyToIndex(currWidth, currHeight)
                    tileViewFactory.CreateTileView(currIndex = currIndex)
                }
            }
        }
    }
}