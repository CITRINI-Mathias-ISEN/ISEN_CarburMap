package org.isen.carburmap.lib.geo

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GeoCodeTest{
    @Test
    fun testGetLatLonFromAddress(){
        val geoCode = GeoCode()
        val latLon = geoCode.getFromAddress("Parc des Princes, 24, Rue du Commandant Guilbaud, Quartier d'Auteuil")
        assertEquals(48.84, latLon[0].lat, 0.005)
        assertEquals(2.25, latLon[0].lon, 0.005)
        assertEquals("Parc des Princes, 24, Rue du Commandant Guilbaud, Quartier d'Auteuil, Paris 16e Arrondissement, Paris, Île-de-France, France métropolitaine, 75016, France", latLon[0].display_name)
        assertEquals(119871247 , latLon[0].place_id)
    }
}