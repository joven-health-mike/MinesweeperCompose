package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.util.CountUpTimer

class TimerFactory(private val callback: (tenMsInterval: Long) -> Unit) {
    fun create(startTime: Long = 0L): CountUpTimer {
        return CountUpTimer(startTime = startTime, callback = callback)
    }
}