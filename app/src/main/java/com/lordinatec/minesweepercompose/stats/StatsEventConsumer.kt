/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.stats

import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.GameEvent
import javax.inject.Inject

/**
 * Consumes game events and updates the stats.
 *
 * @param eventProvider The event provider.
 * @param statsProvider The stats provider.
 */
class StatsEventConsumer @Inject constructor(
    private val eventProvider: EventProvider,
    private val statsProvider: StatsProvider
) {
    /**
     * Consumes game events and updates the stats.
     */
    suspend fun consume() {
        eventProvider.eventFlow.collect { event ->
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
