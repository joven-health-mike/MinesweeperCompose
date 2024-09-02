/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.Config
import com.mikeburke106.mines.api.model.Game
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
    private val game: Game,
    private val positionPool: Position.Pool,
    private val numMines: Int,
    private var listener: GameControlStrategy.Listener? = null
) : GameControlStrategy, AdjacentHelper by AdjacentHelperImpl(game.field(), positionPool) {
    private var gameOver = false
    private var cleared = mutableSetOf<Position>()
    private var flagged = mutableSetOf<Position>()

    override fun clear(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        if (game.field().isFlag(position)) {
            return
        }
        val isMine = game.field().clear(position)
        if (isMine) {
            handleClearExploded(position)
        } else if (!cleared.contains(position)) {
            handleClearSuccess(position)
        }
    }

    private fun handleClearSuccess(position: Position) {
        val adjacentMines = countAdjacentMines(position)
        cleared.add(position)
        listener?.positionCleared(position.x(), position.y(), adjacentMines)
        if (adjacentMines == 0) {
            clearAdjacentTiles(position.x(), position.y())
        }
        if (cleared.size == positionPool.size() - numMines) {
            listener?.gameWon()
        }
    }

    private fun handleClearExploded(position: Position) {
        gameOver = true
        listener?.positionExploded(position.x(), position.y())
        listener?.gameLost()
    }

    fun clearAdjacentTiles(origX: Int, origY: Int) {
        for (adjacentPosition in getAdjacentTiles(origX, origY)) {
            val x = adjacentPosition.x()
            val y = adjacentPosition.y()
            if (!cleared.contains(adjacentPosition)) {
                clear(x, y)
            }
        }
    }

    override fun toggleFlag(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        val isFlag = game.field().flag(position)
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
                if (!game.field().isMine(flaggedPosition)) {
                    gameWon = false
                }
            }
            if (gameWon) {
                listener?.gameWon()
            } else {
                listener?.gameLost()
            }
        }
    }

    private fun flag(position: Position) {
        listener?.positionFlagged(position.x(), position.y())
        flagged.add(position)
    }

    private fun unflag(position: Position) {
        listener?.positionUnflagged(position.x(), position.y())
        flagged.remove(position)
    }

    override fun saveGame(fileName: String?) {
        TODO("Not yet implemented")
    }

    override fun restoreGame(fileName: String?) {
        TODO("Not yet implemented")
    }

    override fun setListener(listener: GameControlStrategy.Listener?) {
        this.listener = listener
    }

    override fun isGameOver(): Boolean {
        return gameOver
    }
}
