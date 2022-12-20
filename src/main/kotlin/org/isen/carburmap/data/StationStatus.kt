package org.isen.carburmap.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

data class StationStatus (val data:DataStationStatus, val lastUpdatedOther:Long, val ttl:Int) {
    class Deserializer : ResponseDeserializable<StationStatus> {
        override fun deserialize(content: String): StationStatus = Gson().fromJson(content, StationStatus::class.java)
    }
}

data class DataStationStatus(val stations:List<StationDescription>)

data class StationDescription(
    val price_e10: Double,
    val price_e85: Double,
    val price_sp98: Double,
    val price_gazole: Double,
    val price_sp95: Double,
    val price_gplc: Double,
    val update: String,
    val cp: String,
    val services: String,
    val timetable: String,
    val com_arm_code: Int,
    val id: Long,
    val name: String,
    val com_arm_name: String,
    val address: String,
    val automate_24_24: String,
    val pop: String,
    val epci_code: Long,
    val reg_name: String,
    val brand: String,
    val dep_name: String,
    val epci_name: String,
    val fuel:String,
    val shortage: String,
    val geo_point: String,
    val dist: Double
)


