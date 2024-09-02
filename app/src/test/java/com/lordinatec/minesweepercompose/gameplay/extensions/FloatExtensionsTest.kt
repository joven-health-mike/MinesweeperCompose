/*
 * Copyright Lordinatec LLC 2024
 */

package com.lordinatec.minesweepercompose.gameplay.extensions

import com.lordinatec.minesweepercompose.extensions.formatString
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class FloatExtensionsTest {
    @Test
    fun testZeroPadding() = runTest {
        val testValue = 3.1415926535898f
        assertEquals("3", testValue.formatString(0))
    }

    @Test
    fun testOnePadding() = runTest {
        val testValue = 3.1415926535898f
        assertEquals("3.1", testValue.formatString(1))
    }

    @Test
    fun testTwoPadding() = runTest {
        val testValue = 3.1415926535898f
        assertEquals("3.14", testValue.formatString(2))
    }

    @Test
    fun testThreePadding() = runTest {
        val testValue = 3.1415926535898f
        assertEquals("3.141", testValue.formatString(3))
    }

    @Test
    fun testNegativePadding() = runTest {
        val testValue = 3.1415926535898f
        assertThrows(IllegalArgumentException::class.java) {
            testValue.formatString(-3)
        }
    }
}