package org.isen.carburmap.data

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.isen.carburmap.data.json.StationsListJSON
import org.isen.carburmap.data.xml.StationsListXML

/**
 * Class including a list of stations
 * @property stations the list of stations
 */
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

    /**
     * Copy the object
     * @return a copy of the object
     */
    public fun copy(): StationsList {
        return StationsList(ArrayList())
    }

    /**
     * Merge two stations list into one
     * @param stationsList the stations list to merge with the current one
     */
    public fun merge(stations : StationsList) {
        for (station in stations.stations) {
            var stationInList : Station? = this.stations.find { it.id == station.id }
            if (stationInList == null) {
                this.stations.add(station)
            }
        }
    }
}

/**
 * Class representing a station
 * @property id the id of the station
 * @property cp the postal code of the station
 * @property adresse the address of the station
 * @property ville the city of the station
 * @property automate_24_24 if the station is open 24/24
 * @property surRoute if the station is on the road
 * @property coordonnees the coordinates of the station
 * @property prix the prices of each fuel
 * @property services the services of the station
 * @property horaires the opening hours of the station
 */
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

/**
 * Class representing the prices of a station
 * @property carburant the name of the fuel
 * @property valeur the price of the fuel
 * @property maj the date of the last update
 */
data class Prix(
    val carburant: String?=null,
    val valeur: Double?=null,
    val maj: String?=null
)

/**
 * Class representing the opening hours of a station
 * @property jour the list of opening days
 */
data class Planning(val jour: ArrayList<Jour>) {

    class Deserializer : ResponseDeserializable<Planning> {
        override fun deserialize(content: String): Planning = Gson().fromJson(content, Planning::class.java)
    }
}

/**
 * Class representing a day of the opening hours of a station
 * @property id the id of the day
 * @property nom the name of the day
 * @property horaire the opening hours of the day
 */
data class Jour(
    @SerializedName("@id")
    val id: Int?= null,
    @SerializedName("@nom")
    val nom: String?= null,
    val horaire: Horaire)

/**
 * Class representing the opening hours of a day
 * @property ouverture the opening time
 * @property fermeture the closing time
 */
data class Horaire(
    @SerializedName("@ouverture")
    val ouverture: String? = null,
    @SerializedName("@fermeture")
    val fermeture: String?= null)
