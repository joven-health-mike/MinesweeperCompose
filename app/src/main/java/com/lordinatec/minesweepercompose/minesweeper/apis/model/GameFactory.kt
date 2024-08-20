package com.lordinatec.minesweepercompose.minesweeper.apis.model

import com.lordinatec.minesweepercompose.minesweeper.apis.Config
import com.mikeburke106.mines.api.model.Field
import com.mikeburke106.mines.api.model.GameControlStrategy.Listener
import com.mikeburke106.mines.basic.controller.BasicGameController
import com.mikeburke106.mines.basic.model.BasicConfiguration
import com.mikeburke106.mines.basic.model.BasicFieldFactory
import com.mikeburke106.mines.basic.model.BasicGame
import com.mikeburke106.mines.basic.model.BasicPosition
import com.mikeburke106.mines.basic.model.BasicPositionPool
import com.mikeburke106.mines.basic.model.RandomPositionProvider
import com.mikeburke106.mines.basic.model.RegularIntervalTimingStrategy

private val fieldFactory = BasicFieldFactory(RandomPositionProvider.Factory())

interface GameInfoHolder {
    fun getGameController(): BasicGameController
    fun getField(): Field
    fun getPositionPool(): BasicPositionPool
}

class GameFactory {
    fun createGame(x: Int, y: Int, listener: Listener): GameInfoHolder {
        val positionPool = BasicPositionPool(BasicPosition.Factory(), Config.WIDTH, Config.HEIGHT)
        val config: Field.Configuration = BasicConfiguration(positionPool, Config.MINES)
        val field = fieldFactory.newInstance(config, x, y)
        val game = BasicGame(System.currentTimeMillis(), field, RegularIntervalTimingStrategy(1L))
        return object : GameInfoHolder {
            override fun getGameController(): BasicGameController {
                return BasicGameController(
                    game,
                    positionPool,
                    null
                ).also { it.setListener(listener) }
            }

            override fun getField(): Field {
                return field
            }

            override fun getPositionPool(): BasicPositionPool {
                return positionPool
            }
        }
    }
}