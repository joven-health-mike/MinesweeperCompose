/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import javax.inject.Inject

class TimerEventConsumer @Inject constructor(
    private val timer: Timer,
    private val eventProvider: EventProvider
) {

    suspend fun consume() {
        eventProvider.eventFlow.collect { event ->
            when (event) {
                is GameEvent.FieldReset -> {
                    timer.stop()
                }

                is GameEvent.GameCreated -> {
                    timer.start()
                }

                is GameEvent.GameWon, GameEvent.GameLost -> {
                    timer.pause()
                }
            }
        }
    }
}
