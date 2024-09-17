/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import javax.inject.Inject

class TimerEventConsumer @Inject constructor(
    private val timer: Timer,
    private val eventPublisher: GameEventPublisher
) {

    suspend fun consume() {
        eventPublisher.events.collect { event ->
            when (event) {
                is GameEvent.FieldReset -> {
                    println("Stopping timer")
                    timer.stop()
                }

                is GameEvent.GameCreated -> {
                    println("Starting timer")
                    timer.start()
                }

                is GameEvent.GameWon, GameEvent.GameLost -> {
                    println("Pausing timer")
                    timer.pause()
                }
            }
        }
    }
}
