/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

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
) : GameControlStrategy {
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
        val adjacentMines = adjacentMines(position)
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

    // TODO: extract this to a separate class
    enum class AdjacentTile(val transX: Int, val transY: Int) {
        TOP_LEFT(-1, -1),
        TOP(0, -1),
        TOP_RIGHT(1, -1),
        LEFT(-1, 0),
        RIGHT(1, 0),
        BOTTOM_LEFT(-1, 1),
        BOTTOM(0, 1),
        BOTTOM_RIGHT(1, 1)
    }

    // TODO: extract this to a separate class
    private fun adjacentMines(position: Position): Int {
        var count = 0
        for (adjacent in AdjacentTile.entries) {
            val x = position.x() + adjacent.transX
            val y = position.y() + adjacent.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height()) {
                val newPosition = positionPool.atLocation(x, y)
                if (game.field().isMine(newPosition)) {
                    count++
                }
            }
        }
        return count
    }

    // TODO: extract this to a separate class
    fun countAdjacentFlags(origX: Int, origY: Int): Int {
        var result = 0

        for (adjacentTile in AdjacentTile.entries) {
            val x = origX + adjacentTile.transX
            val y = origY + adjacentTile.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height() && game.field()
                    ?.isFlag(positionPool.atLocation(x, y))!!
            ) {
                result++
            }
        }

        return result
    }

    // TODO: extract this to a separate class
    fun clearAdjacentTiles(origX: Int, origY: Int) {
        for (adjacentTile in AdjacentTile.entries) {
            val x = origX + adjacentTile.transX
            val y = origY + adjacentTile.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height() && !game.field()
                    ?.isFlag(positionPool.atLocation(x, y))!!
            ) {
                clear(x, y)
            }
        }
    }

    // TODO: simplify
    override fun toggleFlag(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        val isFlag = game.field().flag(position)
        if (isFlag) {
            listener?.positionUnflagged(x, y)
            flagged.remove(position)
        } else {
            listener?.positionFlagged(x, y)
            flagged.add(position)
            if (flagged.size == numMines) {
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
