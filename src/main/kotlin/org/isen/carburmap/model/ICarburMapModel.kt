package org.isen.carburmap.model

import com.graphhopper.ResponsePath
import org.isen.carburmap.data.*
import org.isen.carburmap.lib.event.Promise
import org.locationtech.jts.geom.Coordinate
import java.beans.PropertyChangeListener

interface ICarburMapModel {
    enum class DataType {
        Stations, SelectedStation, VillesList, Itinerary
    }
    fun register(datatype:DataType,listener:PropertyChangeListener)
    fun unregister(listener:PropertyChangeListener)
    fun findStationByJSON(lat:Double, lon:Double, filters: Filters, merge:Boolean = false, promise: Promise? = null)

    fun findStationByXML(points: List<org.openstreetmap.gui.jmapviewer.Coordinate>, filters:Filters)
    fun fetchAllCities() : Array<SearchData>?
    fun filtrage(filters: Filters, stations: StationsList)
    fun newItinerary(routingEngineRes: ResponsePath, filters: Filters)
}