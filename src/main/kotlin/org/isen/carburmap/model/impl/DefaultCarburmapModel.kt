package org.isen.carburmap.model.impl

import com.github.kittinunf.fuel.httpGet
import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.data.Station
import org.isen.carburmap.model.ICarburMapModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates

class DefaultCarburmapModel : ICarburMapModel {

    companion object : Logging

    private val pcs = PropertyChangeSupport(this)

    private var station: Station? by Delegates.observable(null) {
            _, oldValue, newValue ->
        logger.info("stationInformation updated")
        //pcs.firePropertyChange(IMyVelibModel.DATATYPE_VELIB, oldValue, newValue)
    }

    override fun findStation(x:Double, y:Double, radius:Long) {
            "https://public.opendatasoft.com/api/records/1.0/search/?dataset=prix-des-carburants-j-1&q=&rows=1&geofilter.distance=$x%2C+$y%2C+$radius"
            .httpGet()
            .responseObject(Station.Deserializer()) { request, response, result ->
                val (data, error) = result
                if (data != null) {
                    station = data
                    println(station!!.records[0].fields.name)
                } else {
                    logger.warn("Be careful data is void $error")
                }
            }
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