/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameListenerBridge
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState
import com.lordinatec.minesweepercompose.minesweeper.apis.util.clickAdjacentPositions
import com.lordinatec.minesweepercompose.minesweeper.apis.util.countAdjacent
import com.lordinatec.minesweepercompose.minesweeper.apis.util.getAdjacent
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Game screen.
 *
 * @param application The application context.
 * @param config The configuration object.
 * @param gameControllerFactory The factory for creating a GameController.
 *
 * @return A ViewModel for the Game screen.
 */
class GameViewModel(
    private val application: Application,
    private val config: Config = Config,
    gameControllerFactory: GameController.Factory,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    /**
     * Update timeValue when time is updated.
     */
    private val onTimeUpdate: (newTime: Long) -> Unit = {
        _uiState.update { currentState ->
            currentState.copy(
                timeValue = it
            )
        }
    }

    /**
     * Update the game state when the game is won.
     *
     * This will clear the entire field and stop the timer.
     */
    private val gameWon: () -> Unit = {
        clearEverything()
        gameController.cancelTimer()
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                winner = true,
            )
        }
    }

    /**
     * Update the game state when the game is lost.
     *
     * This will clear the entire field and stop the timer.
     */
    private val gameLost: () -> Unit = {
        clearEverything()
        gameController.cancelTimer()
        _uiState.update { currentState ->
            currentState.copy(
                gameOver = true,
                winner = false,
            )
        }
    }

    /**
     * Handle game events through the GameListenerBridge.
     */
    private val gamePlayListener: GameListenerBridge = GameListenerBridge(
        onTimeUpdate = onTimeUpdate,
        onPositionCleared = { index, numOfAdjacent ->
            // when position is cleared, clear the position and update the value
            updatePosition(
                index,
                TileState.CLEARED,
                if (numOfAdjacent == 0) "" else numOfAdjacent.toString()
            )
            // if no adjacent mines, click all adjacent positions
            if (numOfAdjacent == 0) clickAdjacentPositions(model, index)
            // calculate % chance for each covered tile
            calculateMineLikelihoods()
            // check if game is won
            if (!_uiState.value.gameOver && assessWinConditions()) gameWon()
        },
        onPositionExploded = { index ->
            // when position is exploded, explode the position and update the value
            updatePosition(index, TileState.EXPLODED, "*")
            // check if game is lost
            if (!uiState.value.gameOver) gameLost()
        },
        onPositionFlagged = { index ->
            // when position is flagged, flag the position and update the value
            updatePosition(index, TileState.FLAGGED, "F")
            // decrease number of mines remaining
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining - 1
                )
            }
            // calculate % chance for each covered tile
            calculateMineLikelihoods()
        },
        onPositionUnflagged = { index ->
            // when position is unflagged, unflag the position and update the value
            updatePosition(index, TileState.COVERED, "")
            // increase number of mines remaining
            _uiState.update { currentState ->
                currentState.copy(
                    minesRemaining = currentState.minesRemaining + 1
                )
            }
            // calculate % chance for each covered tile
            calculateMineLikelihoods()
        },
        onGameWon = gameWon,
        onGameLost = gameLost
    )

    private val gameController = gameControllerFactory.createGameController(gamePlayListener)
    private val model = this

    /* PUBLIC APIS */
    /**
     * Clear the game state and reset the game.
     */
    fun resetGame() {
        _uiState.update { GameState() }
        gameController.resetGame()
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
        // if game is not created, create the game
        if (!gameController.gameCreated) createGame(index)
        gameController.clear(index)
    }

    /**
     * Toggles a flag at the given index. The last flag will clear the field and end the game.
     *
     * @param index The index of the tile to toggle the flag.
     */
    fun toggleFlag(index: Int) {
        gameController.toggleFlag(index)

        // TODO: Wrap in feature flag
        // if all mines are flagged, clear all other tiles
        if (_uiState.value.tileStates.count { it == TileState.FLAGGED } == config.MINES) {
            clearEverything()
        }
    }

    /**
     * Ensures the given coordinates are in range of the field.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     *
     * @return True if the coordinates are in range, false otherwise.
     */
    // TODO: Move this somewhere else
    fun validCoordinates(x: Int, y: Int): Boolean {
        return x in 0 until config.WIDTH && y in 0 until config.HEIGHT
    }

    /**
     * Checks if the tile at the given index is in the given state.
     *
     * @param index The index of the tile to check.
     * @param tileState The state to check for.
     *
     * @return True if the tile is in the given state, false otherwise.
     */
    fun positionIs(index: Int, tileState: TileState): Boolean {
        return _uiState.value.tileStates[index] == tileState
    }

    /**
     * Clears all adjacent tiles to the given index.
     *
     * @param index The index of the tile to clear adjacent tiles.
     */
    fun clearAdjacentTiles(index: Int) {
        clickAdjacentPositions(model, index)
    }

    /**
     * Gets the number of adjacent mines to the given index.
     *
     * @param index The index of the tile to check.
     *
     * @return The number of adjacent mines.
     */
    fun getAdjacentFlags(index: Int): Int {
        return countAdjacent(model, index, TileState.FLAGGED)
    }

    /**
     * Pauses the timer.
     */
    fun pauseTimer() {
        if (gameController.gameCreated && !_uiState.value.gameOver) {
            gameController.pauseTimer()
        }
    }

    /**
     * Resumes the timer.
     */
    fun resumeTimer() {
        if (gameController.gameCreated && !_uiState.value.gameOver) {
            gameController.resumeTimer()
        }
    }

    /* PRIVATE FUNCTIONS */
    /**
     * Creates a new game. The given index is guaranteed to NOT be a mine.
     *
     * This function resets the game state and starts the timer.
     *
     * @param index The index of the first tile to clear.
     */
    private fun createGame(index: Int) {
        gameController.createGame(index)
        _uiState.update { currentState ->
            currentState.copy(
                newGame = false
            )
        }
        gameController.startTimer()
    }

    /**
     * Clears all covered tiles.
     */
    private fun clearEverything() {
        _uiState.value.tileStates
            .withIndex()
            .filter { it.value == TileState.COVERED }
            .forEach { clear(it.index) }
    }

    /**
     * Checks if the win conditions are met.
     *
     * @return True if the win conditions are met, false otherwise.
     */
    private fun assessWinConditions(): Boolean {
        return _uiState.value.tileStates.count {
            it == TileState.COVERED || it == TileState.FLAGGED
        } == config.MINES
    }

    /**
     * Updates the position at the given index.
     *
     * @param index The index of the tile to update.
     * @param tileState The new state of the tile.
     * @param value The new value of the tile.
     */
    private fun updatePosition(index: Int, tileState: TileState, value: String) {
        _uiState.update {
            it.copy(
                tileStates = it.tileStates.toMutableList().apply { this[index] = tileState },
                tileValues = it.tileValues.toMutableList().apply { this[index] = value }
            )
        }
    }

    /**
     * Calculates the likelihood of a mine for each covered tile.
     *
     * This function is EXPERIMENTAL and may not be accurate.
     *
     * Feature Flag: SHOW_COVERED_CHANCES
     */
    private fun calculateMineLikelihoods() {
        if (!config.FEATURES.SHOW_COVERED_CHANCES) return

        // TODO: simplify
        // TODO: for tiles that have multiple adjacent cleared tiles, keep the lowest % available
        for (i in 0 until config.WIDTH * config.HEIGHT) {
            val tileState = _uiState.value.tileStates[i]
            if (tileState != TileState.COVERED) continue

            val adjacent = getAdjacent(model, i)

            // Check if any adjacent tile is cleared
            if (adjacent.any {
                    _uiState.value.tileStates[it] == TileState.CLEARED
                }) {
                adjacent.forEach {
                    if (_uiState.value.tileStates[it] == TileState.CLEARED) {
                        val tileValue = _uiState.value.tileValues[it]
                        val tileLongValue = tileValue.toLongOrNull() ?: 0L

                        val adjFlags = countAdjacent(model, it, TileState.FLAGGED)
                        val adjCovered = countAdjacent(model, it, TileState.COVERED)
                        if (adjCovered == 0) return@forEach

                        val chance = ((tileLongValue - adjFlags).toFloat() / adjCovered) * 100
                        _uiState.update { currentState ->
                            currentState.copy(
                                tileValues = currentState.tileValues.toMutableList().apply {
                                    this[i] = "$chance%"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}