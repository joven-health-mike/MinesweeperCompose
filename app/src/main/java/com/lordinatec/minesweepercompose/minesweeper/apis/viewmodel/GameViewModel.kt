/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lordinatec.minesweepercompose.minesweeper.apis.model.EventPublisher
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameController
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameEvent
import com.lordinatec.minesweepercompose.minesweeper.apis.model.GameState
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
 * @param gameEvents The GameEventPublisher to use.
 *
 * @return A ViewModel for the Game screen.
 */
class GameViewModel(
    private val gameController: GameController,
    private val gameEvents: EventPublisher
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
                        if (event.adjacentMines == 0) "" else event.adjacentMines.toString()
                    )

                    is GameEvent.PositionExploded -> updatePosition(
                        event.index,
                        TileState.EXPLODED,
                        "*"
                    )

                    is GameEvent.PositionFlagged -> updatePosition(
                        event.index,
                        TileState.FLAGGED,
                        "F"
                    )

                    is GameEvent.PositionUnflagged -> updatePosition(
                        event.index,
                        TileState.COVERED,
                        ""
                    )

                    is GameEvent.GameWon -> gameWon()
                    is GameEvent.GameLost -> gameLost()
                    is GameEvent.GameCreated -> {
                        gameController.startTimer()
                        _uiState.update { state ->
                            state.copy(
                                newGame = false
                            )
                        }
                    }
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
        if (!uiState.value.gameOver) {
            gameController.clearEverything()
            gameController.stopTimer()
            _uiState.update { state ->
                state.copy(
                    gameOver = true,
                    winner = true,
                )
            }
        }
    }

    /**
     * Update the game state when the game is lost.
     *
     * This will clear the entire field and stop the timer.
     */
    private val gameLost: () -> Unit = {
        if (!uiState.value.gameOver) {
            gameController.clearEverything()
            gameController.stopTimer()
            _uiState.update { state ->
                state.copy(
                    gameOver = true,
                    winner = false,
                )
            }
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
        gameController.maybeCreateGame(index)
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

    /**
     * Pauses the timer.
     */
    fun pauseTimer() {
        gameController.pauseTimer()
    }

    /**
     * Resumes the timer.
     */
    fun resumeTimer() {
        gameController.resumeTimer()
    }

    /* PRIVATE FUNCTIONS */
    /**
     * Updates the position at the given index.
     *
     * @param index The index of the tile to update.
     * @param tileState The new state of the tile.
     * @param value The new value of the tile.
     */
    private fun updatePosition(index: Int, tileState: TileState, value: String) {
        _uiState.update { state ->
            var newMinesValue = state.minesRemaining
            if (tileState == TileState.FLAGGED) {
                newMinesValue--
            } else if (tileState == TileState.COVERED) {
                newMinesValue++
            }
            state.copy(
                tileStates = state.tileStates.toMutableList().apply { this[index] = tileState },
                tileValues = state.tileValues.toMutableList().apply { this[index] = value },
                minesRemaining = newMinesValue
            )
        }
    }
}