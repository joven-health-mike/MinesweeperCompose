/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

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

        val shuffledIndexes = fieldIndexRange().filterNot { it == safeIndex }.shuffled()

        _mines.addAll(shuffledIndexes.take(configuration.numMines))
    }

    override fun clear(clearIndex: FieldIndex): Boolean {
        _cleared.add(clearIndex)
        return _mines.contains(clearIndex)
    }

    override fun flag(flagIndex: FieldIndex): Boolean {
        return isFlag(flagIndex).let { isFlag ->
            if (isFlag) {
                _flags.remove(flagIndex)
            } else {
                _flags.add(flagIndex)
            }
            isFlag
        }
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
