/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.model

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AdjacentTest {
    // must be at least 3x3
    private val width = 10
    private val height = 10

    private lateinit var adjacent: Adjacent

    @Test
    fun testTopLeftCorner() = runTest {
        val testIndex = 0
        val expectedAdjacentCount = 3
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex + 1))
        assertTrue(adjacent.contains(testIndex + width))
        assertTrue(adjacent.contains(testIndex + width + 1))
    }

    @Test
    fun testTopRightCorner() = runTest {
        val testIndex = width - 1
        val expectedAdjacentCount = 3
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex - 1))
        assertTrue(adjacent.contains(testIndex + width))
        assertTrue(adjacent.contains(testIndex + width - 1))
    }

    @Test
    fun testBottomLeftCorner() = runTest {
        val testIndex = width * (height - 1)
        val expectedAdjacentCount = 3
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex + 1))
        assertTrue(adjacent.contains(testIndex - width))
        assertTrue(adjacent.contains(testIndex - width + 1))
    }

    @Test
    fun testBottomRightCorner() = runTest {
        val testIndex = width * height - 1
        val expectedAdjacentCount = 3
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex - 1))
        assertTrue(adjacent.contains(testIndex - width))
        assertTrue(adjacent.contains(testIndex - width - 1))
    }

    @Test
    fun testLeftEdge() = runTest {
        val testIndex = width
        val expectedAdjacentCount = 5
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex + 1))
        assertTrue(adjacent.contains(testIndex - width))
        assertTrue(adjacent.contains(testIndex - width + 1))
        assertTrue(adjacent.contains(testIndex + width))
        assertTrue(adjacent.contains(testIndex + width + 1))
    }

    @Test
    fun testTopEdge() = runTest {
        val testIndex = width / 2
        val expectedAdjacentCount = 5
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex + 1))
        assertTrue(adjacent.contains(testIndex - 1))
        assertTrue(adjacent.contains(testIndex + width - 1))
        assertTrue(adjacent.contains(testIndex + width))
        assertTrue(adjacent.contains(testIndex + width + 1))
    }

    @Test
    fun testRightEdge() = runTest {
        val testIndex = width * 2 - 1
        val expectedAdjacentCount = 5
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex - 1))
        assertTrue(adjacent.contains(testIndex - width))
        assertTrue(adjacent.contains(testIndex - width - 1))
        assertTrue(adjacent.contains(testIndex + width))
        assertTrue(adjacent.contains(testIndex + width - 1))
    }

    @Test
    fun testBottomEdge() = runTest {
        val testIndex = (width * height) - 2
        val expectedAdjacentCount = 5
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex + 1))
        assertTrue(adjacent.contains(testIndex - 1))
        assertTrue(adjacent.contains(testIndex - width - 1))
        assertTrue(adjacent.contains(testIndex - width))
        assertTrue(adjacent.contains(testIndex - width + 1))
    }

    @Test
    fun testCenter() = runTest {
        val testIndex = width + 1
        val expectedAdjacentCount = 8
        adjacent = Adjacent(testIndex, width, height)
        assertEquals(expectedAdjacentCount, adjacent.count())
        assertTrue(adjacent.contains(testIndex + 1))
        assertTrue(adjacent.contains(testIndex - 1))
        assertTrue(adjacent.contains(testIndex - width - 1))
        assertTrue(adjacent.contains(testIndex - width))
        assertTrue(adjacent.contains(testIndex - width + 1))
        assertTrue(adjacent.contains(testIndex + width - 1))
        assertTrue(adjacent.contains(testIndex + width))
        assertTrue(adjacent.contains(testIndex + width + 1))
    }
}
