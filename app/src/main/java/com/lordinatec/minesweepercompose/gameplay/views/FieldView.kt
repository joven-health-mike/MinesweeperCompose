/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.views

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.lordinatec.minesweepercompose.R
import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.gameplay.viewmodel.TileViewClickListener
import com.lordinatec.minesweepercompose.gameplay.views.animations.ShakeableAnimation
import com.lordinatec.minesweepercompose.gameplay.views.animations.rememberShakeable
import com.lordinatec.minesweepercompose.gameplay.views.animations.shakeable
import com.lordinatec.minesweepercompose.ui.theme.Typography


/**
 * The view for the field.
 */
@Composable
fun FieldView(
    gameViewModel: GameViewModel,
    clickListener: TileViewClickListener = TileViewClickListener(gameViewModel)
) {
    Field(gameViewModel = gameViewModel, clickListener = clickListener)
}

/**
 * The view for the field.
 *
 * @param gameViewModel the game view model
 */
@Composable
private fun Field(gameViewModel: GameViewModel, clickListener: TileViewClickListener) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    val tileViewFactory = TileViewFactory(gameViewModel = gameViewModel,
        onClick = { clickListener.onClick(it) },
        onLongClick = { clickListener.onLongClick(it) })
    val portraitMode = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    val shakeable = rememberShakeable()

    val tileSizeInDp = dimensionResource(id = R.dimen.tile_view_size)
    val width =
        if (portraitMode) tileSizeInDp.times(Config.width) else tileSizeInDp.times(Config.height)
    val height =
        if (portraitMode) tileSizeInDp.times(Config.height) else tileSizeInDp.times(Config.width)
    Box(
        modifier = Modifier
            .shakeable(shakeable)
            .width(width)
            .height(height),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            FieldTileArray(gameViewModel, !portraitMode, tileViewFactory)
        }
        GameOverHandler(gameViewModel, shakeable)
    }
}

/**
 * Displays appropriate overlay only when the game is over.
 *
 * @param gameViewModel the game view model
 * @param shakeable the shakeable animation
 */
@Composable
fun GameOverHandler(gameViewModel: GameViewModel, shakeable: ShakeableAnimation) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    if (gameUiState.gameOver) {
        if (gameUiState.winner) {
            HandleGameWon()
        } else {
            HandleGameLost(shakeable)
        }
    }
}

/**
 * Displays the game won overlay.
 */
@Composable
private fun HandleGameWon() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Green.copy(alpha = 0.25f), shape = RoundedCornerShape(0.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "You Win!",
                style = Typography.bodyLarge.copy(color = Color.White)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Touch to start a new game",
                style = Typography.labelSmall.copy(color = Color.White)
            )
        }
    }
}

/**
 * Displays the game lost overlay.
 *
 * @param shakeable the shakeable animation
 */
@Composable
private fun HandleGameLost(shakeable: ShakeableAnimation) {
    // shake the field when the game is lost
    LaunchedEffect(Unit) {
        shakeable.shake()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Red.copy(alpha = 0.25f), shape = RoundedCornerShape(0.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "You Lose!",
                style = Typography.bodyLarge.copy(color = Color.White)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Touch to start a new game",
                style = Typography.labelSmall.copy(color = Color.White)
            )
        }
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
    viewModel: GameViewModel, transposed: Boolean, tileViewFactory: TileViewFactory
) {
    TileArray(
        viewModel,
        width = Config.width,
        height = Config.height,
        transposed = transposed,
        tileViewFactory = tileViewFactory
    )
}
