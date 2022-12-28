package org.isen.carburmap.data


data class Ville(
    val id: Long,
    val name: String,
    val gps_lat: Double,
    val gps_lng: Double,
    val zip_code: String,
)
