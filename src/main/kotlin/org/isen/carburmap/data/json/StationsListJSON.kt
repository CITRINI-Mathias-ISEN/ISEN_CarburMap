package org.isen.carburmap.data.json

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson

/**
 * Class representing a list of stations
 * @param records the list of records associated to the stations
 */
data class StationsListJSON(val records:List<Record>) {
    class Deserializer : ResponseDeserializable<StationsListJSON> {
        override fun deserialize(content: String): StationsListJSON = Gson().fromJson(content, StationsListJSON::class.java)
    }
}

/**
 * The record associated to a station
 */
data class Record(val fields: Fields)

/**
 * Fields of a record
 */
data class Fields(
    val prix_valeur:Double,
    val prix_maj: String,
    val cp: String,
    val services_service: String?,
    val horaires: String,
    val com_code: Int,
    val id: Long,
    val com_arm_name: String,
    val adresse: String,
    val horaires_automate_24_24: String,
    val pop: String,
    val epci_code: Long,
    val reg_name: String,
    val brand: String,
    val dep_name: String,
    val epci_name: String,
    val prix_nom:String,
    val geom: Array<Double>,
    val dist: Double
)


