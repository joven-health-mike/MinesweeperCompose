/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.views.TileState
import com.lordinatec.minesweepercompose.gameplay.views.TileValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Consumes game events and updates the game state.
 *
 * @param eventProvider The provider for game events.
 *
 * @constructor Creates a new GameStateEventConsumer.
 */
class GameStateEventConsumer @Inject constructor(
    private val eventProvider: EventProvider
) {

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    suspend fun consume() {
        eventProvider.eventFlow.collect { event -> consume(event as GameEvent) }
    }

    private fun consume(event: GameEvent) {
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
                        gameOver = false,
                    )
                }
            }

            is GameEvent.FieldReset -> {
                _uiState.update { GameState() }
            }
        }
    }

    /**
     * Update the size of the game.
     */
    fun updateSize() {
        if (uiState.value.tileStates.size != Config.width * Config.height) {
            _uiState.update { GameState() }
        }
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
            val newMinesValue = when (tileState) {
                TileState.FLAGGED -> state.minesRemaining - 1
                TileState.COVERED -> state.minesRemaining + 1
                else -> state.minesRemaining
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
     */
    private val gameWon: () -> Unit = {
        if (!uiState.value.gameOver) {
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
     */
    private val gameLost: () -> Unit = {
        if (!uiState.value.gameOver) {
            _uiState.update { state ->
                state.copy(
                    gameOver = true,
                    winner = false,
                )
            }
        }
    }
}
