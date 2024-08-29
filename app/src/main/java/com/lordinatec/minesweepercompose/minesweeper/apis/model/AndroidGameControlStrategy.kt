/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.mikeburke106.mines.api.model.Game
import com.mikeburke106.mines.api.model.GameControlStrategy
import com.mikeburke106.mines.api.model.Position

class AndroidGameControlStrategy(
    private val game: Game,
    private val positionPool: Position.Pool,
    private var listener: GameControlStrategy.Listener? = null
) : GameControlStrategy {
    private var gameOver = false
    private var cleared = mutableSetOf<Position>()

    override fun clear(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        if (game.field().isFlag(position)) {
            return
        }
        val isMine = game.field().clear(position)
        if (isMine) {
            gameOver = true
            listener?.positionExploded(x, y)
            listener?.gameLost()
        } else if (!cleared.contains(position)) {
            val adjacentMines = adjacentMines(position)
            cleared.add(position)
            listener?.positionCleared(x, y, adjacentMines)
            if (adjacentMines == 0) {
                clearAdjacentTiles(position.x(), position.y())
            }
        }
    }

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

    fun countAdjacentFlags(origX: Int, origY: Int): Int {
        var result = 0

        for (adjacentTile in AdjacentTile.entries) {
            val x = origX + adjacentTile.transX
            val y = origY + adjacentTile.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height()) {
                if (game.field()?.isFlag(positionPool.atLocation(x, y))!!) {
                    result++
                }
            }
        }

        return result
    }

    fun clearAdjacentTiles(origX: Int, origY: Int) {
        for (adjacentTile in AdjacentTile.entries) {
            val x = origX + adjacentTile.transX
            val y = origY + adjacentTile.transY
            if (x >= 0 && x < positionPool.width() && y >= 0 && y < positionPool.height()) {
                if (!game.field()?.isFlag(positionPool.atLocation(x, y))!!) {
                    clear(x, y)
                }
            }
        }
    }

    override fun toggleFlag(x: Int, y: Int) {
        val position = positionPool.atLocation(x, y)
        val isFlag = game.field().flag(position)
        if (isFlag) {
            listener?.positionUnflagged(x, y)
        } else {
            listener?.positionFlagged(x, y)
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