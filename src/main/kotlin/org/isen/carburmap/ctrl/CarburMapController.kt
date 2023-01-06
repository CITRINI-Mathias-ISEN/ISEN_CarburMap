package org.isen.carburmap.ctrl

import org.apache.logging.log4j.kotlin.logger
import org.isen.carburmap.data.Filters
import org.isen.carburmap.lib.routing.RoutingEngine
import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.view.ICarburMapView
import org.isen.carburmap.model.impl.DefaultCarburmapModel.Companion.logger

class CarburMapController(val model:ICarburMapModel) {
    private val views = mutableListOf<ICarburMapView>()

    /**
     * Display all the views
     */
    fun displayViews() {
        views.forEach(){
            it.display()
        }
        model.fetchAllCities()
    }

    /**
     * Launch a search with the given filters
     * @param lat latitude of the searching point
     * @param lon longitude of the searching point
     * @param filters filters to apply to the search
     */
    fun updateData(lat: Double, lon: Double, filters: Filters) {
        if (filters.json && !filters.xml) {
            model.findStationByJSON(lat, lon, filters)
        }
        else if (!filters.json && filters.xml) {
            model.findStationByXML(listOf(org.openstreetmap.gui.jmapviewer.Coordinate(lat, lon)), filters)
        }
        else {
            logger.error("Error in filters selection")
        }
    }

    /**
     * Close all the views
     */
    fun closeView(){
        views.forEach {
            it.close()
        }
    }

    /**
     * Add a view to the controller
     * @param v the view to add
     */
    fun registerViewToCarburMapData(v:ICarburMapView){
        if(!this.views.contains(v)){
            this.views.add(v)
            this.model.register(ICarburMapModel.DataType.Stations, v)
            this.model.register(ICarburMapModel.DataType.VillesList, v)
            this.model.register(ICarburMapModel.DataType.SelectedStation, v)
            this.model.register(ICarburMapModel.DataType.Itinerary, v)
        }
    }

    /**
     * Launch a search of an itinerary based on the given filters
     * @param startLat latitude of the starting point
     * @param startLon longitude of the starting point
     * @param endLat latitude of the ending point
     * @param endLon longitude of the ending point
     * @param filters filters to apply to the search
     */
    fun newItinerary(startLat: Double, startLon: Double, endLat: Double, endLon: Double, filters: Filters) {
        val routingEngineRes = RoutingEngine.getInstance().getPathCar(startLat, startLon, endLat, endLon)
        model.newItinerary(routingEngineRes, filters)
    }
}