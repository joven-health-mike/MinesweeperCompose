/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.view

import com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState

class TileViewClickListener(
    private val gameUiState: GameState,
    private val gameViewModel: GameViewModel
) {
    fun onClick(index: Int) {
        // any clicks after game over should reset the game
        if (gameUiState.gameOver) {
            gameViewModel.resetGame()
            return
        }

        // two types of clicks are allowed:
        //   If the tile is covered, clear it.
        //   If the tile is cleared, try to clear adjacent tiles.
        if (gameUiState.tileStates[index] == TileState.COVERED) {
            gameViewModel.clear(index)
        } else if (gameUiState.tileStates[index] == TileState.CLEARED) {
            tryToClearAdjacentTiles(index)
        }
    }

    fun onLongClick(index: Int) {
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

    private fun tryToClearAdjacentTiles(index: Int) {
        val adjacentFlags = gameViewModel.getAdjacentFlags(index)
        try {
            // if the number of adjacent flags is equal to the value of the tile, clear all adjacent tiles
            if (adjacentFlags == gameUiState.tileValues[index].toInt()) {
                gameViewModel.clearAdjacentTiles(index)
            }
        } catch (e: NumberFormatException) {
            // value is nan - do nothing
        }
    }
}