/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

import kotlinx.coroutines.flow.SharedFlow

/**
 * An interface for providing events
 */
interface EventProvider {
    /**
     * The underlying shared flow of events.
     */
    val eventFlow: SharedFlow<Event>
}
