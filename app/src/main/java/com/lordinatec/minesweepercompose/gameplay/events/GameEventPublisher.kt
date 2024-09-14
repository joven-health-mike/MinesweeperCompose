/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Bridge class to convert GameControlStrategy.Listener events to GameEvent objects
 *
 * @constructor Creates a new GameEventPublisher
 */
class GameEventPublisher @Inject constructor(val publisherScope: CoroutineScope) : EventPublisher {
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
            val gameEvent = event as GameEvent
            if (gameEvent is GameEvent.GameCreated) {
                gameOver = false
            }
            publishEvent(gameEvent)
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
    fun timeUpdate(newTime: Long) {
        publish(GameEvent.TimeUpdate(newTime))
    }

    /**
     * Converts a GameControlStrategy.Listener gameWon to a GameWon event.
     *
     * This function will only publish the event once.
     */
    fun gameWon(winTime: Long) {
        if (!gameOver) {
            publish(GameEvent.GameWon(winTime))
            gameOver = true
        }
    }

    /**
     * Converts a GameControlStrategy.Listener gameLost to a GameLost event.
     *
     * This function will only publish the event once.
     */
    fun gameLost() {
        if (!gameOver) {
            publish(GameEvent.GameLost)
            gameOver = true
        }
    }
}
