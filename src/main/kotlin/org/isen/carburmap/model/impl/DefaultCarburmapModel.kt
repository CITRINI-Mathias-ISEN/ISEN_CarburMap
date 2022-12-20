package org.isen.carburmap.model.impl

import com.github.kittinunf.fuel.httpGet
import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.data.StationStatus
import org.isen.carburmap.model.ICarburMapModel
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import java.util.Locale
import kotlin.properties.Delegates

class DefaultCarburmapModel : ICarburMapModel {

    companion object : Logging

    private val pcs = PropertyChangeSupport(this)

    private var stationStatus: StationStatus? by Delegates.observable(null) {
            _, oldValue, newValue ->
        logger.info("stationInformation updated")
        //pcs.firePropertyChange(IMyVelibModel.DATATYPE_VELIB, oldValue, newValue)
    }

    override fun findStation(radius:Long) {
            "https://public.opendatasoft.com/api/records/1.0/search/?dataset=prix-des-carburants-j-1&q=&rows=1&geofilter.distance=43.427374%2C+6.761395%2C+$radius"
            .httpGet()
            .responseObject(StationStatus.Deserializer()) { request, response, result ->
                val (ss, error) = result
                if (ss != null) {
                    //Nein Nein Nein !
                    stationStatus = ss
                    println(response)
                    println(stationStatus)
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