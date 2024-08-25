/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.lordinatec.minesweepercompose.minesweeper.apis.view.TileState

/**
 * Data class representing the state of the game.
 *
 * @param timeValue The time value of the game.
 * @param gameOver The game over state.
 * @param winner The winner state.
 * @param minesRemaining The number of mines remaining.
 * @param tileStates The list of tile states.
 * @param tileValues The list of tile values.
 *
 * @constructor Creates a new instance of GameState.
 */
data class GameState(
    val timeValue: Long = 0L,
    val gameOver: Boolean = false,
    val winner: Boolean = true,
    val newGame: Boolean = true,
    val minesRemaining: Int = Config.MINES,
    val tileStates: List<TileState> = List<TileState>(Config.WIDTH * Config.HEIGHT) { TileState.COVERED },
    val tileValues: List<String> = List<String>(Config.WIDTH * Config.HEIGHT) { "" }
)