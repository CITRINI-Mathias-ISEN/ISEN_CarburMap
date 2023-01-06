package org.isen.carburmap.model

import com.graphhopper.ResponsePath
import org.isen.carburmap.data.*
import org.isen.carburmap.lib.event.Promise
import java.beans.PropertyChangeListener

interface ICarburMapModel {
    enum class DataType {
        Stations, SelectedStation, VillesList, Itinerary
    }
    /**
     * Register an event listener to the model
     * @param datatype the type of event to listen
     * @param listener the listener to register
     */
    fun register(datatype:DataType,listener:PropertyChangeListener)

    /**
     * Unregister an event listener to the model
     * @param listener the listener to unregister
     */
    fun unregister(listener:PropertyChangeListener)

    /**
     * Get the list of stations from the JSON API
     * @param lat latitude of your position
     * @param lon longitude of your position
     * @param filters filters to apply
     * @param merge if true, merge the result with the current list of stations
     * @param promise promise to resolve when the request is done
     */

    fun findStationByJSON(lat:Double, lon:Double, filters: Filters, merge:Boolean = false, promise: Promise? = null)
    /**
     * Get the list of stations from the XML API
     * @param points list of points to use to find the stations
     * @param filters filters to apply
     */
    fun findStationByXML(points: List<org.openstreetmap.gui.jmapviewer.Coordinate>, filters:Filters)

    /**
     * Fetch the list of cities from the JSON file cities.json
     * @return the list of cities
     */
    fun fetchAllCities() : Array<SearchData>?

    /**
     * Filter the stations in the StationsList with the filters
     * @param filters filters to apply
     * @param stationsList StationsList to filter
     */
    fun filtering(filters: Filters, stations: StationsList)

    /**
     * Update the itinerary
     * @param routingEngineRes the routing engine result
     * @param filters the filters to apply in the itinerary to filter the stations
     */
    fun newItinerary(routingEngineRes: ResponsePath, filters: Filters)
}