package org.isen.carburmap.lib.geo

import org.junit.jupiter.api.Test

class GeoCodeTest{
    @Test
    fun testGetLatLonFromAddress(){
        val geoCode = GeoCode()
        val latLon = geoCode.getLatLonFromAddress("Isen Toulon")
        latLon.forEach {
            println("Lat: ${it.lat} Lon: ${it.lon} Display name: ${it.display_name}")
        }
    }
}