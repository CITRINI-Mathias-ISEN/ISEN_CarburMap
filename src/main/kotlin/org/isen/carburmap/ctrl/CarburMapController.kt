package org.isen.carburmap.ctrl

import org.apache.logging.log4j.kotlin.logger
import org.isen.carburmap.data.Filters
import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.view.ICarburMapView

class CarburMapController(val model:ICarburMapModel) {
    private val views = mutableListOf<ICarburMapView>()

    fun displayViews() {
        views.forEach(){
            it.display()
        }
        model.fetchAllCities()
        //model.findStationByXML(lat, lon, filters)
    }

    fun updateData(lat: Double, lon: Double, filters: Filters) {
        model.fetchAllCities()
        if (filters.json && !filters.xml) {
            //println("JSON")
            model.findStationByJSON(lat, lon, filters)
        }
        else if (!filters.json && filters.xml) {
            //println("XML")
            model.findStationByXML(lat, lon, filters)
        }
        else {
            logger().error("Erreur au niveau du saisie des filtres JSON ou XML")
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
        }
    }
}