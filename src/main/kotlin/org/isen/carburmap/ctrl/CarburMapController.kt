package org.isen.carburmap.ctrl

import org.apache.logging.log4j.kotlin.logger
import org.isen.carburmap.data.Filters
import org.isen.carburmap.lib.routing.RoutingEngine
import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.view.ICarburMapView
import org.isen.carburmap.model.impl.DefaultCarburmapModel.Companion.logger


class CarburMapController(val model:ICarburMapModel) {
    private val views = mutableListOf<ICarburMapView>()

    fun displayViews() {
        views.forEach(){
            it.display()
        }
        model.fetchAllCities()
    }

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

    fun closeView(){
        views.forEach {
            it.close()
        }
    }

    fun registerViewToCarburMapData(v:ICarburMapView){
        if(!this.views.contains(v)){
            this.views.add(v)
            this.model.register(ICarburMapModel.DataType.Stations, v)
            this.model.register(ICarburMapModel.DataType.VillesList, v)
            this.model.register(ICarburMapModel.DataType.SelectedStation, v)
            this.model.register(ICarburMapModel.DataType.Itinerary, v)
        }
    }

    fun newItinerary(startLat: Double, startLon: Double, endLat: Double, endLon: Double, filters: Filters) {
        val routingEngineRes = RoutingEngine.getInstance().getPathCar(startLat, startLon, endLat, endLon)
        model.newItinerary(routingEngineRes, filters)
    }
}