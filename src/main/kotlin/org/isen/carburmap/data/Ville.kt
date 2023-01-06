package org.isen.carburmap.data

import org.isen.carburmap.lib.geo.GeoCodeResult

/**
 * Class representing a City
 * @property id the id of the city
 * @property name the name of the city
 * @property gps_lat the latitude of the city
 * @property gps_lng the longitude of the city
 * @property zip_code the postal code of the city
 */
data class Ville(
    val id: Int,
    val name: String,
    val gps_lat: Double,
    val gps_lng: Double,
    val zip_code: String,
)

/**
 * Class representing the data in the search bar
 * @property id the id of the place
 * @property displayName the name of the place
 * @property lat the latitude of the place
 * @property lon the longitude of the place
 * @property cp the postal code of the place
 * @property isGeoCodeResult for the difference between a geocode and a city
 * @property geoCodeResult the geocode result of the place
 */
data class SearchData(
    val id: Int = 0,
    val displayName: String,
    val lat: Double,
    val lon: Double,
    val cp: String?,
    val isGeoCodeResult: Boolean = false,
    val geoCodeResult: GeoCodeResult? = null,
) : Comparable<SearchData> {
    override fun compareTo(other: SearchData): Int {
        return if (this.isGeoCodeResult && other.isGeoCodeResult) {
            this.geoCodeResult!!.display_name.compareTo(other.geoCodeResult!!.display_name)
        } else if (this.isGeoCodeResult) {
            1
        } else if (other.isGeoCodeResult) {
            -1
        } else {
            this.displayName.compareTo(other.displayName)
        }
    }

    override fun toString(): String {
        return displayName
    }
}