/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.util

import android.os.CountDownTimer

/**
 * A timer that counts up from a start time to a duration.
 *
 * @param interval The interval in milliseconds between each tick.
 * @param startTime The start time in milliseconds.
 * @param callback The callback to be called on each tick.
 * @param duration The duration in milliseconds.
 *
 * @constructor Creates a CountUpTimer.
 */
class CountUpTimer(
    interval: Long = 10L,
    startTime: Long = 0L,
    private val callback: (tenMsInterval: Long) -> Unit,
    private val duration: Long = Long.MAX_VALUE
) :
    CountDownTimer(duration - startTime, interval) {

    override fun onTick(msUntilFinished: Long) {
        callback(duration - msUntilFinished)
    }

    override fun onFinish() {
        callback(duration)
    }
}