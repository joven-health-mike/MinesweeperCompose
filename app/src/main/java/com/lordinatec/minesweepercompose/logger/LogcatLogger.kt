/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.logger

import android.util.Log
import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import javax.inject.Inject

/**
 * Logs game events to Logcat. Logger starts immediately on creation.
 *
 * @param eventProvider The provider of game events.
 */
class LogcatLogger @Inject constructor(
    private val eventProvider: EventProvider
) {
    private val level = debug

    suspend fun consume() {
        eventProvider.eventFlow.collect { event ->
            when (event) {
                is GameEvent.GameCreated -> level("Game created")
                is GameEvent.GameLost -> level("Game lost")
                is GameEvent.GameWon -> level("Game won: ${event.endTime}")
                is GameEvent.PositionCleared -> level("Position cleared: ${event.index}, ${event.adjacentMines}")
                is GameEvent.PositionFlagged -> level("Position flagged: ${event.index}")
                is GameEvent.PositionUnflagged -> level("Position unflagged: ${event.index}")
                is GameEvent.PositionExploded -> level("Position exploded: ${event.index}")
                is GameEvent.FieldReset -> level("Field reset")
            }
        }
    }

    companion object {
        private val debug: (s: String) -> Unit = { s -> Log.d("MinesweeperCompose", s) }
        private val info: (s: String) -> Unit = { s -> Log.i("MinesweeperCompose", s) }
        private val verbose: (s: String) -> Unit = { s -> Log.v("MinesweeperCompose", s) }
    }
}
