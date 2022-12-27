package org.isen.carburmap.model

import GeoDistanceHelper
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GeoDistanceHelperTest {

    @Test
    fun calculate() {
        val helper = GeoDistanceHelper(1, 1)
        val distance = helper.calculate(1, 1)
        assertEquals(0.0, distance, 0.001)
        val distance2 = helper.calculate(2, 2)
        assertEquals(157225.43, distance2, 0.01)
    }
}