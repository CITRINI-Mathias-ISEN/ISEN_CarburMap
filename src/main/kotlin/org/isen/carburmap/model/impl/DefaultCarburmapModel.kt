package org.isen.carburmap.model.impl

import com.github.kittinunf.fuel.httpGet
import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.data.StationsList
import org.isen.carburmap.model.ICarburMapModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates

class DefaultCarburmapModel : ICarburMapModel {

    companion object : Logging

    private val pcs = PropertyChangeSupport(this)

    private var stationsList: StationsList? by Delegates.observable(null) {
            _, oldValue, newValue ->
        logger.info("stationInformation updated")
        //pcs.firePropertyChange(IMyVelibModel.DATATYPE_VELIB, oldValue, newValue)
    }

    override fun findStationByJSON(x:Double, y:Double, radius:Long) {
            "https://data.economie.gouv.fr//api/records/1.0/search/?dataset=prix-carburants-fichier-instantane-test-ods-copie&q=&rows=-1&geofilter.distance=$x%2C+$y%2C+$radius"
            .httpGet()
            .responseObject(StationsList.Deserializer()) { request, response, result ->
                val (data, error) = result
                if (data != null) {
                    stationsList = data
                    println(stationsList!!.records[0].fields)
                } else {
                    println(error)
                    logger.warn("Be careful data is void $error")
                }
            }
    }

    override fun findStationByXML(x:Double, y:Double, radius:Long) {
        // Get the file from resources folder
        val file = ClassLoader.getSystemClassLoader().getResource("./PrixCarburants_instantane.xml")
        val xml = file.readText()
    }

    override fun register(datatype:String?, listener:PropertyChangeListener){
        //TODO
    }
    override fun unregister(listener:PropertyChangeListener){
        //TODO
    }
    override fun changeCurrentSelection(id:Long){
        //TODO
    }
}