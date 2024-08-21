package com.lordinatec.minesweepercompose.minesweeper.apis.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
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
    val portraitMode = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    // create the field
    if (portraitMode) {
        FieldPortrait(tileViewFactory = tileViewFactory)
    } else {
        FieldLandscape(tileViewFactory = tileViewFactory)
    }
}

@Composable
private fun FieldPortrait(tileViewFactory: TileViewFactory) {
    TileArray(width = Config.WIDTH, height = Config.HEIGHT, tileViewFactory = tileViewFactory)
}

@Composable
private fun FieldLandscape(tileViewFactory: TileViewFactory) {
    TileArray(width = Config.HEIGHT, height = Config.WIDTH, tileViewFactory = tileViewFactory)
}
