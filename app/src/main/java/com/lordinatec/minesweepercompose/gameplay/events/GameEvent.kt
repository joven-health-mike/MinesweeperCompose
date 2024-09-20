/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

/**
 * Interface for game events.
 */
interface GameEvent : Event {
    /**
     * Event for when a game is created.
     */
    object GameCreated : GameEvent

    /**
     * Event for when a game is won.
     *
     * @param endTime The time the game was won.
     */
    data class GameWon(val endTime: Long) : GameEvent

    /**
     * Event for when a game is lost.
     */
    object GameLost : GameEvent

    /**
     * Event for when the field is reset.
     */
    object FieldReset : GameEvent

    /**
     * Event for when the time is updated.
     *
     * @param newTime The new time.
     */
    data class TimeUpdate(val newTime: Long) : GameEvent

    /**
     * Event for when a position is cleared.
     *
     * @param index The index of the position.
     * @param adjacentMines The number of adjacent mines.
     */
    data class PositionCleared(val index: Int, val adjacentMines: Int) : GameEvent

    /**
     * Event for when a position is exploded.
     *
     * @param index The index of the position.
     */
    data class PositionExploded(val index: Int) : GameEvent

    /**
     * Event for when a position is flagged.
     *
     * @param index The index of the position.
     */
    data class PositionFlagged(val index: Int) : GameEvent

    /**
     * Event for when a position is unflagged.
     *
     * @param index The index of the position.
     */
    data class PositionUnflagged(val index: Int) : GameEvent
}
