/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import androidx.lifecycle.ViewModel
import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.GameController
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.apis.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import com.lordinatec.minesweepercompose.gameplay.views.TileState
import com.lordinatec.minesweepercompose.gameplay.views.TileValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Game screen.
 *
 * @param gameController The controller for the game.
 * @param gameEvents The event publisher for the game.
 * @param field The field for the game.
 *
 * @return A ViewModel for the Game screen.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameController: GameController,
    private val gameEvents: GameEventPublisher,
    private val field: Field,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        // listen for game events
        gameEvents.publisherScope.launch {
            gameEvents.events.collect { event ->
                when (event) {
                    is GameEvent.TimeUpdate -> {
                        _uiState.update { state ->
                            state.copy(
                                timeValue = event.newTime
                            )
                        }
                    }

                    is GameEvent.PositionCleared -> updatePosition(
                        event.index, TileState.CLEARED, TileValue.fromValue(event.adjacentMines)
                    )

                    is GameEvent.PositionExploded -> updatePosition(
                        event.index, TileState.EXPLODED, TileValue.MINE
                    )

                    is GameEvent.PositionFlagged -> updatePosition(
                        event.index, TileState.FLAGGED, TileValue.FLAG
                    )

                    is GameEvent.PositionUnflagged -> updatePosition(
                        event.index, TileState.COVERED, TileValue.UNKNOWN
                    )

                    is GameEvent.GameWon -> gameWon()
                    is GameEvent.GameLost -> gameLost()
                    is GameEvent.GameCreated -> {
                        _uiState.update { state ->
                            state.copy(
                                newGame = false,
                                tileStates = List(Config.width * Config.height) { TileState.COVERED },
                                tileValues = List(Config.width * Config.height) { TileValue.UNKNOWN }
                            )
                        }
                    }
                }
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
     * Update the size of the game.
     */
    fun updateSize() {
        if (uiState.value.tileStates.size != Config.width * Config.height) {
            _uiState.update { state ->
                state.copy(
                    tileStates = List(Config.width * Config.height) { TileState.COVERED },
                    tileValues = List(Config.width * Config.height) { TileValue.UNKNOWN }
                )
            }
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
        val newGame = gameController.maybeCreateGame(index)
        if (newGame) {
            field.updateConfiguration(DefaultConfiguration())
            updateSize()
            _uiState.update { state ->
                state.copy(
                    newGame = true,
                    gameOver = false
                )
            }
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

    /* PRIVATE FUNCTIONS */
    /**
     * Updates the position at the given index.
     *
     * @param index The index of the tile to update.
     * @param tileState The new state of the tile.
     * @param tileValue The new value of the tile.
     */
    private fun updatePosition(index: Int, tileState: TileState, tileValue: TileValue) {
        _uiState.update { state ->
            var newMinesValue = state.minesRemaining
            if (tileState == TileState.FLAGGED) {
                newMinesValue--
            } else if (tileState == TileState.COVERED) {
                newMinesValue++
            }
            state.copy(
                tileStates = state.tileStates.toMutableList().apply { this[index] = tileState },
                tileValues = state.tileValues.toMutableList().apply { this[index] = tileValue },
                minesRemaining = newMinesValue
            )
        }
    }

    /**
     * Update the game state when the game is won.
     *
     * This will clear the entire field and stop the timer.
     */
    private val gameWon: () -> Unit = {
        gameController.clearEverything()
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
        gameController.clearEverything()
        _uiState.update { state ->
            state.copy(
                gameOver = true,
                winner = false,
            )
        }
    }
}
