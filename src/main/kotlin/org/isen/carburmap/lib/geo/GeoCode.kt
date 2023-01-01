package org.isen.carburmap.lib.geo

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.isen.carburmap.data.SearchData
import java.util.concurrent.TimeUnit

class GeoCode {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    fun getFromAddress(address: String): List<GeoCodeResult> {
        val request = okhttp3.Request.Builder()
            .url("https://nominatim.openstreetmap.org/search?q=$address&format=json&dedupe=0&countrycodes=Fr&polygon_threshold=0.0005") //&polygon_geojson=1 pour récup les poly
            .build()
        val response = client.newCall(request).execute()
        val body = response.body?.string()
        val json = Gson().fromJson(body, Array<GeoCodeResult>::class.java)
        return json.toList()
    }

}

class GeoCodeResult {
    var place_id: Int = 0
    var osm_type: String? = null
    var osm_id: Long = 0
    var boundingbox: Array<String>? = null
    var lat: Double = 0.0
    var lon: Double = 0.0
    var display_name: String = "No name"

    fun toSearchData(): SearchData {
        return SearchData(
            id = place_id,
            displayName = display_name,
            lat = lat,
            lon = lon,
            cp = null,
            isGeoCodeResult = true,
            geoCodeResult = this
        )
    }
}
