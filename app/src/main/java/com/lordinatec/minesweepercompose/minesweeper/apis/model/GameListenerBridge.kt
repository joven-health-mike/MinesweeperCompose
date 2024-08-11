package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.mikeburke106.mines.api.model.GameControlStrategy

class GameListenerBridge(
    private val onTimeUpdate: (newTime: Long) -> Unit = {},
    private val onPositionCleared: (x: Int, y: Int, adjacentMines: Int) -> Unit = { _, _, _ -> },
    private val onPositionExploded: (x: Int, y: Int) -> Unit = { _, _ -> },
    private val onPositionFlagged: (x: Int, y: Int) -> Unit = { _, _ -> },
    private val onPositionUnflagged: (x: Int, y: Int) -> Unit = { _, _ -> },
    private val onGameWon: () -> Unit = {},
    private val onGameLost: () -> Unit = {}
) :
    GameControlStrategy.Listener {
    override fun timeUpdate(newTime: Long) {
        onTimeUpdate(newTime)
    }

    override fun positionCleared(x: Int, y: Int, adjacentMines: Int) {
        onPositionCleared(x, y, adjacentMines)
    }

    override fun positionExploded(x: Int, y: Int) {
        onPositionExploded(x, y)
    }

    override fun positionFlagged(x: Int, y: Int) {
        onPositionFlagged(x, y)
    }

    override fun positionUnflagged(x: Int, y: Int) {
        onPositionUnflagged(x, y)
    }

    override fun gameWon() {
        onGameWon()
    }

    override fun gameLost() {
        onGameLost()
    }
}