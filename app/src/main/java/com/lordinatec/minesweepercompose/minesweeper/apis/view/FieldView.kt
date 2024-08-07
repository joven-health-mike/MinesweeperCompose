package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun FieldView(width: Int, height: Int) {
    var tileStates by remember { mutableStateOf(List(width * height) { TileState.COVERED }) }
    Column {
        for (j in 0..<height) {
            Row {
                for (i in 0..<width) {
                    val index = j * width + i
                    TileView(index, tileStates[j * width + i], object : TileViewListener {
                        override fun onClick(index: Int) {
                            tileStates = tileStates.toMutableList().apply {
                                this[index] =
                                    if (Math.random() < 0.5) TileState.CLEARED else TileState.EXPLODED
                            }
                        }

                        override fun onLongClick(index: Int) {
                            tileStates = tileStates.toMutableList().apply {
                                this[index] =
                                    if (this[index] == TileState.FLAGGED) TileState.COVERED else TileState.FLAGGED
                            }
                        }
                    })
                }
            }
        }
    }
}