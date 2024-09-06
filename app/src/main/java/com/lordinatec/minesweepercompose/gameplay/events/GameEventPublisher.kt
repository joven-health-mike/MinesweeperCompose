/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.timer.TimeProvider
import com.mikeburke106.mines.api.model.GameControlStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Bridge class to convert GameControlStrategy.Listener events to GameEvent objects
 *
 * @constructor Creates a new GameEventPublisher
 */
class GameEventPublisher(
    private val publisherScope: CoroutineScope,
) :
    GameControlStrategy.Listener, EventPublisher, CoordinateTranslator by XYIndexTranslator() {
    private val _events = MutableSharedFlow<Event>()
    override val events = _events.asSharedFlow()
    private var gameOver = false

    /**
     * Publishes a GameEvent
     *
     * @param event The event to publish
     */
    override fun publish(event: Event) {
        publisherScope.launch {
            withContext(Dispatchers.IO) {
                val gameEvent = event as GameEvent
                if (gameEvent is GameEvent.GameCreated) {
                    gameOver = false
                }
                publishEvent(gameEvent)
            }
        }
    }

    private suspend fun publishEvent(event: GameEvent) {
        _events.emit(event)
    }

    /**
     * Converts a GameControlStrategy.Listener timeUpdate to a TimeUpdate event
     *
     * @param newTime The new time
     */
    override fun timeUpdate(newTime: Long) {
        publish(GameEvent.TimeUpdate(newTime))
    }

    /**
     * Converts a GameControlStrategy.Listener positionCleared to a PositionCleared event
     *
     * @param x The x coordinate of the cleared position
     * @param y The y coordinate of the cleared position
     * @param adjacentMines The number of adjacent mines
     */
    override fun positionCleared(x: Int, y: Int, adjacentMines: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionCleared(index, adjacentMines))
    }

    /**
     * Converts a GameControlStrategy.Listener positionExploded to a PositionExploded event
     *
     * @param x The x coordinate of the exploded position
     * @param y The y coordinate of the exploded position
     */
    override fun positionExploded(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionExploded(index))
    }

    /**
     * Converts a GameControlStrategy.Listener positionFlagged to a PositionFlagged event
     *
     * @param x The x coordinate of the flagged position
     * @param y The y coordinate of the flagged position
     */
    override fun positionFlagged(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionFlagged(index))
    }

    /**
     * Converts a GameControlStrategy.Listener positionUnflagged to a PositionUnflagged event
     *
     * @param x The x coordinate of the unflagged position
     * @param y The y coordinate of the unflagged position
     */
    override fun positionUnflagged(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionUnflagged(index))
    }

    /**
     * Converts a GameControlStrategy.Listener gameWon to a GameWon event.
     *
     * This function will only publish the event once.
     */
    override fun gameWon() {
        if (!gameOver) {
            // TODO: how to get game time without circular dependency...
            publish(GameEvent.GameWon(Long.MAX_VALUE))
            gameOver = true
        }
    }

    /**
     * Converts a GameControlStrategy.Listener gameLost to a GameLost event.
     *
     * This function will only publish the event once.
     */
    override fun gameLost() {
        if (!gameOver) {
            publish(GameEvent.GameLost)
            gameOver = true
        }
    }
}
