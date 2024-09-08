/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.config.XYIndexTranslator
import com.mikeburke106.mines.api.model.Position
import javax.inject.Inject

class AndroidPositionPool @Inject constructor(
    private val positionFactory: PositionFactory,
    private val xyIndexTranslator: XYIndexTranslator
) :
    Position.Pool {

    private val positions = mutableListOf<AndroidPosition>()

    override fun iterator(): MutableIterator<AndroidPosition> = positions.iterator()
    override fun atLocation(x: Int, y: Int): AndroidPosition =
        positions[xyIndexTranslator.xyToIndex(x, y)]

    override fun size(): Int = positions.size
    override fun width(): Int = xyIndexTranslator.indexToXY(positions.size - 1).first + 1
    override fun height(): Int = xyIndexTranslator.indexToXY(positions.size - 1).second + 1

    fun setDimensions(width: Int, height: Int) {
        positions.clear()
        for (y in 0 until height) {
            for (x in 0 until width) {
                positions.add(positionFactory.createPosition(x, y))
            }
        }
    }
}
