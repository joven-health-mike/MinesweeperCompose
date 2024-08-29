/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import kotlinx.coroutines.flow.SharedFlow

interface EventPublisher {
    val events: SharedFlow<Event>
    fun publish(event: Event)
}