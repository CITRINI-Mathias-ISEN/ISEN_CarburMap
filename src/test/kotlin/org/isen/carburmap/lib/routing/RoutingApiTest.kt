package org.isen.carburmap.lib.routing

import com.graphhopper.util.shapes.GHPoint
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


class RoutingApiTest{

    @Test
    fun testRoutingApi(){
        val routingApi = RoutingApi()
        val result = routingApi.getRoute(GHPoint(43.56345578807291, 4.0916781735807675), GHPoint(43.60554813079337, 3.87394831667493))
        assert(result.distance > 0)
        assert(result.time > 0)
        assert(!result.points.isEmpty)
    }

}