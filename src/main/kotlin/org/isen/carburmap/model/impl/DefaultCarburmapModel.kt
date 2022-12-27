package org.isen.carburmap.model.impl

import GeoDistanceHelper
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.data.*
import org.isen.carburmap.data.xml.Pdv
import org.isen.carburmap.lib.marker.MapMarkerStation
import org.isen.carburmap.model.ICarburMapModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates
import org.isen.carburmap.data.xml.StationsListXML

internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
    setDefaultUseWrapper(false)
}).registerKotlinModule()
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

class DefaultCarburmapModel : ICarburMapModel {

    companion object : Logging

    private val pcs = PropertyChangeSupport(this)

    private var stationsList : StationsList? by Delegates.observable(null) {
            _, oldValue, newValue ->
        logger.info("stationInformation updated")
        pcs.firePropertyChange(ICarburMapModel.DataType.Stations.toString(), oldValue, newValue)
    }

    private var villesList : Array<Field>? by Delegates.observable(null) {
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
    override fun findStationByJSON(lat:Double, lon:Double, radius:Long) {
            "https://data.economie.gouv.fr//api/records/1.0/search/?dataset=prix-carburants-fichier-instantane-test-ods-copie&q=&rows=-1&geofilter.distance=$lat%2C+$lon%2C+$radius"
            .httpGet()
            .responseObject(StationsListJSON.Deserializer()) { request, response, result ->
                val (data, error) = result
                if (data != null) {
                    stationsList = StationsList(data)
                    println(stationsList!!.stations[0].prix)
                    println(stationsList!!.stations[0].services)
                    println(stationsList!!.stations[0].horaires)
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
    override fun findStationByXML(lat:Double, lon:Double, radius:Long) {
        // Get the file from resources folder
        val file = ClassLoader.getSystemClassLoader().getResource("./PrixCarburants_instantane.xml")
        val xml = file.readText()
        var data = kotlinXmlMapper.readValue(xml, StationsListXML::class.java)
        val geoDistanceHelper = GeoDistanceHelper(lat, lon)
        data.pdv = data.pdv.filter{ geoDistanceHelper.calculate(it.latitude.toDouble() / 100000, it.longitude.toDouble() / 100000) < radius.toDouble() } as ArrayList<Pdv>
        stationsList = StationsList(data)
    }

    override fun fetchAllCities() : Array<Field>? {

        val content = ClassLoader.getSystemClassLoader().getResource("./cities.json")?.readText(Charsets.UTF_8)

        val gson = Gson()
        villesList = gson.fromJson(content, Array<Field>::class.java)
        println(villesList!![0].name)
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
}