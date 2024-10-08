/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.gameplay.views.animations.dropDown
import com.lordinatec.minesweepercompose.gameplay.views.animations.rememberDropDownAnimation
import com.lordinatec.minesweepercompose.ui.theme.Typography
import com.lordinatec.minesweepercompose.views.AppIcon

/**
 * Factory class to create TileView
 *
 * @param gameViewModel The view model of the game
 * @param onClick Listener for clicks
 * @param onLongClick Listener for long clicks
 */
class TileViewFactory(
    private val gameViewModel: GameViewModel,
    private val onClick: ((index: Int) -> Unit)? = null,
    private val onLongClick: ((index: Int) -> Unit)? = null
) {
    /**
     * Create a TileView with the given index.
     */
    @Composable
    fun CreateTileView(currIndex: Int) {
        val gameUiState by gameViewModel.uiState.collectAsState()
        val startPosition = if (gameUiState.newGame) -80f else 0f
        val dropDownAnimation = rememberDropDownAnimation(startPosition)
        Box(modifier = Modifier.dropDown(dropDownAnimation)) {
            if (!gameUiState.gameOver) {
                LaunchedEffect(Unit) {
                    dropDownAnimation.drop()
                }
            }
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
    value: TileValue,
    state: TileState,
    gameViewModel: GameViewModel,
    onClick: ((index: Int) -> Unit)? = null,
    onLongClick: ((index: Int) -> Unit)? = null
) {
    val tileViewSize =
        dimensionResource(id = com.lordinatec.minesweepercompose.R.dimen.tile_view_size)
    Box(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick?.let { it(index) } },
                onClick = { onClick?.let { it(index) } })
            .size(tileViewSize)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        state.primaryColor, state.secondaryColor
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (state == TileState.COVERED) {
            TextTile(value)
        } else {
            when (value.value) {
                "F" -> FlagTile(gameViewModel, index)
                "*" -> MineTile()
                else -> TextTile(value)
            }
        }
    }
}

@Composable
private fun TextTile(value: TileValue) {
    Text(
        text = value.value,
        color = value.textColor,
        style = Typography.bodyLarge
    )
}

@Composable
private fun FlagTile(gameViewModel: GameViewModel, index: Int) {
    val gameUiState = gameViewModel.uiState.collectAsState()
    val flagSize = dimensionResource(id = com.lordinatec.minesweepercompose.R.dimen.flag_size)
    Icon(
        Icons.Filled.Flag,
        contentDescription = "Flagged tile",
        tint = colorResource(android.R.color.holo_green_dark),
        modifier = Modifier
            .width(flagSize)
            .height(flagSize)
    )
    if (gameUiState.value.gameOver && !gameViewModel.flagIsCorrect(index)
    ) {
        IncorrectFlagOverlay()
    }
}

@Composable
private fun MineTile() {
    val mineSize = dimensionResource(id = com.lordinatec.minesweepercompose.R.dimen.mine_size)
    AppIcon(size = mineSize, tint = Color.Black)
}

@Composable
private fun IncorrectFlagOverlay() {
    val flagSize = dimensionResource(id = com.lordinatec.minesweepercompose.R.dimen.flag_size)
    Icon(
        Icons.Filled.Close,
        contentDescription = "Flag is wrong",
        tint = colorResource(android.R.color.holo_red_dark),
        modifier = Modifier
            .width(flagSize)
            .height(flagSize)
    )
}

/**
 * Enum class for the state of a tile.
 *
 * @param primaryColor The primary color of the tile
 * @param secondaryColor The secondary color of the tile
 */
enum class TileState(val primaryColor: Color, val secondaryColor: Color) {
    COVERED(Color.Blue, Color.Gray),
    CLEARED(Color(0xFF808080), Color.DarkGray),
    FLAGGED(Color.Blue, Color.Gray),
    EXPLODED(Color.Red, Color.Magenta)
}

/**
 * Enum class for the value of a tile.
 */
enum class TileValue(val value: String, val textColor: Color) {
    EMPTY("0", Color.Transparent),
    ONE("1", Color.Blue),
    TWO("2", Color.Green),
    THREE("3", Color.Red),
    FOUR("4", Color.Magenta),
    FIVE("5", Color.Cyan),
    SIX("6", Color.Yellow),
    SEVEN("7", Color.Gray),
    EIGHT("8", Color.DarkGray),
    MINE("*", Color.Transparent),
    FLAG("F", Color.Transparent),
    UNKNOWN("", Color.Transparent);

    companion object {
        /**
         * Get the TileValue from the given value.
         *
         * @param value The value of the tile
         *
         * @return The TileValue
         */
        fun fromValue(value: Int): TileValue {
            return when (value) {
                0 -> EMPTY
                1 -> ONE
                2 -> TWO
                3 -> THREE
                4 -> FOUR
                5 -> FIVE
                6 -> SIX
                7 -> SEVEN
                8 -> EIGHT
                else -> UNKNOWN
            }
        }
    }
}
