/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.gameplay.views.TileState

/**
 * Handles click events on the tile views.
 *
 * @param gameViewModel the view model for the game
 */
class TileViewClickListener(
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
        // any clicks after game over should reset the game
        if (gameUiState.gameOver) {
            gameViewModel.resetGame()
            return
        }

        if (gameUiState.tileStates[index] == TileState.COVERED) {
            //   If the tile is covered, clear it.
            gameViewModel.clear(index)
        } else if (gameUiState.tileStates[index] == TileState.CLEARED) {
            //   If the tile is cleared, try to clear adjacent tiles.
            tryToClearAdjacentTiles(index)
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
        // any clicks after game over should reset the game
        if (gameUiState.gameOver) {
            gameViewModel.resetGame()
            return
        }

        // if the tile is covered or flagged, toggle the flag
        if (gameUiState.tileStates[index] == TileState.COVERED
            || gameUiState.tileStates[index] == TileState.FLAGGED
        ) {
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
        try {
            // if the number of adjacent flags is equal to the value of the tile, clear all adjacent tiles
            if (adjacentFlags == gameUiState.tileValues[index].value.toInt()) {
                gameViewModel.clearAdjacentTiles(index)
            }
        } catch (e: NumberFormatException) {
            // value is nan - do nothing
        }
    }
}
