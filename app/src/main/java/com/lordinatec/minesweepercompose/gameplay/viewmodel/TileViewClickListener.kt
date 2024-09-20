/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.gameplay.views.TileState
import javax.inject.Inject

/**
 * Handles click events on the tile views.
 *
 * @param gameViewModel the view model for the game
 */
class TileViewClickListener @Inject constructor(
    private val gameViewModel: GameViewModel
) {

    /**
     * Handles a click event on a tile.
     *
     * Two types of clicks are allowed:
     *  If the tile is covered, clear it.
     *  If the tile is cleared, try to clear adjacent tiles.
     *
     * If the game is over, any click will reset the game.
     *
     * @param index the index of the tile that was clicked
     */
    fun onClick(index: Int) {
        val gameUiState = gameViewModel.uiState.value

        // Reset the game if it's over
        if (gameUiState.gameOver) {
            gameViewModel.resetGame()
            return
        }

        // Clear the tile or attempt to clear adjacent tiles based on its state
        when (gameUiState.tileStates[index]) {
            TileState.COVERED -> gameViewModel.clear(index)
            TileState.CLEARED -> tryToClearAdjacentTiles(index)
            else -> return
        }
    }

    /**
     * Handles a long click event on a tile. If the tile is covered or flagged, toggle the flag.
     *
     * If the game is over, any click will reset the game.
     *
     * @param index the index of the tile that was clicked
     */
    fun onLongClick(index: Int) {
        val gameUiState = gameViewModel.uiState.value

        // Reset the game if it's over
        if (gameUiState.gameOver) {
            gameViewModel.resetGame()
            return
        }

        // Toggle the flag if the tile is covered or flagged
        if (gameUiState.tileStates[index] in listOf(TileState.COVERED, TileState.FLAGGED)) {
            gameViewModel.toggleFlag(index)
        }
    }

    /**
     * Tries to clear adjacent tiles if the number of adjacent flags is equal to the value of the tile.
     *
     * @param index the index of the tile
     */
    private fun tryToClearAdjacentTiles(index: Int) {
        val gameUiState = gameViewModel.uiState.value
        val adjacentFlags = gameViewModel.getAdjacentFlags(index)

        val tileValue = gameUiState.tileValues[index].value.toIntOrNull()

        if (tileValue != null && adjacentFlags == tileValue) {
            gameViewModel.clearAdjacentTiles(index)
        }
    }
}
