/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState
import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel

/**
 * Factory class to create TileView
 *
 * @param gameUiState The state of the game
 * @param gameViewModel The view model of the game
 * @param onClick Listener for clicks
 * @param onLongClick Listener for long clicks
 */
class TileViewFactory(
    private val gameUiState: GameState,
    private val gameViewModel: GameViewModel,
    private val onClick: ((index: Int) -> Unit)? = null,
    private val onLongClick: ((index: Int) -> Unit)? = null
) {
    /**
     * Create a TileView with the given index.
     */
    @Composable
    fun CreateTileView(
        currIndex: Int,
    ) {
        TileView(
            currIndex,
            gameUiState.tileValues[currIndex],
            gameUiState.tileStates[currIndex],
            gameViewModel,
            onClick,
            onLongClick
        )
    }
}

/**
 * View for a tile.
 *
 * @param index The index of the tile
 * @param value The value of the tile
 * @param state The state of the tile
 * @param gameViewModel The view model of the game
 * @param onClick Listener for clicks
 * @param onLongClick Listener for long clicks
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TileView(
    index: Int,
    value: String,
    state: TileState,
    gameViewModel: GameViewModel,
    onClick: ((index: Int) -> Unit)? = null,
    onLongClick: ((index: Int) -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick?.let { it(index) } },
                onClick = { onClick?.let { it(index) } })
            .size(75.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        state.primaryColor, state.secondaryColor
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = value, color = Color.White,
            style = TextStyle(
                fontSize = 24.sp,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
        )
        val gameUiState = gameViewModel.uiState.collectAsState()
        if (gameUiState.value.gameOver && state == TileState.FLAGGED && !gameViewModel.flagIsCorrect(
                index
            )
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "Flag is wrong",
                tint = colorResource(android.R.color.holo_red_dark),
                modifier = Modifier
                    .width(70.dp)
                    .height(70.dp)
                    .shadow(10.dp)
            )
        }
    }
}

/**
 * Enum class for the state of a tile.
 *
 * @param primaryColor The primary color of the tile
 * @param secondaryColor The secondary color of the tile
 */
enum class TileState(val primaryColor: Color, val secondaryColor: Color) {
    COVERED(Color.Blue, Color.Gray),
    CLEARED(Color.Gray, Color.DarkGray),
    FLAGGED(Color.Green, Color.Blue),
    EXPLODED(Color.Red, Color.Magenta)
}