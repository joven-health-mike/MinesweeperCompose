/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.logger

import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import javax.inject.Inject

/**
 * Game event logger - logs game events to system.out. Call consume to start logging.
 *
 * @param eventProvider The provider of game events.
 */
class GameEventLogger @Inject constructor(
    private val eventProvider: EventProvider
) {
    suspend fun consume() {
        eventProvider.eventFlow.collect { event ->
            when (event) {
                is GameEvent.GameCreated -> println("Game created")
                is GameEvent.GameLost -> println("Game lost")
                is GameEvent.GameWon -> println("Game won: ${event.endTime}")
                is GameEvent.PositionCleared -> println("Position cleared: ${event.index}, ${event.adjacentMines}")
                is GameEvent.PositionFlagged -> println("Position flagged: ${event.index}")
                is GameEvent.PositionUnflagged -> println("Position unflagged: ${event.index}")
                is GameEvent.PositionExploded -> println("Position exploded: ${event.index}")
                is GameEvent.FieldReset -> println("Field reset")
            }
        }
    }
}
