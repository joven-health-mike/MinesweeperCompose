/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.apis.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import com.lordinatec.minesweepercompose.gameplay.timer.Timer
import com.lordinatec.minesweepercompose.gameplay.timer.TimerLifecycleObserver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
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
    fun provideDefaultConfiguration(
        @Named("numRows") numRows: Int,
        @Named("numCols") numCols: Int,
        @Named("numMines") numMines: Int
    ): DefaultConfiguration = DefaultConfiguration(numRows, numCols, numMines)

    @Provides
    @Singleton
    fun provideTimerLifecycleObserver(timer: Timer): TimerLifecycleObserver =
        TimerLifecycleObserver(timer)

    @Provides
    @Singleton
    @Named("numRows")
    fun provideNumRows(): Int = 0

    @Provides
    @Singleton
    @Named("numCols")
    fun provideNumCols(): Int = 0

    @Provides
    @Singleton
    @Named("numMines")
    fun provideNumMines(): Int = 0

}

@Module
@InstallIn(SingletonComponent::class)
interface InterfaceGameModule {

    @Binds
    fun bindConfiguration(defaultConfiguration: DefaultConfiguration): Configuration

    @Binds
    fun bindField(androidField: AndroidField): Field
}
