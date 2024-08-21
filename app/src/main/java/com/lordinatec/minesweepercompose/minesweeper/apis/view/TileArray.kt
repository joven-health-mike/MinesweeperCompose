package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex

@Composable
fun TileArray(width: Int, height: Int, tileViewFactory: TileViewFactory) {
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