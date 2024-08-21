/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer

class TimerFactory() {
    fun create(startTime: Long = 0L, callback: (tenMsInterval: Long) -> Unit): CountUpTimer {
        return CountUpTimer(startTime = startTime, callback = callback)
    }
}