/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import javax.inject.Inject

class AndroidGameEventListener @Inject constructor(
    private val gameEventPublisher: GameEventPublisher
) {
    fun timeUpdate(newTime: Long) {
        gameEventPublisher.timeUpdate(newTime)
    }

    fun positionCleared(x: Int, y: Int, adjacent: Int) {
        gameEventPublisher.positionCleared(x, y, adjacent)
    }

    fun positionExploded(x: Int, y: Int) {
        gameEventPublisher.positionExploded(x, y)
    }

    fun positionFlagged(x: Int, y: Int) {
        gameEventPublisher.positionFlagged(x, y)
    }

    fun positionUnflagged(x: Int, y: Int) {
        gameEventPublisher.positionUnflagged(x, y)
    }

    fun gameWon(winTime: Long) {
        gameEventPublisher.gameWon(winTime)
    }

    fun gameLost() {
        gameEventPublisher.gameLost()
    }
}