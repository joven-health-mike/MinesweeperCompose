/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model.apis

/**
 * Field interface
 */
interface Field {
    /**
     * Configuration of the field
     */
    val configuration: Configuration

    /**
     * Mines FieldIndexs
     */
    val mines: Collection<FieldIndex>

    /**
     * Flags FieldIndexs
     */
    val flags: Collection<FieldIndex>

    /**
     * Cleared FieldIndexs
     */
    val cleared: Collection<FieldIndex>

    /**
     * Reset the field
     *
     * @param configuration Configuration Configuration of the field
     */
    fun reset(configuration: Configuration)

    /**
     * Create mines in the field. The given FieldIndexes are guaranteed to NOT be a mine.
     *
     * @param safeIndex Int Index to avoid creating mines on
     */
    fun createMines(safeIndex: FieldIndex)

    /**
     * Determines if the index is a flag
     *
     * @param index Int Index to check
     *
     * @return Boolean True if the index is a flag
     */
    fun isFlag(index: FieldIndex): Boolean

    /**
     * Determines if the index is a mine
     *
     * @param index Int Index to check
     *
     * @return Boolean True if the index is a mine
     */
    fun isMine(index: FieldIndex): Boolean

    /**
     * Clear the given index.
     *
     * @param clearIndex Int Index to clear
     *
     * @return Boolean True if the cleared position was a mine
     */
    fun clear(clearIndex: FieldIndex): Boolean

    /**
     * Flag the given index and returns true if the position was UNflagged.
     *
     * @param flagIndex Int Index to flag
     *
     * @return Boolean True if the position was UNflagged
     */
    fun flag(flagIndex: FieldIndex): Boolean

    /**
     * Returns the number of flags remaining to be placed.
     *
     * @return Int number of flags remaining
     */
    fun flagsRemaining(): Int {
        return configuration.numMines - flags.size
    }

    /**
     * Determines if all mines are flagged
     *
     * @return Boolean True if all mines are flagged
     */
    fun flaggedAllMines(): Boolean {
        return flags.size == mines.size
    }

    /**
     * Determines if all flags are correct
     *
     * @return Boolean True if all flags are correct
     */
    fun allFlagsCorrect(): Boolean {
        return flags.size == mines.size && flags.containsAll(mines)
    }

    /**
     * Get the adjacent FieldIndexes of the given FieldIndex
     *
     * @param index Int Index of the FieldIndex
     *
     * @return Collection<FieldIndex> Adjacent FieldIndexes
     */
    fun adjacentFieldIndexes(index: FieldIndex): Collection<FieldIndex> {
        return mutableListOf<FieldIndex>().apply {
            for (adjacent in Adjacent(index, configuration.numRows, configuration.numCols))
                add(adjacent)
        }
    }

    /**
     * Determines if the entire field has been cleared.
     *
     * @return Boolean True if the entire field has been cleared
     */
    fun allClear(): Boolean {
        return cleared.size == fieldSize() - mines.size
    }

    /**
     * Returns the size of the field.
     *
     * @return Int Size of the field
     */
    fun fieldSize(): Int {
        return configuration.numRows * configuration.numCols
    }

    /**
     * Returns the range of the field indexes.
     *
     * @return IntRange Range of the field indexes
     */
    fun fieldIndexRange(): IntRange {
        return 0 until fieldSize()
    }
}
