package org.isen.carburmap.lib.marker

import org.isen.carburmap.data.Station
import org.openstreetmap.gui.jmapviewer.Coordinate

class MapMarkerStation(val station: Station, imgPath: String) : MapMarkerSelect(Coordinate(station.coordonnees[0], station.coordonnees[1]), imgPath)