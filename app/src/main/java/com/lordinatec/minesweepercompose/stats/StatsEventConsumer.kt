/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import com.lordinatec.minesweepercompose.gameplay.events.EventPublisher
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent

/**
 * Consumes game events and updates the stats.
 */
class StatsEventConsumer(
    private val eventPublisher: EventPublisher,
    private val statsProvider: StatsProvider
) {
    /**
     * Consumes game events and updates the stats.
     */
    suspend fun consume() {
        eventPublisher.events.collect { event ->
            when (event) {
                is GameEvent.GameWon -> {
                    statsProvider.setWins(statsProvider.getWins() + 1)
                    maybeSaveBestTime(event.endTime)
                }

                is GameEvent.GameLost -> {
                    statsProvider.setLosses(statsProvider.getLosses() + 1)
                }
            }
        }
    }

    private fun maybeSaveBestTime(newTime: Long) {
        val bestTime = statsProvider.getBestTime()
        if (bestTime == 0L || newTime < bestTime) {
            statsProvider.setBestTime(newTime)
        }
    }
}
