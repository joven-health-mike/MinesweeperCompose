package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel

@Composable
fun FieldView(
    gameViewModel: GameViewModel
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    Field(gameUiState = gameUiState, gameViewModel = gameViewModel)
}

@Composable
private fun Field(gameUiState: GameState, gameViewModel: GameViewModel) {
    val clickListener = TileViewClickListener(gameUiState, gameViewModel)
    val tileViewFactory = TileViewFactory(
        gameUiState = gameUiState,
        gameViewModel = gameViewModel,
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
