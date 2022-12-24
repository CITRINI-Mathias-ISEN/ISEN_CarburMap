package org.isen.carburmap.data


data class Field(
    val id: Long,
    val name: String,
    val gps_lat: Double,
    val gps_lng: Double,
)
