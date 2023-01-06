package org.isen.carburmap.data.xml

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("pdv_liste")
data class StationsListXML(
    @JsonProperty("pdv")
    var pdv: ArrayList<Pdv>) {

    fun copy(): StationsListXML {
        val stations = StationsListXML(ArrayList<Pdv>())
        for (pdv in this.pdv) {
            stations.pdv.add(pdv.copy())
        }
        return stations
    }
}

/**
 * This class represents a station ( pdv stands for "point de vente" in French )
 * @param id the id of the station
 * @param adresse the address of the station
 * @param cp the postal code of the station
 * @param ville the city of the station
 * @param latitude the latitude of the station
 * @param longitude the longitude of the station
 * @param horaires the opening hours of the station
 * @param prix the prices of each fuel
 * @param services the services of the station
 * @param pop place of purchase (A = Autoroute, R = Route, I = Interieur)
 */
@JsonRootName("pdv")
data class Pdv(
    // Attribute in xml object
    @set:JsonProperty("id")
    var id: Long,
    @set:JsonProperty("latitude")
    var latitude: Double,
    @set:JsonProperty("longitude")
    var longitude: Double,
    @set:JsonProperty("cp")
    var cp: String,
    @set:JsonProperty("pop")
    var pop: String,
    @set:JsonProperty("adresse")
    var adresse: String,
    @set:JsonProperty("ville")
    var ville: String,
    @set:JsonProperty("services")
    var services: Services ? = null,
    @set:JsonProperty("prix")
    var prix: List<Prix> = ArrayList(),
    @set:JsonProperty("horaires")
    var horaires: Horaires ?= null,
)

/**
 * This class represents a set of services
 * @param service the name of the service
 */
data class Services(
    @set:JsonProperty("service")
    var name: ArrayList<String> = ArrayList()
)

/**
 * This class represents a price with its name and value
 * @property nom the name of the fuel
 * @property id the id of the fuel
 * @property valeur the value of the fuel
 * @property maj the date of the last update
 */
data class Prix(
    @JsonProperty("nom")
    var carburant: String ?= null,
    @JsonProperty("id")
    var id: Int ?= null,
    @JsonProperty("valeur")
    var valeur: Double ?= null,
    @JsonProperty("maj")
    var maj: String ?= null
)

/**
 * This class represents the opening hours of a station
 * @param automates_24_24 represents if the station is open 24/24
 * @param horaires the opening hours of the station for each day
 */
data class Horaires(
    @JsonProperty("automate-24-24")
    var automate_24_24: String ?= null,
    @JsonProperty("jour")
    var jours: List<Jour> = ArrayList(),
)

/**
 * This class represents the opening hours of a station for a specific day
 * @param id the id of the day
 * @param nom the name of the day
 * @param horaires the opening hours of the station for the day
 */
data class Jour(
    @set:JsonProperty("id")
    var id: Int ?= null,
    @set:JsonProperty("nom")
    var nom: String ?= null,
    @set:JsonProperty("ferme")
    var ferme: String ?= null,
    @set:JsonProperty("horaire")
    var horaire: Horaire ?= null,
)

/**
 * This class represents the opening hours of a station for a specific day
 * @param debut the opening time of the station
 * @param fin the closing time of the station
 */
data class Horaire(
    @set:JsonProperty("ouverture")
    var debut: String ?= null,
    @set:JsonProperty("fermeture")
    var fin: String ?= null,
)
