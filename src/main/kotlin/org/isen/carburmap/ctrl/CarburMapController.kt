package org.isen.carburmap.ctrl

import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.view.ICarburMapView

class CarburMapController(val model:ICarburMapModel) {
    val views = mutableListOf<ICarburMapView>()

    fun displayViews(){
        views.forEach(){
            it.display()
        }
    }

    fun closeView(){
        views.forEach(){
            it.close()
        }
    }
}