package org.isen.carburmap.lib.marker

import org.isen.carburmap.data.StationsList
import org.openstreetmap.gui.jmapviewer.Coordinate
import org.openstreetmap.gui.jmapviewer.JMapViewer
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class MarkerManager(var map: JMapViewer) : PropertyChangeListener {
    override fun propertyChange(evt: PropertyChangeEvent?) {
        if(evt == null || evt.newValue !is StationsList) return
        synchronized(map){
            (evt.newValue as StationsList).stations.forEach {
                val coordinate = Coordinate(it.coordonnees[0], it.coordonnees[1])
                val markerIcon = MapMarkerIcon(coordinate, "./img/gas-station.png")
                map.addMapMarker(markerIcon)
                println("Station at ${it.coordonnees[0]} ${it.coordonnees[1]}")
            }
        }
    }

}