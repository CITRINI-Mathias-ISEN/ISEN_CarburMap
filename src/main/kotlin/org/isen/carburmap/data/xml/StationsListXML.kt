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

data class Service(
    @JsonProperty("service")
    var nom: String ?= null
)

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

data class Horaires(
    @JsonProperty("automate-24-24")
    var automate_24_24: String ?= null,
    @JsonProperty("jour")
    var jours: List<Jour> = ArrayList(),
)

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

data class Horaire(
    @set:JsonProperty("ouverture")
    var debut: String ?= null,
    @set:JsonProperty("fermeture")
    var fin: String ?= null,
)
