/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface for publishing events.
 */
interface EventPublisher {
    /**
     * Flow of events.
     */
    val events: SharedFlow<Event>

    /**
     * Publishes an event.
     *
     * @param event The event to publish.
     */
    fun publish(event: Event)
}
