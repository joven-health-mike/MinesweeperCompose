package com.lordinatec.minesweepercompose.minesweeper.apis.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel

interface FieldViewListener {
    fun onExitClicked()
}

@Composable
fun FieldView(fieldViewModel: FieldViewModel, listener: FieldViewListener) {
    val gameUiState by fieldViewModel.uiState.collectAsState()

    if (gameUiState.gameOver) {
        GameOverDialog(
            title = "You " + if (gameUiState.winner) "Win!" else "Lose",
            object : GameOverDialogListener {
                override fun onNewGameClicked() {
                    fieldViewModel.resetGame()
                }

                override fun onExitClicked() {
                    listener.onExitClicked()
                }
            })
    }

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
                                if (gameUiState.gameOver) return

                                if (gameUiState.tileStates[index] == TileState.COVERED) {
                                    fieldViewModel.clearIndex(index)
                                }
                            }

                            override fun onLongClick(index: Int) {
                                if (gameUiState.gameOver) return

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

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}