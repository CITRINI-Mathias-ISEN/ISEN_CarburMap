package org.isen.carburmap.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class Field(
    val id: Long,
    val name: String,
    val gps_lat: Double,
    val gps_lon: Double,
)
