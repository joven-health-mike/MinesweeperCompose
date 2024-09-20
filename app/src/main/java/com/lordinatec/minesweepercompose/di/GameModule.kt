/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.Field
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import com.lordinatec.minesweepercompose.gameplay.timer.TimerLifecycleObserver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for providing game related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
class GameModule {

    @Provides
    @Singleton
    fun provideAndroidField(
        configuration: Configuration,
    ): AndroidField =
        AndroidField(configuration)

    @Provides
    @Singleton
    fun provideDefaultConfiguration(): DefaultConfiguration = DefaultConfiguration(0, 0, 0)

    @Provides
    @Singleton
    fun provideTimerLifecycleObserver(timer: Timer): TimerLifecycleObserver =
        TimerLifecycleObserver(timer)

}

@Module
@InstallIn(SingletonComponent::class)
interface InterfaceGameModule {

    @Binds
    fun bindConfiguration(defaultConfiguration: DefaultConfiguration): Configuration

    @Binds
    fun bindField(androidField: AndroidField): Field
}
