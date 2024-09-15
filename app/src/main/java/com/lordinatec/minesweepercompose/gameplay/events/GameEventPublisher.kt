/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Bridge class to convert GameControlStrategy.Listener events to GameEvent objects
 *
 * @constructor Creates a new GameEventPublisher
 */
class GameEventPublisher @Inject constructor(
    val publisherScope: CoroutineScope,
    val callbackDispatcher: CoroutineDispatcher = Dispatchers.Main
) : EventPublisher {
    private val _events = MutableSharedFlow<Event>()
    override val events = _events.asSharedFlow()
    private var gameOver = false

    /**
     * Publishes a GameEvent
     *
     * @param event The event to publish
     */
    override fun publish(event: Event) {
        // block so the events are published in order received
        runBlocking<Unit> {
            publisherScope.launch {
                val gameEvent = event as GameEvent
                if (gameEvent is GameEvent.GameCreated) {
                    gameOver = false
                }
                publishEvent(gameEvent)
            }
        }
    }

    private suspend fun publishEvent(event: GameEvent) {
        withContext(callbackDispatcher) {
            _events.emit(event)
        }
    }
}
