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
    controller.newItinerary(43.56345578807291, 4.0916781735807675, 43.60554813079337, 3.87394831667493)
}