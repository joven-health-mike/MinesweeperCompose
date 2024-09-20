/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import android.app.Application
import android.content.Context
import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.gameplay.events.EventPublisher
import com.lordinatec.minesweepercompose.gameplay.events.GameEventPublisher
import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.timer.CoroutineTimer
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import com.lordinatec.minesweepercompose.gameplay.timer.TimerEventConsumer
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameController
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameStateEventConsumer
import com.lordinatec.minesweepercompose.gameplay.viewmodel.GameViewModel
import com.lordinatec.minesweepercompose.stats.StatsEventConsumer
import com.lordinatec.minesweepercompose.stats.StatsProvider
import com.lordinatec.minesweepercompose.stats.StatsUpdater
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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
        gameStateEventConsumer: GameStateEventConsumer
    ): GameViewModel =
        GameViewModel(gameController, gameStateEventConsumer)

    @Provides
    @Singleton
    fun provideGameStateEventConsumer(
        eventProvider: EventProvider
    ): GameStateEventConsumer = GameStateEventConsumer(eventProvider)

    @Provides
    @Singleton
    fun provideIOCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideMainCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideStatsEventConsumer(
        eventProvider: EventProvider,
        statsProvider: StatsProvider
    ): StatsEventConsumer = StatsEventConsumer(eventProvider, statsProvider)

    @Provides
    @Singleton
    fun provideTimerEventConsumer(
        timer: Timer,
        eventProvider: EventProvider
    ): TimerEventConsumer =
        TimerEventConsumer(timer, eventProvider)

    @Provides
    @Singleton
    fun provideGameEventPublisher(
        scope: CoroutineScope,
    ): GameEventPublisher = GameEventPublisher(scope)

    @Provides
    @Singleton
    fun provideCoroutineTimer(
        scope: CoroutineScope,
        eventPublisher: GameEventPublisher
    ): CoroutineTimer = CoroutineTimer(100L, scope, eventPublisher)

    @Provides
    @Singleton
    fun provideGameController(
        androidField: AndroidField,
        timer: Timer,
        eventPublisher: GameEventPublisher,
    ): GameController =
        GameController(androidField, timer, eventPublisher)

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
    fun provideEventProvider(
        eventPublisher: GameEventPublisher
    ): EventProvider
}
