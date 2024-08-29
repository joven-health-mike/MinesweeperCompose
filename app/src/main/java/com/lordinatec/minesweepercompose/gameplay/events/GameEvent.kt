/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

interface GameEvent : Event {
    object GameCreated : GameEvent
    object GameWon : GameEvent
    object GameLost : GameEvent
    data class TimeUpdate(val newTime: Long) : GameEvent
    data class PositionCleared(val index: Int, val adjacentMines: Int) : GameEvent
    data class PositionExploded(val index: Int) : GameEvent
    data class PositionFlagged(val index: Int) : GameEvent
    data class PositionUnflagged(val index: Int) : GameEvent
}