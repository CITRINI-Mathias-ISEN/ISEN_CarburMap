package org.isen.carburmap.data

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
    var services: ArrayList<String> ? = ArrayList<String>()
    //val horaires: String,
)

data class Prix(
    val carburant: String,
    val valeur: Double,
    val maj: String
)
