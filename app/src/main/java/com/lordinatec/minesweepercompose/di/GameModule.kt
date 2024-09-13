/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.GameController
import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactoryImpl
import com.lordinatec.minesweepercompose.gameplay.model.apis.DefaultConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
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
        xyIndexTranslator: XYIndexTranslator
    ): AndroidField =
        AndroidField(coordinateFactory, configuration, xyIndexTranslator)

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
    fun provideCoordinateFactoryImpl(): CoordinateFactoryImpl = CoordinateFactoryImpl()

    @Provides
    @Singleton
    fun provideTimerLifecycleObserver(gameController: GameController): TimerLifecycleObserver =
        TimerLifecycleObserver(gameController)

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
abstract class InterfaceGameModule {

    @Binds
    abstract fun bindConfiguration(defaultConfiguration: DefaultConfiguration): Configuration

    @Binds
    abstract fun bindCoordinateTranslator(xyIndexTranslator: XYIndexTranslator): CoordinateTranslator

    @Binds
    abstract fun bindCoordinateFactory(coordinateFactoryImpl: CoordinateFactoryImpl): CoordinateFactory

    @Binds
    abstract fun bindField(androidField: AndroidField): Field
}
