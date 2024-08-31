/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import com.lordinatec.minesweepercompose.config.Config
import com.lordinatec.minesweepercompose.gameplay.views.TileState

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
    val minesRemaining: Int = Config.mines,
    val tileStates: List<TileState> = List(Config.width * Config.height) { TileState.COVERED },
    val tileValues: List<String> = List(Config.width * Config.height) { "" }
)