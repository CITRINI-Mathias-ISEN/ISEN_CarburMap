package org.isen.carburmap.lib.geo

import kotlin.math.*

/**
 * Helper class to calculate distance between two points
 * @param latA latitude of the fixed point
 * @param lonA longitude of the fixed point
 * @property EARTH_DIAMETER diameter of the earth in meters
 * @property latARad latitude of the fixed point in radians
 * @property lonARad longitude of the fixed point in radians
 */
class GeoDistanceHelper(latA:Number, lonA:Number) {
    companion object {
        const val EARTH_DIAMETER = 6371e3
    }

    private val latARad = latA.toDouble() * PI / 180
    private val lonARad = lonA.toDouble() * PI / 180

    /**
     * Calculate the distance between the fixed point in the object and a point given in parameters
     * @param latB latitude of the second point
     * @param lonB longitude of the second point
     * @return the distance in meters
     */
    fun calculate(latB:Number, lonB:Number): Double {
        val latBRad = latB.toDouble() * PI / 180
        val lonBRad = lonB.toDouble() * PI / 180
        val deltaLat = latBRad - latARad
        val deltaLon = lonBRad - lonARad
        val a = sin(deltaLat / 2) * sin(deltaLat / 2) +
                cos(latARad) * cos(latBRad) *
                sin(deltaLon / 2) * sin(deltaLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_DIAMETER * c
    }
}
