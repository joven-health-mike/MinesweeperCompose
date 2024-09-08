/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import android.app.Application
import android.content.Context
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.GameController
import com.lordinatec.minesweepercompose.gameplay.GameFactory
import com.lordinatec.minesweepercompose.gameplay.events.EventPublisher
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.AndroidPositionPool
import com.lordinatec.minesweepercompose.gameplay.timer.CoroutineTimer
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.stats.StatsEventConsumer
import com.lordinatec.minesweepercompose.stats.StatsProvider
import com.lordinatec.minesweepercompose.stats.StatsUpdater
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GameViewModelModule {

    @Provides
    @Singleton
    fun provideViewModel(
        gameController: GameController,
        gameEventPublisher: GameEventPublisher,
        timer: Timer
    ): GameViewModel = GameViewModel(gameController, gameEventPublisher, timer)

    @Provides
    @Singleton
    fun provideIOCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideStatsEventConsumer(
        eventPublisher: EventPublisher,
        statsProvider: StatsProvider
    ): StatsEventConsumer = StatsEventConsumer(eventPublisher, statsProvider)

    @Provides
    @Singleton
    fun provideGameEventPublisher(
        scope: CoroutineScope,
        xyIndexTranslator: XYIndexTranslator
    ): GameEventPublisher = GameEventPublisher(scope, xyIndexTranslator)

    @Provides
    @Singleton
    fun provideTimerInterval(): Long = 10L

    @Provides
    @Singleton
    fun provideTimerDefaultOnTickListener(): Timer.DefaultOnTickListener =
        Timer.DefaultOnTickListener()

    @Provides
    @Singleton
    fun provideCoroutineTimer(
        interval: Long,
        scope: CoroutineScope,
        onTickListener: Timer.OnTickListener
    ): CoroutineTimer = CoroutineTimer(interval, scope, onTickListener)

    @Provides
    @Singleton
    fun provideGameController(
        gameFactory: GameFactory,
        eventPublisher: GameEventPublisher,
        gameField: AndroidField,
        positionPool: AndroidPositionPool,
        xyIndexTranslator: XYIndexTranslator
    ): GameController =
        GameController(gameFactory, eventPublisher, gameField, positionPool, xyIndexTranslator)

    @Provides
    @Singleton
    fun provideStatsUpdater(
        context: Context
    ): StatsUpdater = StatsUpdater(context)

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext
}

@Module
@InstallIn(SingletonComponent::class)
abstract class GameViewModelInterfaceModule {
    @Binds
    abstract fun provideEventPublisher(
        gameEventPublisher: GameEventPublisher
    ): EventPublisher

    @Binds
    abstract fun provideStatsProvider(
        statsUpdater: StatsUpdater
    ): StatsProvider

    @Binds
    abstract fun provideTimer(
        timer: CoroutineTimer
    ): Timer

    @Binds
    abstract fun provideTimerOnTickListener(defaultOnTickListener: Timer.DefaultOnTickListener): Timer.OnTickListener
}
