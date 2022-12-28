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
    customKeyStore()
    val model = DefaultCarburmapModel()
    val controller = CarburMapController(model)
    val mapView = MapView(controller)
    val filterView = StartPage(controller)

    controller.registerViewToCarburMapData(mapView)
    controller.registerViewToCarburMapData(filterView)

    controller.displayViews()
}

fun customKeyStore() {
    val certFile : Certificate = ClassLoader.getSystemClassLoader().getResource("data.economie.gouv.fr.crt")?.let { CertificateFactory.getInstance("X.509").generateCertificate(it.openStream()) }!!
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)
    keyStore.setCertificateEntry("data.economie.gouv.fr", certFile)
    FuelManager.instance.keystore = keyStore
}