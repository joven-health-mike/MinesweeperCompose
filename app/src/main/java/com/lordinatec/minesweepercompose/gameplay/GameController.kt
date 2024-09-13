/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.apis.Coordinate
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import javax.inject.Inject

/**
 * GameController - handles all interactions between the view model and the model
 *
 * @param gameField - field to store game state
 * @param timer - timer to keep track of game time
 * @param eventPublisher - publisher to publish game events
 * @param xyIndexTranslator - translator to convert between xy and index
 *
 * @constructor Create empty Game controller
 */
class GameController @Inject constructor(
    private val gameField: Field,
    private val timer: Timer,
    private val eventPublisher: GameEventPublisher,
    private val xyIndexTranslator: CoordinateTranslator,
    private val coordinateFactory: CoordinateFactory
) {

    private var gameCreated: Boolean = false

    init {
        timer.onTickListener = Timer.OnTickListener { newTime ->
            eventPublisher.timeUpdate(newTime)
        }
        gameCreated = false
        gameField.reset()
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
            eventPublisher.publish(GameEvent.GameCreated)
            val (x, y) = xyIndexTranslator.indexToXY(index)
            gameField.createMines(x, y)
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
        println("Clearing everything")
        if (!gameCreated) return
        for ((index, coordinate) in gameField.fieldList.withIndex()) {
            if (!gameField.cleared.contains(coordinate)) {
                clear(index)
            }
        }
    }

    private fun getAdjacent(index: Int): Collection<Coordinate> {
        val position = gameField.fieldList[index]
        return gameField.adjacentCoordinates(position, coordinateFactory)
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
        println("Clearing adjacent tiles")
        if (!gameCreated) return
        val position = gameField.fieldList[index]
        val adjacentCoordinates = gameField.adjacentCoordinates(
            position, coordinateFactory
        ).filter { !gameField.isFlag(it.index) }
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
            gameField.isFlag(
                xyIndexTranslator.xyToIndex(
                    it.x(),
                    it.y()
                )
            )
        }
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        gameCreated = false
        eventPublisher.resetField()
        gameField.reset()
        timer.stop()
    }

    /**
     * Clears tile at given index
     *
     * @param index - index of the tile to clear
     */
    fun clear(index: Int) {
        if (!gameCreated) return
        if (gameField.isFlag(index)) return
        if (gameField.cleared.contains(gameField.fieldList[index])) return

        val isMine = gameField.clear(index)
        if (isMine) {
            timer.stop()
            eventPublisher.publish(GameEvent.PositionExploded(index))
            eventPublisher.gameLost()
        } else {
            val adjacent = getAdjacent(index).filter { coordinate ->
                val coordIndex = xyIndexTranslator.xyToIndex(coordinate.x(), coordinate.y())
                gameField.isMine(coordIndex)
            }.size
            eventPublisher.publish(GameEvent.PositionCleared(index, adjacent))
            if (adjacent == 0) {
                clearAdjacentTiles(index)
            }
            if (gameField.allClear()) {
                timer.stop()
                eventPublisher.gameWon(timer.time)
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
     * Maybe end the game if all mines are flagged
     */
    private fun maybeEndGame() {
        if (Config.feature_end_game_on_last_flag && gameField.flaggedAllMines()) {
            timer.stop()
            if (gameField.allFlagsCorrect()) {
                eventPublisher.gameWon(timer.time)
            } else {
                eventPublisher.gameLost()
            }
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
}
