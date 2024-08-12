package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewState

private val gameOverText: (Boolean) -> String =
    { winner -> "You " + if (winner) "Win!" else "Lose." }

@Composable
fun FieldView(
    fieldViewModel: FieldViewModel,
    exitClicked: (() -> Unit)? = null
) {
    val gameUiState by fieldViewModel.uiState.collectAsState()

    // show game over dialog once the game is over
    if (gameUiState.gameOver) {
        GameOverDialog(
            title = gameOverText(gameUiState.winner),
            newGameClicked = { fieldViewModel.resetGame() },
        ) { exitClicked?.let { it() } }
    }

    Field(gameUiState = gameUiState, fieldViewModel = fieldViewModel)
}

@Composable
private fun Field(gameUiState: FieldViewState, fieldViewModel: FieldViewModel) {
    val clickListener = TileViewClickListener(gameUiState, fieldViewModel)
    val tileViewFactory = TileViewFactory(
        gameUiState = gameUiState,
        onClick = { clickListener.onClick(it) },
        onLongClick = { clickListener.onLongClick(it) })

    // create the field
    Column {
        for (currHeight in 0 until Config.HEIGHT) {
            Row {
                FieldRow(heightIndex = currHeight, tileViewFactory = tileViewFactory)
            }
        }
    }
}

@Composable
private fun FieldRow(heightIndex: Int, tileViewFactory: TileViewFactory) {
    // create a row of tiles
    for (currWidth in 0 until Config.WIDTH) {
        val currIndex = xyToIndex(currWidth, heightIndex)
        tileViewFactory.CreateTileView(currIndex = currIndex)
    }
}
