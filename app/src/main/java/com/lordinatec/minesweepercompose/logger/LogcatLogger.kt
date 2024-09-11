/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.logger

import android.util.Log
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import kotlinx.coroutines.launch
import javax.inject.Inject

class LogcatLogger @Inject constructor(
    val eventPublisher: GameEventPublisher
) {
    private val level = debug

    init {
        eventPublisher.publisherScope.launch {
            eventPublisher.events.collect { event ->
                when (event) {
                    is GameEvent.GameCreated -> level("Game created")
                    is GameEvent.GameLost -> level("Game lost")
                    is GameEvent.GameWon -> level("Game won")
                    is GameEvent.TimeUpdate -> level("Time updated: ${event.newTime}")
                    is GameEvent.PositionCleared -> level("Position cleared: ${event.index}, ${event.adjacentMines}")
                    is GameEvent.PositionFlagged -> level("Position flagged: ${event.index}")
                    is GameEvent.PositionUnflagged -> level("Position unflagged: ${event.index}")
                    is GameEvent.PositionExploded -> level("Position exploded: ${event.index}")
                    is GameEvent.FieldReset -> level("Field reset")
                }
            }
        }
    }

    companion object {
        private val debug: (s: String) -> Unit = { s -> Log.d("MinewsweeperCompose", s) }
        private val info: (s: String) -> Unit = { s -> Log.i("MinewsweeperCompose", s) }
        private val verbose: (s: String) -> Unit = { s -> Log.v("MinewsweeperCompose", s) }
    }
}