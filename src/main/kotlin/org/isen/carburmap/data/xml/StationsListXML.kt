package org.isen.carburmap.data.xml

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonRootName

/**
 * Class representing a list of stations
 * @property pdv the list of records associated to the stations
 */
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
 * @property id the id of the station
 * @property adresse the address of the station
 * @property cp the postal code of the station
 * @property ville the city of the station
 * @property latitude the latitude of the station
 * @property longitude the longitude of the station
 * @property horaires the opening hours of the station
 * @property prix the prices of each fuel
 * @property services the services of the station
 * @property pop place of purchase (A = Autoroute, R = Route, I = Interieur)
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
    var services: List<Service> = ArrayList(),
    @set:JsonProperty("prix")
    var prix: List<Prix> = ArrayList(),
    @set:JsonProperty("horaires")
    var horaires: Horaires ?= null,
)

/**
 * This class represents a service
 * @property nom the name of the service
 */
data class Service(
    @JsonProperty("service")
    var nom: String ?= null
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
 * @property automates_24_24 represents if the station is open 24/24
 * @property horaires the opening hours of the station for each day
 */
data class Horaires(
    @JsonProperty("automate-24-24")
    var automate_24_24: String ?= null,
    @JsonProperty("jour")
    var jours: List<Jour> = ArrayList(),
)

/**
 * This class represents the opening hours of a station for a specific day
 * @property id the id of the day
 * @property nom the name of the day
 * @property fermes represents if the station is closed on this day
 * @property horaires the opening hours of the station on this day
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
 * @property debut the opening time of the station
 * @property fin the closing time of the station
 */
data class Horaire(
    @set:JsonProperty("ouverture")
    var debut: String ?= null,
    @set:JsonProperty("fermeture")
    var fin: String ?= null,
)
