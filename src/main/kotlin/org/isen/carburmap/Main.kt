package org.isen.carburmap

import com.github.kittinunf.fuel.core.FuelManager
import org.isen.carburmap.view.impl.StartPage
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import org.isen.carburmap.ctrl.CarburMapController
import org.isen.carburmap.view.impl.MapView
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory

fun main() {
    val model = DefaultCarburmapModel()
    val controller = CarburMapController(model)
    val mapView = MapView(controller)
    val filterView = StartPage(controller)

    controller.registerViewToCarburMapData(mapView)
    controller.registerViewToCarburMapData(filterView)

    controller.displayViews()
}