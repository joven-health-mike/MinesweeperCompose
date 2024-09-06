/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.viewmodel

import android.app.Application
import android.content.Context
import com.lordinatec.minesweepercompose.gameplay.GameController
import com.lordinatec.minesweepercompose.gameplay.GameFactory
import com.lordinatec.minesweepercompose.gameplay.events.EventPublisher
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.timer.DefaultTimeProvider
import com.lordinatec.minesweepercompose.gameplay.timer.TimeProvider
import com.lordinatec.minesweepercompose.gameplay.timer.TimerFactory
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
        gameEventPublisher: GameEventPublisher
    ): GameViewModel = GameViewModel(gameController, gameEventPublisher)

    @Provides
    @Singleton
    fun provideDefaultTimeProvider(
        viewModel: GameViewModel
    ): DefaultTimeProvider = DefaultTimeProvider(viewModel)

    @Provides
    @Singleton
    fun provideGameFactory(): GameFactory = GameFactory()

    @Provides
    @Singleton
    fun provideTimerFactory(): TimerFactory = TimerFactory()

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
    ): GameEventPublisher = GameEventPublisher(scope)

    @Provides
    @Singleton
    fun provideGameController(
        gameFactory: GameFactory,
        timerFactory: TimerFactory,
        eventPublisher: GameEventPublisher,
    ): GameController =
        GameController.Factory(gameFactory, timerFactory)
            .createGameController(eventPublisher)

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
abstract class InterfaceModule {
    @Binds
    abstract fun provideEventPublisher(
        gameEventPublisher: GameEventPublisher
    ): EventPublisher

    @Binds
    abstract fun provideTimeProvider(
        defaultTimeProvider: DefaultTimeProvider
    ): TimeProvider

    @Binds
    abstract fun provideStatsProvider(
        statsUpdater: StatsUpdater
    ): StatsProvider
}
