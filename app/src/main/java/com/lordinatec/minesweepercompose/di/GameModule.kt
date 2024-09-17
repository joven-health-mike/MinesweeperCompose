/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.apis.CachedCoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateTranslator
import com.lordinatec.minesweepercompose.gameplay.model.apis.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import com.lordinatec.minesweepercompose.gameplay.model.apis.XYIndexTranslator
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
        coordinateFactory: CoordinateFactory,
        configuration: Configuration,
    ): AndroidField =
        AndroidField(coordinateFactory, configuration)

    @Provides
    @Singleton
    fun provideDefaultConfiguration(
        @Named("numRows") numRows: Int,
        @Named("numCols") numCols: Int,
        @Named("numMines") numMines: Int
    ): DefaultConfiguration = DefaultConfiguration(numRows, numCols, numMines)

    @Provides
    @Singleton
    fun provideXYIndexTranslator(): XYIndexTranslator = XYIndexTranslator()

    @Provides
    @Singleton
    fun provideCoordinateFactoryImpl(coordinateTranslator: CoordinateTranslator): CachedCoordinateFactory =
        CachedCoordinateFactory(coordinateTranslator)

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
    fun bindCoordinateTranslator(xyIndexTranslator: XYIndexTranslator): CoordinateTranslator

    @Binds
    fun bindCoordinateFactory(coordinateFactoryImpl: CachedCoordinateFactory): CoordinateFactory

    @Binds
    fun bindField(androidField: AndroidField): Field
}
