/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.di

import com.lordinatec.minesweepercompose.config.CoordinateTranslator
import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.lordinatec.minesweepercompose.gameplay.model.AdjacentHelper
import com.lordinatec.minesweepercompose.gameplay.model.AdjacentHelperImpl
import com.lordinatec.minesweepercompose.gameplay.model.AndroidConfiguration
import com.lordinatec.minesweepercompose.gameplay.model.AndroidField
import com.lordinatec.minesweepercompose.gameplay.model.AndroidPositionPool
import com.lordinatec.minesweepercompose.gameplay.model.PositionFactory
import com.lordinatec.minesweepercompose.gameplay.model.RandomPositionPool
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GameModule {

    @Provides
    @Singleton
    fun provideAndroidPositionPool(
        positionFactory: PositionFactory,
        xyIndexTranslator: XYIndexTranslator
    ): AndroidPositionPool = AndroidPositionPool(positionFactory, xyIndexTranslator)

    @Provides
    @Singleton
    fun provideRandomPositionPool(
        positionPool: AndroidPositionPool
    ): RandomPositionPool = RandomPositionPool(positionPool)

    @Provides
    @Singleton
    fun providePositionFactory(): PositionFactory = PositionFactory()

    @Provides
    @Singleton
    fun provideAndroidField(configuration: AndroidConfiguration): AndroidField =
        AndroidField(configuration)

    @Provides
    @Singleton
    fun provideAndroidConfiguration(
        positionPool: RandomPositionPool,
        numMines: Int
    ): AndroidConfiguration = AndroidConfiguration(positionPool, numMines)

    @Provides
    @Singleton
    fun provideAdjacentHelperImpl(
        field: AndroidField,
        positionPool: AndroidPositionPool
    ): AdjacentHelperImpl = AdjacentHelperImpl(field, positionPool)

    @Provides
    @Singleton
    fun provideXYIndexTranslator(): XYIndexTranslator = XYIndexTranslator()

    @Provides
    @Singleton
    fun provideNumMines(): Int = 0

}

@Module
@InstallIn(SingletonComponent::class)
abstract class InterfaceGameModule {

    @Binds
    abstract fun bindAdjacentHelper(adjacentHelperImpl: AdjacentHelperImpl): AdjacentHelper

    @Binds
    abstract fun bindCoordinateTranslator(xyIndexTranslator: XYIndexTranslator): CoordinateTranslator
}

