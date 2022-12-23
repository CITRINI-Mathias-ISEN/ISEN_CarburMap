package org.isen.carburmap.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName

public class StationsList {
    public val stations : ArrayList<Station> = ArrayList<Station>()

    constructor(stations : StationsListJSON) {
        for (record in stations.records) {
            var station : Station? = this.stations.find { it.id == record.fields.id }
            // Check if the station is already in the list
            if ( station == null) {
                val id = record.fields.id
                val cp = record.fields.cp
                val adresse = record.fields.adresse
                val ville = record.fields.com_arm_name
                val automate_24_24 = record.fields.horaires_automate_24_24 == "OUI"
                val surRoute = record.fields.services_service == "R"
                val coordonnees = record.fields.geom
                val services = ArrayList<String>()
                // Split the services string with the separator "//"
                record.fields.services_service.split("//").forEach { services.add(it) }
                this.stations.add(Station(id, cp, adresse, ville, automate_24_24, surRoute, coordonnees))
                station = this.stations.find { it.id == record.fields.id }!!
                station.services = services
                // Transform the string horaires into a JSON object
                val gson = Gson()
                // Deserialize the JSON object into a Planning object
                val planning = gson.fromJson(record.fields.horaires, Planning::class.java)
                station.horaires = planning
            }
            station.prix?.add(Prix(record.fields.prix_nom, record.fields.prix_valeur, record.fields.prix_maj))
        }
    }
}

data class Station(
    val id: Long,
    val cp: String,
    val adresse: String,
    val ville: String,
    val automate_24_24: Boolean,
    val surRoute: Boolean,
    val coordonnees: Array<Double>,
    var prix: ArrayList<Prix> ? = ArrayList<Prix>(),
    var services: ArrayList<String> ? = ArrayList<String>(),
    var horaires: Planning ? = null
)

data class Prix(
    val carburant: String,
    val valeur: Double,
    val maj: String
)

data class Planning(val jour: List<Jour>) {

    class Deserializer : ResponseDeserializable<Planning> {
        override fun deserialize(content: String): Planning = Gson().fromJson(content, Planning::class.java)
    }
}

data class Jour(
    @SerializedName("@id")
    val id: Int,
    @SerializedName("@nom")
    val nom: String,
    val horaire: Horaire)

data class Horaire(
    @SerializedName("@ouverture")
    val ouverture: String,
    @SerializedName("@fermeture")
    val fermeture: String)
