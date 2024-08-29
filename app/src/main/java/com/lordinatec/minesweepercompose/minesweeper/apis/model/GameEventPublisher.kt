/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config.xyToIndex
import com.mikeburke106.mines.api.model.GameControlStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Bridge class to convert GameControlStrategy.Listener events to GameEvent objects
 *
 * @param coroutineScope - CoroutineScope to use for publishing events
 *
 * @constructor Creates a new GameEventPublisher
 */
class GameEventPublisher(private val coroutineScope: CoroutineScope) :
    GameControlStrategy.Listener, EventPublisher {
    private val _events = MutableSharedFlow<Event>()
    override val events = _events.asSharedFlow()

    override fun publish(event: Event) {
        coroutineScope.launch {
            publishEvent(event as GameEvent)
        }
    }

    private suspend fun publishEvent(event: GameEvent) {
        _events.emit(event)
    }

    override fun timeUpdate(newTime: Long) {
        publish(GameEvent.TimeUpdate(newTime))
    }

    override fun positionCleared(x: Int, y: Int, adjacentMines: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionCleared(index, adjacentMines))
    }

    override fun positionExploded(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionExploded(index))
    }

    override fun positionFlagged(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionFlagged(index))
    }

    override fun positionUnflagged(x: Int, y: Int) {
        val index = xyToIndex(x, y)
        publish(GameEvent.PositionUnflagged(index))
    }

    override fun gameWon() {
        publish(GameEvent.GameWon)
    }

    override fun gameLost() {
        publish(GameEvent.GameLost)
    }

    class Factory {
        fun create(coroutineScope: CoroutineScope): GameEventPublisher {
            return GameEventPublisher(coroutineScope)
        }
    }
}