package org.isen.carburmap

import com.github.kittinunf.fuel.core.FuelManager
import org.openstreetmap.gui.jmapviewer.JMapViewer
import org.openstreetmap.gui.jmapviewer.MapMarkerDot
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.ctrl.CarburMapController
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
    val marker = MapMarkerDot(48.8567, 2.3508)
    marker.color = Color.RED
    marker.name = "Paris"
    map.addMapMarker(marker)
    val model = DefaultCarburmapModel()
    model.findStationByJSON(48.712, 2.371, 1000)
}

fun customKeyStore() {
    val certFile : Certificate = ClassLoader.getSystemClassLoader().getResource("data.economie.gouv.fr.crt")?.let { CertificateFactory.getInstance("X.509").generateCertificate(it.openStream()) }!!
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
    keyStore.load(null, null)
    keyStore.setCertificateEntry("data.economie.gouv.fr", certFile)
    FuelManager.instance.keystore = keyStore
}