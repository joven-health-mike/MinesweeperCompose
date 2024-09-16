/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.config.Config
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Game screen.
 *
 * @param gameController The controller for the game.
 * @param gameStateEventConsumer The consumer for game state events.
 *
 * @return A ViewModel for the Game screen.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameController: GameController,
    private val gameStateEventConsumer: GameStateEventConsumer
) : ViewModel() {

    val uiState = gameStateEventConsumer.uiState

    /* PUBLIC APIS */
    /**
     * Clear the game state and reset the game.
     */
    fun resetGame() {
        gameController.resetGame()
    }

    /**
     * Update the size of the game.
     */
    fun updateSize() {
        if (uiState.value.tileStates.size != Config.width * Config.height) {
            gameController.resetGame()
            gameStateEventConsumer.updateSize()
        }
    }

    /**
     * Checks if a flag is correct.
     *
     * @param index The index of the tile to check.
     *
     * @return True if the flag is correct, false otherwise.
     */
    fun flagIsCorrect(index: Int): Boolean {
        return gameController.flagIsCorrect(index)
    }

    /**
     * Clear the tile at the given index.
     *
     * @param index The index of the tile to clear.
     */
    fun clear(index: Int) {
        // lazily create the game when the user makes their first move
        if (gameController.maybeCreateGame(index)) {
            updateSize()
        }
        gameController.clear(index)
    }

    /**
     * Toggles a flag at the given index. The last flag will clear the field and end the game.
     *
     * @param index The index of the tile to toggle the flag.
     */
    fun toggleFlag(index: Int) {
        // TODO: what should happen if the user flags a tile before the game has been created?
        gameController.toggleFlag(index)
    }

    /**
     * Clears all adjacent tiles to the given index.
     *
     * @param index The index of the tile to clear adjacent tiles.
     */
    fun clearAdjacentTiles(index: Int) {
        gameController.clearAdjacentTiles(index)
    }

    /**
     * Gets the number of adjacent mines to the given index.
     *
     * @param index The index of the tile to check.
     *
     * @return The number of adjacent mines.
     */
    fun getAdjacentFlags(index: Int): Int {
        return gameController.countAdjacentFlags(index)
    }
}
