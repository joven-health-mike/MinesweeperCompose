package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel

@Composable
fun FieldView(fieldViewModel: FieldViewModel) {
    val gameUiState by fieldViewModel.uiState.collectAsState()
    Column {
        for (currHeight in 0..<Config.height) {
            Row {
                for (currWidth in 0..<Config.width) {
                    val currIndex = xyToIndex(currWidth, currHeight)
                    TileView(
                        currIndex,
                        gameUiState.tileValues[currIndex],
                        gameUiState.tileStates[currIndex],
                        object : TileViewListener {
                            override fun onClick(index: Int) {
                                if (gameUiState.tileStates[index] == TileState.COVERED) {
                                    fieldViewModel.clearIndex(index)
                                }
                            }

                            override fun onLongClick(index: Int) {
                                if (gameUiState.tileStates[index] == TileState.COVERED
                                    || gameUiState.tileStates[index] == TileState.FLAGGED
                                ) {
                                    fieldViewModel.flagIndex(index)
                                }
                            }
                        })
                }
            }
        }
    }
}