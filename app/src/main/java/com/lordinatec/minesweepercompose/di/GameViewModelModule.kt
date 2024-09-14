/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import android.app.Application
import android.content.Context
import com.lordinatec.minesweepercompose.gameplay.GameController
import com.lordinatec.minesweepercompose.gameplay.events.EventPublisher
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactory
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

/**
 * Module for providing GameViewModel and its dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
class GameViewModelModule {

    @Provides
    @Singleton
    fun provideViewModel(
        gameController: GameController,
        gameEventPublisher: GameEventPublisher,
        field: AndroidField,
    ): GameViewModel = GameViewModel(gameController, gameEventPublisher, field)

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
        androidField: AndroidField,
        timer: Timer,
        eventPublisher: GameEventPublisher,
        coordinateFactory: CoordinateFactory
    ): GameController =
        GameController(androidField, timer, eventPublisher, coordinateFactory)

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
interface GameViewModelInterfaceModule {
    @Binds
    fun provideEventPublisher(
        gameEventPublisher: GameEventPublisher
    ): EventPublisher

    @Binds
    fun provideStatsProvider(
        statsUpdater: StatsUpdater
    ): StatsProvider

    @Binds
    fun provideTimer(
        timer: CoroutineTimer
    ): Timer

    @Binds
    fun provideTimerOnTickListener(defaultOnTickListener: Timer.DefaultOnTickListener): Timer.OnTickListener
}
