package org.isen.carburmap.data

import org.isen.carburmap.lib.geo.GeoCodeResult

data class Ville(
    val id: Int,
    val name: String,
    val gps_lat: Double,
    val gps_lng: Double,
    val zip_code: String,
)

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