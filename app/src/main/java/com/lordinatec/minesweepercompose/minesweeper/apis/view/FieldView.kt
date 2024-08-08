package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel

@Composable
fun FieldView(fieldViewModel: FieldViewModel) {
    val gameUiState by fieldViewModel.uiState.collectAsState()
    Column {
        for (j in 0..<Config.height) {
            Row {
                for (i in 0..<Config.width) {
                    val index = j * Config.width + i
                    TileView(
                        index,
                        gameUiState.tileStates[j * Config.width + i],
                        object : TileViewListener {
                            override fun onClick(index: Int) {
                                fieldViewModel.clearIndex(index)
                            }

                            override fun onLongClick(index: Int) {
                                fieldViewModel.flagIndex(index)
                            }
                        })
                }
            }
        }
    }
}