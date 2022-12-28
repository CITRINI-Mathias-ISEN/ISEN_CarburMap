package org.isen.carburmap.lib.routing

import com.graphhopper.GHRequest
import com.graphhopper.ResponsePath
import com.graphhopper.api.GraphHopperWeb
import com.graphhopper.util.Parameters
import com.graphhopper.util.shapes.GHPoint
import okhttp3.OkHttpClient
import java.util.*
import java.util.concurrent.TimeUnit

class RoutingApi(private val profile:String = "car_better", private val apiKey:String = "", private val local: Locale = Locale.FRANCE) {
    private val graph = GraphHopperWeb("https://routing-engine.kyllian.io/route")

    init {
        graph.downloader = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build()
    }
    fun getRoute(start:GHPoint, end:GHPoint, p: String=profile) : ResponsePath {
        val ghReq: GHRequest = GHRequest(start, end).setProfile(p)
        ghReq.putHint("instructions", true)
        ghReq.putHint("calc_points", true)
        ghReq.putHint("elevation", false)
        ghReq.locale = local

        ghReq.pathDetails = listOf(
            Parameters.Details.STREET_NAME,
            Parameters.Details.AVERAGE_SPEED,
            Parameters.Details.EDGE_ID
        )
        val fullRes = graph.route(ghReq)
        if(fullRes.hasErrors()) {
            throw Exception(fullRes.errors.joinToString(", "))
        }
        return fullRes.best
    }

}