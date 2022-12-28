package org.isen.carburmap.lib.geo

import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class GeoCode {
    fun getLatLonFromAddress(address: String): List<GeoCodeResult> {
        val client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build()
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
    var lat: Double? = null
    var lon: Double? = null
    var display_name: String? = null
}
