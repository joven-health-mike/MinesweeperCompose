/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

import kotlinx.coroutines.flow.SharedFlow

interface EventPublisher {
    val events: SharedFlow<Event>
    fun publish(event: Event)
}