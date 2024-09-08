/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.api.model.Position

/**
 * Implementation of GameControlStrategy that is designed to be used in an Android
 *
 * @param game the game to control
 * @param positionPool the pool of positions
 * @param numMines the number of mines in the game
 * @param listener the listener to notify of game events
 */
class AndroidGameControlStrategy(
    private val field: AndroidField,
    private val positionPool: AndroidPositionPool,
    private val numMines: Int,
    private val timer: Timer,
    private val eventListener: GameEventPublisher,
    private val adjacentHelper: AdjacentHelper
) : GameControlStrategy {
    private var gameOver = false
    private var cleared = mutableSetOf<Position>()
    private var flagged = mutableSetOf<Position>()

    override fun clear(x: Int, y: Int) {
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

    private fun handleClearSuccess(position: Position) {
        val adjacentMines = adjacentHelper.countAdjacentMines(position)
        cleared.add(position)
        eventListener.positionCleared(position.x(), position.y(), adjacentMines)
        if (adjacentMines == 0) {
            clearAdjacentTiles(position.x(), position.y())
        }
        if (cleared.size == positionPool.size() - numMines) {
            eventListener.gameWon(timer.time)
        }
    }

    private fun handleClearExploded(position: Position) {
        gameOver = true
        eventListener.positionExploded(position.x(), position.y())
        eventListener.gameLost()
    }

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

    fun countAdjacentFlags(origX: Int, origY: Int): Int {
        return adjacentHelper.countAdjacentFlags(origX, origY)
    }

    override fun toggleFlag(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        val isFlag = field.flag(position)
        if (isFlag) {
            unflag(position)
        } else {
            flag(position)
            maybeEndGame()
        }
    }

    private fun maybeEndGame() {
        if (Config.feature_end_game_on_last_flag && flagged.size == numMines) {
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

    private fun flag(position: Position) {
        eventListener.positionFlagged(position.x(), position.y())
        flagged.add(position)
    }

    private fun unflag(position: Position) {
        eventListener.positionUnflagged(position.x(), position.y())
        flagged.remove(position)
    }

    override fun saveGame(fileName: String?) {
        TODO("Not yet implemented")
    }

    override fun restoreGame(fileName: String?) {
        TODO("Not yet implemented")
    }

    override fun setListener(p0: GameControlStrategy.Listener?) {
        TODO("Not yet implemented")
    }

    override fun isGameOver(): Boolean {
        return gameOver
    }
}
