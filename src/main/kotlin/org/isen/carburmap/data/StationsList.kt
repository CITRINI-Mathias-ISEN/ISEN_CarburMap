package org.isen.carburmap.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import org.isen.carburmap.data.xml.StationsListXML

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
                record.fields.services_service?.split("//")?.forEach { services.add(it) }
                var station : Station = Station(id, cp, adresse, ville, automate_24_24, surRoute, coordonnees)
                this.stations.add(station)
                station.services = services
                // Transform the string horaires into a JSON object
                val gson = Gson()
                // Deserialize the JSON object into a Planning object
                val planning: Planning = try {
                    gson.fromJson(record.fields.horaires, Planning::class.java)
                } catch (e: Exception) {
                    Planning(arrayListOf())
                }
                station.horaires = planning
            }
            if (station != null) {
                station.prix?.add(Prix(record.fields.prix_nom, record.fields.prix_valeur, record.fields.prix_maj))
            }
        }
    }
    constructor(stations : StationsListXML) {
        for (pdv in stations.pdv) {
            var station : Station? = this.stations.find { it.id == pdv.id }
            // Check if the station is already in the list
            if ( station == null) {
                val id = pdv.id
                val cp = pdv.cp
                val adresse = pdv.adresse
                val ville = pdv.ville
                val automate_24_24 = pdv.horaires?.automate_24_24 == "1"
                val surRoute = pdv.pop == "R"
                val coordonnees : Array<Double> = arrayOf(pdv.latitude / 100000, pdv.longitude / 100000)
                station = Station(id, cp, adresse, ville, automate_24_24, surRoute, coordonnees)
                this.stations.add(station)
                val services = ArrayList<String>()
                pdv.services.forEach { if (it.nom != null) services.add(it.nom!!) }
                station.services = services
                if (pdv.horaires != null) {
                    for (jour in pdv.horaires?.jours!!) {
                        val horaire = Horaire(jour.horaire?.debut, jour.horaire?.fin)
                        station.horaires?.jour?.add(Jour(jour.id, jour.nom, horaire))
                    }
                }
                for (prix in pdv.prix) {
                    station.prix?.add(Prix(prix.carburant, prix.valeur, prix.maj))
                }
            }
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
    val carburant: String?=null,
    val valeur: Double?=null,
    val maj: String?=null
)

data class Planning(val jour: ArrayList<Jour>) {

    class Deserializer : ResponseDeserializable<Planning> {
        override fun deserialize(content: String): Planning = Gson().fromJson(content, Planning::class.java)
    }
}

data class Jour(
    @SerializedName("@id")
    val id: Int?= null,
    @SerializedName("@nom")
    val nom: String?= null,
    val horaire: Horaire)

data class Horaire(
    @SerializedName("@ouverture")
    val ouverture: String? = null,
    @SerializedName("@fermeture")
    val fermeture: String?= null)
