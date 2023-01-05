package org.isen.carburmap.model

import com.graphhopper.ResponsePath
import org.isen.carburmap.data.*
import java.beans.PropertyChangeListener

interface ICarburMapModel {
    enum class DataType {
        Stations, SelectedStation, VillesList, Itinerary
    }
    fun register(datatype:DataType,listener:PropertyChangeListener)
    fun unregister(listener:PropertyChangeListener)
    fun findStationByJSON(lat:Double, lon:Double, filters: Filters, merge:Boolean = false)

    fun findStationByXML(lat:Double, lon:Double, filters: Filters)
    fun changeCurrentSelection(id:Long)
    fun fetchAllCities() : Array<SearchData>?
    fun filtrage(filters: Filters)
    fun newItinerary(routingEngineRes: ResponsePath)
}