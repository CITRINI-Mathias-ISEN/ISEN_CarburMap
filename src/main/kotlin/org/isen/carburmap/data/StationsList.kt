package org.isen.carburmap.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.isen.carburmap.data.json.StationsListJSON
import org.isen.carburmap.data.xml.StationsListXML

class StationsList {
    var stations : ArrayList<Station> = ArrayList<Station>()

    constructor(stations : StationsListJSON) {
        for (record in stations.records) {
            var station : Station? = this.stations.find { it.id == record.fields.id }
            if ( station == null) {
                val id = record.fields.id
                val cp = record.fields.cp
                val adresse = record.fields.adresse
                val ville = record.fields.com_arm_name
                val automate_24_24 = record.fields.horaires_automate_24_24 == "OUI"
                val surRoute = record.fields.services_service == "R"
                val coordonnees = record.fields.geom
                val services = ArrayList<String>()
                record.fields.services_service?.split("//")?.forEach { services.add(it) }
                var station = Station(id, cp, adresse, ville, automate_24_24, surRoute, coordonnees)
                this.stations.add(station)
                station.services = services
                val gson = Gson()
                val planning: Planning = try {
                    gson.fromJson(record.fields.horaires, Planning::class.java)
                } catch (e: Exception) {
                    Planning(arrayListOf())
                }
                station.horaires = planning
            }
            if (station != null) {
                if (station.prix?.find { it.carburant == record.fields.prix_nom } == null) {
                    station.prix?.add(Prix(record.fields.prix_nom, record.fields.prix_valeur, record.fields.prix_maj))
                }
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
                    if (station.prix?.find { it.carburant == prix.carburant } == null) {
                        station.prix?.add(Prix(prix.carburant, prix.valeur, prix.maj))
                    }
                }
            }
        }
    }

    constructor (stations : ArrayList<Station>) {
        this.stations = stations
    }

    public fun copy(): StationsList {
        return StationsList(ArrayList())
    }

    public fun merge(stations : StationsList) {
        for (station in stations.stations) {
            var stationInList : Station? = this.stations.find { it.id == station.id }
            if (stationInList == null) {
                this.stations.add(station)
            }
        }
    }
}

data class Station(
    val id: Long,
    val cp: String?=null,
    val adresse: String?=null,
    val ville: String?=null,
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
