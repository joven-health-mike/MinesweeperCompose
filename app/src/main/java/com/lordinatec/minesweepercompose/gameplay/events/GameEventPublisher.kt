/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.events

import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.timer.TimeProvider
import com.mikeburke106.mines.api.model.GameControlStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Bridge class to convert GameControlStrategy.Listener events to GameEvent objects
 *
 * @constructor Creates a new GameEventPublisher
 */
class GameEventPublisher(
    private val publisherScope: CoroutineScope,
    var timeProvider: TimeProvider? = null
) :
    GameControlStrategy.Listener, EventPublisher, CoordinateTranslator by XYIndexTranslator() {
    private val _events = MutableSharedFlow<Event>()
    override val events = _events.asSharedFlow()
    private var gameOver = false

    override fun publish(event: Event) {
        publisherScope.launch {
            withContext(Dispatchers.IO) {
                val gameEvent = event as GameEvent
                if (gameEvent is GameEvent.GameCreated) {
                    gameOver = false
                }
                publishEvent(gameEvent)
            }
        }
    }

    private suspend fun publishEvent(event: GameEvent) {
        _events.emit(event)
    }

    override fun timeUpdate(newTime: Long) {
        publish(GameEvent.TimeUpdate(newTime))
    }

    override fun positionCleared(x: Int, y: Int, adjacentMines: Int) {
        // TODO: remove this dependency on xyToIndex
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
        if (!gameOver) {
            publish(GameEvent.GameWon(timeProvider?.currentMillis() ?: Long.MAX_VALUE))
            gameOver = true
        }
    }

    override fun gameLost() {
        if (!gameOver) {
            publish(GameEvent.GameLost)
            gameOver = true
        }
    }
}
