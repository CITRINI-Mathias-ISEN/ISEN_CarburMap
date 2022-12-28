package org.isen.carburmap.lib.routing

import com.graphhopper.ResponsePath
import com.graphhopper.util.shapes.GHPoint
import org.openstreetmap.gui.jmapviewer.Coordinate

class RoutingEngine private constructor() {
    companion object {
        private var INSTANCE: RoutingEngine? = null
        fun getInstance(): RoutingEngine {
            if (INSTANCE == null) {
                INSTANCE = RoutingEngine()
            }
            return INSTANCE!!
        }
    }

    private val routingApiCar = RoutingApi()

    fun getPathCar(start: GHPoint, end: GHPoint): ResponsePath {
        return routingApiCar.getRoute(start, end)
    }
    fun getPathCar(start: Coordinate, end: Coordinate): ResponsePath {
        return getPathCar(GHPoint(start.lat, start.lon), GHPoint(end.lat, end.lon))
    }
    fun getPathCar(startLat : Double, startLon : Double, endLat : Double, endLon : Double): ResponsePath {
        return getPathCar(GHPoint(startLat, startLon), GHPoint(endLat, endLon))
    }


}