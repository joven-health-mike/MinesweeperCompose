/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Coordinate
import com.lordinatec.minesweepercompose.gameplay.model.apis.CoordinateFactory
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import javax.inject.Inject

/**
 * Android implementation of the Field interface.
 *
 * @param configuration the configuration of the field
 */
class AndroidField @Inject constructor(
    override val coordinateFactory: CoordinateFactory,
    override val configuration: Configuration
) : Field {
    private val _fieldList = mutableListOf<Coordinate>()
    private val _mines = mutableSetOf<Coordinate>()
    private val _flags = mutableSetOf<Coordinate>()
    private val _cleared = mutableSetOf<Coordinate>()

    override val fieldList: List<Coordinate>
        get() = _fieldList.toList()
    override val mines: Collection<Coordinate>
        get() = _mines.toSet()
    override val flags: Collection<Coordinate>
        get() = _flags.toSet()
    override val cleared: Collection<Coordinate>
        get() = _cleared.toSet()

    override fun reset(configuration: Configuration) {
        clearCollections()
        updateConfiguration(configuration)
    }

    override fun createMines(index: Int) {
        clearCollections()

        val initCoord = coordinateFactory.createCoordinate(index)
        val shuffledCoordinates = fieldList.shuffled()

        for (coordinate in shuffledCoordinates) {
            if (coordinate != initCoord) {
                _mines.add(coordinate)
            }
            if (_mines.size >= configuration.numMines) {
                break
            }
        }
    }

    override fun clear(index: Int): Boolean {
        val position = _fieldList[index]
        _cleared.add(position)
        return _mines.contains(position)
    }

    override fun flag(index: Int): Boolean {
        val position = _fieldList[index]
        val isFlag = isFlag(index)

        if (isFlag) {
            _flags.remove(position)
        } else {
            _flags.add(position)
        }

        return isFlag
    }

    override fun isFlag(index: Int): Boolean {
        val position = _fieldList[index]
        return _flags.contains(position)
    }

    override fun isMine(index: Int): Boolean {
        val position = _fieldList[index]
        return _mines.contains(position)
    }

    private fun updateConfiguration(configuration: Configuration) {
        if (this.configuration == configuration) return
        this.configuration.numRows = configuration.numRows
        this.configuration.numCols = configuration.numCols
        this.configuration.numMines = configuration.numMines
        clearCollections()
        _fieldList.clear()

        for (y in 0 until configuration.numCols) {
            for (x in 0 until configuration.numRows) {
                _fieldList.add(
                    coordinateFactory.createCoordinate(x, y)
                )
            }
        }
    }

    private fun clearCollections() {
        _mines.clear()
        _flags.clear()
        _cleared.clear()
    }
}
