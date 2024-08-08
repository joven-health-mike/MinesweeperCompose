package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState

data class FieldViewState(
    val minesRemaining: Int = -1,
    val timeValue: Long = -1L,
    val tileStates: List<TileState> = List<TileState>(Config.width * Config.height) { TileState.COVERED }
)