package org.isen.carburmap.model.impl

import org.isen.carburmap.lib.geo.GeoDistanceHelper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.data.*
import org.isen.carburmap.data.json.StationsListJSON
import org.isen.carburmap.data.xml.Pdv
import org.isen.carburmap.data.xml.StationsListXML
import org.isen.carburmap.lib.marker.MapMarkerStation
import org.isen.carburmap.model.ICarburMapModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates

internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
    setDefaultUseWrapper(false)
}).registerKotlinModule()
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

class DefaultCarburmapModel : ICarburMapModel {

    companion object : Logging

    private val pcs = PropertyChangeSupport(this)

    private var stationsList : StationsList? = null

    private var stationsListFinal : StationsList? by Delegates.observable(null) {
            _, oldValue, newValue ->
        logger.info("stationInformation updated")
        pcs.firePropertyChange(ICarburMapModel.DataType.Stations.toString(), oldValue, newValue)
    }

    private var villesList : Array<Ville>? by Delegates.observable(null) {
            _, oldValue, newValue ->
        logger.info("stationInformation updated")
        pcs.firePropertyChange("villesList", oldValue, newValue)
    }

    var selectedMapMarkerStation: MapMarkerStation? by Delegates.observable(null) {
            _, oldValue, newValue ->
        logger.info("update selectedStation $newValue")
        pcs.firePropertyChange(ICarburMapModel.DataType.SelectedStation.toString(), oldValue, newValue)
    }

    /**
     * Get the list of stations from the JSON API
     * @param lat latitude of your position
     * @param lon longitude of your position
     * @param distance radius of the search in meters
     * @return the list of stations in a radius of distance from your position
     */
    override fun findStationByJSON(lat:Double, lon:Double, filters:Filters) {
            "https://data.economie.gouv.fr//api/records/1.0/search/?dataset=prix-carburants-fichier-instantane-test-ods-copie&q=&rows=-1&geofilter.distance=$lat%2C+$lon%2C+10000"
            .httpGet()
            .responseObject(StationsListJSON.Deserializer()) { request, response, result ->
                val (data, error) = result
                if (data != null) {
                    stationsList = StationsList(data)
                    filtrage(filters)
                    stationsListFinal = stationsList
                } else {
                    println(error)
                    logger.warn("Be careful data is void $error")
                }
            }
    }

    /**
     * Get the list of stations from the XML API
     * @param lat latitude of your position
     * @param lon longitude of your position
     * @param distance radius of the search in meters
     * @return the list of stations in a radius of distance from your position
     */
    override fun findStationByXML(lat:Double, lon:Double, filters:Filters) {
        // Get the file from resources folder
        val file = ClassLoader.getSystemClassLoader().getResource("./PrixCarburants_instantane.xml")
        val xml = file.readText()
        var data = kotlinXmlMapper.readValue(xml, StationsListXML::class.java)
        if (data != null) {
            val geoDistanceHelper = GeoDistanceHelper(lat, lon)
            data.pdv = data.pdv.filter{ geoDistanceHelper.calculate(it.latitude.toDouble() / 100000, it.longitude.toDouble() / 100000) < 10000.0 } as ArrayList<Pdv>
            stationsList = StationsList(data)
            filtrage(filters)
            stationsListFinal = stationsList
        } else {
            logger.warn("Be careful data is void")
        }
    }

     override fun fetchAllCities() : Array<Ville>? {

        val content = ClassLoader.getSystemClassLoader().getResource("./cities.json")?.readText(Charsets.UTF_8)

        val gson = Gson()
        villesList = gson.fromJson(content, Array<Ville>::class.java)
        println(villesList!![0].zip_code)
        return villesList!!

    }

    override fun register(datatype:ICarburMapModel.DataType, listener:PropertyChangeListener){
        pcs.addPropertyChangeListener(datatype.toString(), listener)
    }
    override fun unregister(listener:PropertyChangeListener){
        //TODO
    }
    override fun changeCurrentSelection(id:Long){
        //TODO
    }

    override fun filtrage(filters: Filters) {
        if (filters.Toilet) {
            stationsList!!.stations = stationsList!!.stations.filter { it.services!!.contains("Toilettes publiques") } as ArrayList<Station>
        }
        if (filters.FoodStore) {
            stationsList!!.stations = stationsList!!.stations.filter { it.services!!.contains("Boutique non alimentaire") } as ArrayList<Station>
        }
        if (filters.InflationStation) {
            stationsList!!.stations = stationsList!!.stations.filter { it.services!!.contains("Station de gonflage") } as ArrayList<Station>
        }

        if (filters.e10) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "E10"}
            } as ArrayList<Station>
        }
        if (filters.e85) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "E85"}
            } as ArrayList<Station>
        }
        if (filters.sp98) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "SP98"}
            } as ArrayList<Station>
        }
        if (filters.gazole) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "Gazole"}
            } as ArrayList<Station>
        }
        if (filters.sp95) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "SP95"}
            } as ArrayList<Station>
        }
        if (filters.gplc) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "GPLc"}
            } as ArrayList<Station>
        }
        stationsList!!.stations.forEach { println(it) }

    }

}