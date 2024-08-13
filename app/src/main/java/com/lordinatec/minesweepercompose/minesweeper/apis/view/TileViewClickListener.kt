package com.lordinatec.minesweepercompose.minesweeper.apis.view

import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.model.FieldViewState

class TileViewClickListener(
    private val gameUiState: FieldViewState,
    private val fieldViewModel: FieldViewModel
) {
    fun onClick(index: Int) {
        // any clicks after game over should reset the game
        if (gameUiState.gameOver) {
            fieldViewModel.resetGame()
            return
        }

        // two types of clicks are allowed:
        //   If the tile is covered, clear it.
        //   If the tile is cleared, try to clear adjacent tiles.
        if (gameUiState.tileStates[index] == TileState.COVERED) {
            fieldViewModel.clear(index)
        } else if (gameUiState.tileStates[index] == TileState.CLEARED) {
            tryToClearAdjacentTiles(index)
        }
    }

    fun onLongClick(index: Int) {
        // any clicks after game over should reset the game
        if (gameUiState.gameOver) {
            fieldViewModel.resetGame()
            return
        }

        // if the tile is covered or flagged, toggle the flag
        if (gameUiState.tileStates[index] == TileState.COVERED
            || gameUiState.tileStates[index] == TileState.FLAGGED
        ) {
            fieldViewModel.toggleFlag(index)
        }
    }

    private fun tryToClearAdjacentTiles(index: Int) {
        val adjacentFlags = fieldViewModel.getAdjacentFlags(index)
        try {
            // if the number of adjacent flags is equal to the value of the tile, clear all adjacent tiles
            if (adjacentFlags == gameUiState.tileValues[index].toInt()) {
                fieldViewModel.clearAdjacentTiles(index)
            }
        } catch (e: NumberFormatException) {
            // value is nan - do nothing
        }
    }
}