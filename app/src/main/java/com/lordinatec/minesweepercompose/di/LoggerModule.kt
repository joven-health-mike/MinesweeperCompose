/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import com.lordinatec.minesweepercompose.gameplay.events.EventProvider
import com.lordinatec.minesweepercompose.logger.GameEventLogger
import com.lordinatec.minesweepercompose.logger.LogcatLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger module for providing logger dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
class LoggerModule {

    @Provides
    @Singleton
    fun providesGameEventLogger(
        eventProvider: EventProvider
    ): GameEventLogger = GameEventLogger(eventProvider)

    @Provides
    @Singleton
    fun providesLogcatLogger(
        eventProvider: EventProvider
    ): LogcatLogger = LogcatLogger(eventProvider)

}
