package org.isen.carburmap

import org.isen.carburmap.view.impl.StartPage
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import org.isen.carburmap.ctrl.CarburMapController
import org.isen.carburmap.lib.key.CustomKeyStore
import org.isen.carburmap.view.impl.MapView

fun main() {
    CustomKeyStore.customKeyStore()
    val model = DefaultCarburmapModel()
    val controller = CarburMapController(model)
    val mapView = MapView(controller)
    val filterView = StartPage(controller)

    controller.registerViewToCarburMapData(mapView)
    controller.registerViewToCarburMapData(filterView)

    controller.displayViews()
}