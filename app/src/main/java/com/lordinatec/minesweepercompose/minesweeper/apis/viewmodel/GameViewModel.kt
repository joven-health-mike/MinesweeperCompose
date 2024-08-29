/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameEvent
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameEventPublisher
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState
import com.lordinatec.minesweepercompose.minesweeper.apis.util.clickAdjacentPositions
import com.lordinatec.minesweepercompose.minesweeper.apis.util.countAdjacent
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the Game screen.
 *
 * @param gameController The GameController to use.
 *
 * @return A ViewModel for the Game screen.
 */
class GameViewModel(
    private val gameController: GameController,
    private val gameEvents: GameEventPublisher
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gameEvents.events.collect { event ->
                when (event) {
                    is GameEvent.TimeUpdate -> onTimeUpdate(event.newTime)
                    is GameEvent.PositionCleared -> updatePosition(
                        event.index,
                        TileState.CLEARED,
                        event.adjacentMines.toString()
                    )

                    is GameEvent.PositionExploded -> updatePosition(
                        event.index,
                        TileState.EXPLODED,
                        ""
                    )

                    is GameEvent.PositionFlagged -> updatePosition(
                        event.index,
                        TileState.FLAGGED,
                        ""
                    )

                    is GameEvent.PositionUnflagged -> updatePosition(
                        event.index,
                        TileState.COVERED,
                        ""
                    )

                    is GameEvent.GameWon -> gameWon()
                    is GameEvent.GameLost -> gameLost()
                }
            }
        }
    }

    /**
     * Update timeValue when time is updated.
     */
    private var onTimeUpdate: (newTime: Long) -> Unit = {
        _uiState.update { state ->
            state.copy(
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
        _uiState.update { state ->
            state.copy(
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
        _uiState.update { state ->
            state.copy(
                gameOver = true,
                winner = false,
            )
        }
    }

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
        return x in 0 until Config.WIDTH && y in 0 until Config.HEIGHT
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
        clickAdjacentPositions(this, index)
    }

    /**
     * Gets the number of adjacent mines to the given index.
     *
     * @param index The index of the tile to check.
     *
     * @return The number of adjacent mines.
     */
    fun getAdjacentFlags(index: Int): Int {
        return countAdjacent(this, index, TileState.FLAGGED)
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
        _uiState.update { state ->
            state.copy(
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
        } == Config.MINES
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
}