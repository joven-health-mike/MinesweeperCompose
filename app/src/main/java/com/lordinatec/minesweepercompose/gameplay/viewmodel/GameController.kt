/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.apis.Coordinate
import com.lordinatec.minesweepercompose.gameplay.model.apis.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
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

    init {
        gameField.reset(DefaultConfiguration())
        timer.stop()
    }

    /**
     * Create a game. Mine will never occur at the given index.
     *
     * @param index - index of the initial clicked tile
     */
    fun maybeCreateGame(index: Int): Boolean {
        if (!gameCreated) {
            gameCreated = true
            gameOver = false
            eventPublisher.publish(GameEvent.GameCreated)
            gameField.createMines(index)
            timer.stop()
            timer.start()
            return true
        }
        return false
    }

    /**
     * Clears the entire field.
     */
    fun clearEverything() {
        if (!gameCreated) return
        for ((index) in gameField.fieldList.withIndex()) {
            clear(index)
        }
    }

    /**
     * Clears the entire field.
     */
    fun pauseTimer() {
        if (!gameCreated) return
        timer.pause()
    }

    /**
     * Clears the entire field.
     */
    fun resumeTimer() {
        if (!gameCreated) return
        timer.resume()
    }

    /**
     * Clears adjacent tiles to the given index
     *
     * @param index - index of the tile to clear adjacent tiles
     */
    fun clearAdjacentTiles(index: Int) {
        if (!gameCreated) return
        val adjacentCoordinates = getAdjacent(index).filter { !gameField.isFlag(it.index) }
        for (adjacentCoordinate in adjacentCoordinates) {
            if (!gameField.cleared.contains(adjacentCoordinate)) {
                clear(adjacentCoordinate.index)
            }
        }
    }

    /**
     * Count the number of adjacent flags to the given index
     *
     * @param index - index of the tile to count adjacent flags
     */
    fun countAdjacentFlags(index: Int): Int {
        if (!gameCreated) return -1
        return getAdjacent(index).count {
            gameField.isFlag(it.index)
        }
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        gameCreated = false
        gameOver = false
        eventPublisher.publish(GameEvent.FieldReset)
        gameField.reset(DefaultConfiguration())
        timer.stop()
    }

    /**
     * Clears tile at given index
     *
     * @param index - index of the tile to clear
     */
    fun clear(index: Int) {
        if (!gameCreated || gameField.isFlag(index) || gameField.cleared.contains(gameField.fieldList[index])) return

        val isMine = gameField.clear(index)
        if (isMine) {
            timer.pause()
            eventPublisher.publish(GameEvent.PositionExploded(index))
            if (!gameOver) {
                gameOver = true
                eventPublisher.publish(GameEvent.GameLost)
                clearEverything()
            }
        } else {
            val adjacent = getAdjacent(index).filter { coordinate ->
                gameField.isMine(coordinate.index)
            }.size
            eventPublisher.publish(GameEvent.PositionCleared(index, adjacent))
            if (adjacent == 0) {
                clearAdjacentTiles(index)
            }
            if (gameField.allClear()) {
                timer.pause()
                if (!gameOver) {
                    gameOver = true
                    eventPublisher.publish(GameEvent.GameWon(timer.time))
                }
                clearEverything()
            }
        }
    }

    /**
     * Toggles flag at given index
     *
     * @param index - index of the tile to toggle flag
     */
    fun toggleFlag(index: Int) {
        if (!gameCreated) return

        val unflagged = gameField.flag(index)
        if (unflagged) {
            eventPublisher.publish(GameEvent.PositionUnflagged(index))
        } else {
            eventPublisher.publish(GameEvent.PositionFlagged(index))
            maybeEndGame()
        }
    }

    /**
     * Checks if a flag is correct at the given index
     *
     * @param index - index of the tile to check
     */
    fun flagIsCorrect(index: Int): Boolean {
        if (!gameCreated) return false

        return gameField.isMine(index)
    }

    /**
     * Maybe end the game if all mines are flagged
     */
    private fun maybeEndGame() {
        if (Config.feature_end_game_on_last_flag && gameField.flaggedAllMines()) {
            timer.pause()
            if (gameOver) return
            gameOver = true
            if (gameField.allFlagsCorrect()) {
                eventPublisher.publish(GameEvent.GameWon(timer.time))
            } else {
                eventPublisher.publish(GameEvent.GameLost)
            }
            clearEverything()
        }
    }

    private fun getAdjacent(index: Int): Collection<Coordinate> {
        return gameField.adjacentCoordinates(index)
    }
}
