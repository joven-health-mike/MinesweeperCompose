/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.views

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.gameplay.viewmodel.TileViewClickListener
import com.lordinatec.minesweepercompose.gameplay.views.animations.rememberShakeable
import com.lordinatec.minesweepercompose.gameplay.views.animations.shakeable

/**
 * The view for the field.
 */
@Composable
fun FieldView(
    gameViewModel: GameViewModel
) {
    Field(gameViewModel = gameViewModel)
}

/**
 * The view for the field.
 *
 * @param gameViewModel the game view model
 */
@Composable
private fun Field(gameViewModel: GameViewModel) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val clickListener = TileViewClickListener(gameUiState, gameViewModel)
    val tileViewFactory = TileViewFactory(
        gameViewModel = gameViewModel,
        onClick = { clickListener.onClick(it) },
        onLongClick = { clickListener.onLongClick(it) })
    val portraitMode = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    val shakeable = rememberShakeable()

    Box(modifier = Modifier.shakeable(shakeable)) {
        // shake the field when the game is lost
        if (gameUiState.gameOver && !gameUiState.winner) {
            LaunchedEffect(Unit) {
                shakeable.shake()
            }
        }
        FieldTileArray(gameViewModel, !portraitMode, tileViewFactory)
    }
}

/**
 * The view for the field in portrait mode.
 *
 * @param transposed whether the field is transposed
 * @param tileViewFactory the tile view factory
 */
@Composable
private fun FieldTileArray(
    viewModel: GameViewModel,
    transposed: Boolean,
    tileViewFactory: TileViewFactory
) {
    TileArray(
        viewModel,
        width = Config.width,
        height = Config.height,
        transposed = transposed,
        tileViewFactory = tileViewFactory
    )
}
