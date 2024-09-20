/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.Field
import com.lordinatec.minesweepercompose.gameplay.model.FieldIndex
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import javax.inject.Inject

/**
 * GameController - handles all interactions between the view model and the model
 *
 * @param gameField - field to store game state
 * @param timer - timer to keep track of game time
 * @param eventPublisher - publisher to publish game events
 *
 * @constructor Create empty Game controller
 */
class GameController @Inject constructor(
    private val gameField: Field,
    private val timer: Timer,
    private val eventPublisher: GameEventPublisher,
) {

    private var gameCreated: Boolean = false
    private var gameOver: Boolean = false

    /**
     * Create a game.
     *
     * @return Boolean - true if the game was created
     */
    fun maybeCreateGame() = maybeCreateGame(gameField.fieldIndexRange().random())

    /**
     * Create a game. Mine will never occur at the given index.
     *
     * @param index - index of the initial clicked tile
     *
     * @return Boolean - true if the game was created
     */
    fun maybeCreateGame(index: FieldIndex): Boolean {
        if (gameCreated) return false

        gameCreated = true
        gameOver = false
        eventPublisher.publish(GameEvent.GameCreated)
        gameField.createMines(index)
        return true
    }

    /**
     * Clears the entire field.
     */
    fun clearEverything() {
        if (gameCreated) {
            gameField.fieldIndexRange()
                .forEach { clear(it) }
        }
    }

    /**
     * Clears adjacent tiles to the given index
     *
     * @param index - index of the tile to clear adjacent tiles
     */
    fun clearAdjacentTiles(index: FieldIndex) {
        if (gameCreated) {
            getAdjacent(index)
                .filterNot { gameField.isFlag(it) }
                .forEach { clear(it) }
        }
    }

    /**
     * Count the number of adjacent flags to the given index
     *
     * @param index - index of the tile to count adjacent flags
     */
    fun countAdjacentFlags(index: FieldIndex) =
        if (!gameCreated) -1 else getAdjacent(index).count { gameField.isFlag(it) }

    /**
     * Reset the game
     */
    fun resetGame() {
        gameCreated = false
        gameOver = false
        eventPublisher.publish(GameEvent.FieldReset)
        gameField.reset(DefaultConfiguration())
    }

    /**
     * Clear all non-mines
     */
    fun clearNonMines() = gameField.fieldIndexRange()
        .filterNot { gameField.isMine(it) }
        .forEach { clear(it) }

    /**
     * Clears tile at given index
     *
     * @param index - index of the tile to clear
     */
    fun clear(index: FieldIndex) {
        if (shouldClear(index)) {
            if (gameField.clear(index)) {
                handleMineCleared(index)
            } else {
                handleSafeCleared(index)
            }
        }
    }

    /**
     * Toggles flag at given index
     *
     * @param index - index of the tile to toggle flag
     */
    fun toggleFlag(index: FieldIndex) {
        if (gameCreated) {
            val flagged = !gameField.flag(index)
            val event =
                if (flagged) GameEvent.PositionFlagged(index) else GameEvent.PositionUnflagged(index)

            eventPublisher.publish(event)

            if (flagged) maybeEndGame()
        }
    }

    /**
     * Checks if a flag is correct at the given index
     *
     * @param index - index of the tile to check
     */
    fun flagIsCorrect(index: FieldIndex) = gameCreated && gameField.isMine(index)

    /**
     * Maybe end the game if all mines are flagged
     */
    private fun maybeEndGame() {
        if (Config.feature_end_game_on_last_flag && gameField.flaggedAllMines() && !gameOver) {
            gameOver = true

            val event = if (gameField.allFlagsCorrect()) {
                GameEvent.GameWon(timer.time)
            } else {
                GameEvent.GameLost
            }

            eventPublisher.publish(event)
            clearEverything()
        }
    }

    /**
     * Get adjacent coordinates to the given index
     *
     * @param index - index of the tile to get adjacent coordinates
     *
     * @return Collection<Coordinate> - collection of adjacent coordinates
     */
    private fun getAdjacent(index: FieldIndex): Collection<FieldIndex> =
        if (gameCreated) gameField.adjacentFieldIndexes(index) else emptyList()

    /**
     * Check if the tile at the given index should be cleared
     *
     * @param index - index of the tile to check
     *
     * @return Boolean - true if the tile should be cleared
     */
    private fun shouldClear(index: FieldIndex): Boolean =
        gameCreated && !gameField.isFlag(index) && !gameField.cleared.contains(index)

    /**
     * Handle mine cleared tile at the given index. Publishes PositionExploded event and clears the field
     *
     * @param index - index of the tile that was cleared
     *
     * @return Boolean - true if the tile should be cleared
     */
    private fun handleMineCleared(index: FieldIndex) {
        eventPublisher.publish(GameEvent.PositionExploded(index))
        if (!gameOver) {
            gameOver = true
            eventPublisher.publish(GameEvent.GameLost)
            clearEverything()
        }
    }

    /**
     * Handle safe cleared tile at the given index. Publishes PositionCleared event and clears adjacent tiles
     *
     * @param index - index of the tile that was cleared
     *
     * @return Boolean - true if the tile should be cleared
     */
    private fun handleSafeCleared(index: FieldIndex) {
        val adjacentMines = getAdjacent(index).count { gameField.isMine(it) }

        eventPublisher.publish(GameEvent.PositionCleared(index, adjacentMines))

        if (adjacentMines == 0) clearAdjacentTiles(index)

        if (gameField.allClear()) handleGameWon()
    }

    /**
     * Handle game won. Publishes GameWon event and clears the field
     */
    private fun handleGameWon() {
        if (!gameOver) {
            gameOver = true
            eventPublisher.publish(GameEvent.GameWon(timer.time))
        }
        clearEverything()
    }
}
