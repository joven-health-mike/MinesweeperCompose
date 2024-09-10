/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import com.mikeburke106.mines.api.model.Position
import javax.inject.Inject

/**
 * Implementation of GameControlStrategy that is designed to be used in an Android
 *
 * @param field - the field to control
 * @param positionPool - the pool of positions
 * @param numMines - the number of mines
 * @param timer - the timer
 * @param eventListener - the listener for game events
 * @param adjacentHelper - the helper for adjacent tiles
 *
 * @constructor Create a new AndroidGameControlStrategy
 */
class AndroidGameControlStrategy @Inject constructor(
    private val field: AndroidField,
    private val positionPool: AndroidPositionPool,
    private val numMines: Int,
    private val timer: Timer,
    private val eventListener: GameEventPublisher,
    private val adjacentHelper: AdjacentHelper
) {
    private var gameOver = false
    private var cleared = mutableSetOf<Position>()
    private var flagged = mutableSetOf<Position>()

    /**
     * Clear a tile at the given x,y coordinates.
     *
     * @param x - the x coordinate of the tile to clear
     * @param y - the y coordinate of the tile to clear
     */
    fun clear(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        if (field.isFlag(position)) {
            return
        }
        val isMine = field.clear(position)
        if (isMine) {
            handleClearExploded(position)
        } else if (!cleared.contains(position)) {
            handleClearSuccess(position)
        }
    }

    /**
     * Clear all adjacent tiles to the given x,y coordinates
     *
     * @param origX - the x coordinate of the tile to clear adjacent tiles
     * @param origY - the y coordinate of the tile to clear adjacent tiles
     */
    fun clearAdjacentTiles(origX: Int, origY: Int) {
        for (adjacentPosition in adjacentHelper.getAdjacentTiles(origX, origY)) {
            val x = adjacentPosition.x()
            val y = adjacentPosition.y()
            val translatedPosition = positionPool.atLocation(x, y)
            if (!cleared.contains(translatedPosition)) {
                clear(x, y)
            }
        }
    }

    /**
     * Count the number of adjacent flags to the given x,y coordinates
     *
     * @param origX - the x coordinate of the tile to count adjacent flags
     * @param origY - the y coordinate of the tile to count adjacent flags
     *
     * @return the number of adjacent flags
     */
    fun countAdjacentFlags(origX: Int, origY: Int): Int {
        return adjacentHelper.countAdjacentFlags(origX, origY)
    }

    /**
     * Toggle the flag at the given x,y coordinates
     *
     * @param x - the x coordinate of the tile to toggle the flag
     * @param y - the y coordinate of the tile to toggle the flag
     */
    fun toggleFlag(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        val isFlag = field.flag(position)
        if (isFlag) {
            unflag(position)
        } else {
            flag(position)
            maybeEndGame()
        }
    }

    /**
     * Reset the game
     */
    fun resetGame() {
        cleared.clear()
        flagged.clear()
        gameOver = false
    }

    /**
     * Handle a successful clear of a tile
     *
     * @param position - the position of the cleared tile
     */
    private fun handleClearSuccess(position: Position) {
        val adjacentMines = adjacentHelper.countAdjacentMines(position)
        cleared.add(position)
        eventListener.positionCleared(position.x(), position.y(), adjacentMines)
        if (adjacentMines == 0) {
            clearAdjacentTiles(position.x(), position.y())
        }
        if (cleared.size == positionPool.size() - numMines) {
            gameOver = true
            eventListener.gameWon(timer.time)
        }
    }

    /**
     * Handle a clear that exploded a mine
     *
     * @param position - the position of the exploded mine
     */
    private fun handleClearExploded(position: Position) {
        gameOver = true
        eventListener.positionExploded(position.x(), position.y())
        eventListener.gameLost()
    }

    /**
     * Maybe end the game if all mines are flagged
     */
    private fun maybeEndGame() {
        if (Config.feature_end_game_on_last_flag && flagged.size == numMines) {
            gameOver = true
            var gameWon = true
            for (flaggedPosition in flagged) {
                if (!field.isMine(flaggedPosition)) {
                    gameWon = false
                }
            }
            if (gameWon) {
                eventListener.gameWon(timer.time)
            } else {
                eventListener.gameLost()
            }
        }
    }

    /**
     * Flag the position and send the event
     *
     * @param position - the position to flag
     */
    private fun flag(position: Position) {
        eventListener.positionFlagged(position.x(), position.y())
        flagged.add(position)
    }

    /**
     * Unflag the position and send the event
     *
     * @param position - the position to unflag
     */
    private fun unflag(position: Position) {
        eventListener.positionUnflagged(position.x(), position.y())
        flagged.remove(position)
    }
}
