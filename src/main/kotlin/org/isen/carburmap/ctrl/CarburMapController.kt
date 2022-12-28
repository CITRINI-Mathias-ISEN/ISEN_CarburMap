package org.isen.carburmap.ctrl

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
        model.findStationByJSON(lat, lon, filters)
        //model.findStationByXML(lat, lon, filters)
    }

    fun closeView(){
        views.forEach(){
            it.close()
        }
    }

    fun registerViewToCarburMapData(v:ICarburMapView){
        if(!this.views.contains(v)){
            this.views.add(v)
            this.model.register(ICarburMapModel.DataType.Stations, v)
            this.model.register(ICarburMapModel.DataType.Carto, v)
            this.model.register(ICarburMapModel.DataType.SelectedStation, v)
        }
    }
}