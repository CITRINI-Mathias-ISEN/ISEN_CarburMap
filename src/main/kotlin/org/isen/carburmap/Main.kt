package org.isen.carburmap

import com.github.kittinunf.fuel.core.FuelManager
import org.openstreetmap.gui.jmapviewer.JMapViewer
import org.openstreetmap.gui.jmapviewer.MapMarkerDot
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.ctrl.CarburMapController
import org.isen.carburmap.lib.marker.MapMarkerIcon
import org.isen.carburmap.lib.marker.MarkerManager
import org.openstreetmap.gui.jmapviewer.Coordinate
import java.awt.*
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.swing.*

fun main() {
    customKeyStore()
    val frame = JFrame("Carte interactive mieux que Fressel")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val map = JMapViewer()
    val panel = JPanel(BorderLayout())
    panel.add(JScrollPane(map))
    frame.add(panel)
    frame.setSize(800, 600)
    frame.isVisible = true
    val model = DefaultCarburmapModel()
    model.fetchAllCities()
    model.findStationByJSON(48.712, 2.371, 5000)

    model.register("stationsList", MarkerManager(map))
}

fun customKeyStore() {
    val certFile : Certificate = ClassLoader.getSystemClassLoader().getResource("data.economie.gouv.fr.crt")?.let { CertificateFactory.getInstance("X.509").generateCertificate(it.openStream()) }!!
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)
    keyStore.setCertificateEntry("data.economie.gouv.fr", certFile)
    FuelManager.instance.keystore = keyStore
}