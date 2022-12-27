package org.isen.carburmap.ctrl

import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.view.ICarburMapView

class CarburMapController(val model:ICarburMapModel) {
    private val views = mutableListOf<ICarburMapView>()

    fun displayViews(){
        views.forEach(){
            it.display()
        }
        model.fetchAllCities()
        //model.findStationByJSON(48.712, 2.371, 5000)
        model.findStationByXML(48.712, 2.371, 5000)
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