package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.mikeburke106.mines.api.model.GameControlStrategy

class GameListenerBridge(
    private val onTimeUpdate: (newTime: Long) -> Unit = {},
    private val onPositionCleared: (index: Int, adjacentMines: Int) -> Unit = { _, _ -> },
    private val onPositionExploded: (index: Int) -> Unit = { _ -> },
    private val onPositionFlagged: (index: Int) -> Unit = { _ -> },
    private val onPositionUnflagged: (index: Int) -> Unit = { _ -> },
    private val onGameWon: () -> Unit = {},
    private val onGameLost: () -> Unit = {}
) :
    GameControlStrategy.Listener {
    override fun timeUpdate(newTime: Long) {
        onTimeUpdate(newTime)
    }

    override fun positionCleared(x: Int, y: Int, adjacentMines: Int) {
        val index = xyToIndex(x, y)
        onPositionCleared(index, adjacentMines)
    }

    override fun positionExploded(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        onPositionExploded(index)
    }

    override fun positionFlagged(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        onPositionFlagged(index)
    }

    override fun positionUnflagged(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        onPositionUnflagged(index)
    }

    override fun gameWon() {
        onGameWon()
    }

    override fun gameLost() {
        onGameLost()
    }
}