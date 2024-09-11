/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.logger

import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameEventLogger @Inject constructor(
    val eventPublisher: GameEventPublisher
) {
    init {
        eventPublisher.publisherScope.launch {
            eventPublisher.events.collect { event ->
                when (event) {
                    is GameEvent.GameCreated -> println("Game created")
                    is GameEvent.GameLost -> println("Game lost")
                    is GameEvent.GameWon -> println("Game won")
                    is GameEvent.TimeUpdate -> println("Time updated: ${event.newTime}")
                    is GameEvent.PositionCleared -> println("Position cleared: ${event.index}, ${event.adjacentMines}")
                    is GameEvent.PositionFlagged -> println("Position flagged: ${event.index}")
                    is GameEvent.PositionUnflagged -> println("Position unflagged: ${event.index}")
                    is GameEvent.PositionExploded -> println("Position exploded: ${event.index}")
                    is GameEvent.FieldReset -> println("Field reset")
                }
            }
        }
    }
}