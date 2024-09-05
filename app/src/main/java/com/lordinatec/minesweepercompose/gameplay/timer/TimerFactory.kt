/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.timer

/**
 * Factory for creating CountUpTimer instances.
 *
 * @constructor Create a new TimerFactory instance.
 */
class TimerFactory {
    /**
     * Create a new CountUpTimer instance.
     *
     * @param startTime The start time of the timer in milliseconds. (Default is 0L)
     * @param callback The callback function to be called every 10 milliseconds.
     *
     * @return A new CountUpTimer instance.
     */
    fun create(startTime: Long = 0L, callback: (tenMsInterval: Long) -> Unit): CountUpTimer {
        return CountUpTimer(startTime = startTime, callback = callback)
    }
}
