/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import com.lordinatec.minesweepercompose.gameplay.model.apis.Configuration
import com.lordinatec.minesweepercompose.gameplay.model.apis.Field
import com.lordinatec.minesweepercompose.gameplay.model.apis.FieldIndex
import javax.inject.Inject

/**
 * Android implementation of the Field interface.
 *
 * @param configuration the configuration of the field
 */
class AndroidField @Inject constructor(
    override var configuration: Configuration
) : Field {
    private val _mines = mutableSetOf<FieldIndex>()
    private val _flags = mutableSetOf<FieldIndex>()
    private val _cleared = mutableSetOf<FieldIndex>()

    override val mines: Collection<FieldIndex>
        get() = _mines.toSet()
    override val flags: Collection<FieldIndex>
        get() = _flags.toSet()
    override val cleared: Collection<FieldIndex>
        get() = _cleared.toSet()

    override fun reset(configuration: Configuration) {
        clearCollections()
        updateConfiguration(configuration)
    }

    override fun createMines(safeIndex: FieldIndex) {
        clearCollections()

        val shuffledIndexes = fieldIndexRange().toList().shuffled()

        for (mineIndex in shuffledIndexes) {
            if (mineIndex != safeIndex) {
                _mines.add(mineIndex)
            }
            if (_mines.size >= configuration.numMines) {
                break
            }
        }
    }

    override fun clear(clearIndex: FieldIndex): Boolean {
        _cleared.add(clearIndex)
        return _mines.contains(clearIndex)
    }

    override fun flag(flagIndex: FieldIndex): Boolean {
        val isFlag = isFlag(flagIndex)

        if (isFlag) {
            _flags.remove(flagIndex)
        } else {
            _flags.add(flagIndex)
        }

        return isFlag
    }

    override fun isFlag(index: FieldIndex): Boolean {
        return _flags.contains(index)
    }

    override fun isMine(index: FieldIndex): Boolean {
        return _mines.contains(index)
    }

    private fun updateConfiguration(configuration: Configuration) {
        if (this.configuration == configuration) return
        this.configuration = configuration
        clearCollections()
    }

    private fun clearCollections() {
        _mines.clear()
        _flags.clear()
        _cleared.clear()
    }
}
