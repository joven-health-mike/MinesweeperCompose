package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState

data class FieldViewState(
    val gameOver: Boolean = false,
    val winner: Boolean = false,
    val minesRemaining: Int = -1,
    val timeValue: Long = -1L,
    val tileStates: List<TileState> = List<TileState>(Config.WIDTH * Config.HEIGHT) { TileState.COVERED },
    val tileValues: List<String> = List<String>(Config.WIDTH * Config.HEIGHT) { "" }
)