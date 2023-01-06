package org.isen.carburmap.model.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.httpGet
import com.google.gson.Gson
import com.graphhopper.ResponsePath
import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import org.isen.carburmap.data.*
import org.isen.carburmap.data.json.StationsListJSON
import org.isen.carburmap.data.xml.Pdv
import org.isen.carburmap.data.xml.StationsListXML
import org.isen.carburmap.lib.event.Promise
import org.isen.carburmap.lib.event.PromisePool
import org.isen.carburmap.lib.filedl.FileDownloader
import org.isen.carburmap.lib.geo.GeoDistanceHelper
import org.isen.carburmap.lib.marker.MapMarkerStation
import org.isen.carburmap.lib.routing.MapPath
import org.isen.carburmap.model.ICarburMapModel
import org.locationtech.jts.geom.Coordinate
import java.awt.EventQueue
import java.awt.geom.*
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeSupport
import kotlin.properties.Delegates


internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
    setDefaultUseWrapper(false)
}).registerKotlinModule()
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

class DefaultCarburmapModel : ICarburMapModel {

    init {
        val url = "https://donnees.roulez-eco.fr/opendata/instantane"
        val path = "xml/PrixCarburants_instantane.zip"
        FileDownloader.download(path, url)
        FileDownloader.unzip(path, "xml/")
    }
    companion object : Logging

    private val pcs = PropertyChangeSupport(this)

    private var stationsList : StationsList? by Delegates.observable(null) {
            _, oldValue, newValue ->
        pcs.firePropertyChange(ICarburMapModel.DataType.Stations.toString(), oldValue, newValue)
    }

    private var villesList : Array<SearchData>? by Delegates.observable(null) {
            _, oldValue, newValue ->
        pcs.firePropertyChange(ICarburMapModel.DataType.VillesList.toString(), oldValue, newValue)
    }

    var selectedMapMarkerStation: MapMarkerStation? by Delegates.observable(null) {
            _, oldValue, newValue ->
        pcs.firePropertyChange(ICarburMapModel.DataType.SelectedStation.toString(), oldValue, newValue)
    }

    var itinerary: MapPath? by Delegates.observable(null) {
            _, oldValue, newValue ->
        val promisePool = PromisePool {
            EventQueue.invokeLater {
                this.logger().info("update itinerary $newValue")
                val stationListForMerge = it.first()
                for (st in it.subList(1, it.size)) {
                    stationListForMerge.merge(st)
                }
                this.stationsList = stationListForMerge
            }
        }
        if (newValue != null) {
            if (itinerary?.filter?.xml == true) {
                itinerary?.let { this.findStationByXML(newValue.points, newValue.filter) }
            } else {
                for (i in 0 until newValue.points.size - 1 step 100) {
                    val p1 = newValue.points[i]
                    findStationByJSON(p1.lat, p1.lon, Filters(), true, promisePool.createPromise())
                }
            }
        }
        pcs.firePropertyChange(ICarburMapModel.DataType.Itinerary.toString(), oldValue, newValue)
    }

    /**
     * Get the list of stations from the JSON API
     * @param lat latitude of your position
     * @param lon longitude of your position
     * @param filters filters to apply
     * @param merge if true, merge the result with the current list of stations
     * @param promise promise to resolve when the request is done
     */
    override fun findStationByJSON(lat:Double, lon:Double, filters:Filters, merge:Boolean, promise: Promise?) {
        logger.info("lat=$lat, lon=$lon, filters=$filters")
            "https://data.economie.gouv.fr//api/records/1.0/search/?dataset=prix-carburants-fichier-instantane-test-ods-copie&q=&rows=-1&geofilter.distance=$lat%2C+$lon%2C+5000"
            .httpGet()
            .responseObject(StationsListJSON.Deserializer()) { request, response, result ->
                val (data, error) = result
                if (data != null) {
                    val stations = StationsList(data)
                    filtrage(filters, stations)
                    if(merge || promise != null) {
                        promise?.result = stations
                    } else {
                        this.stationsList = stations
                    }
                } else {
                    promise?.state = Promise.State.REJECTED
                    logger.warn("Be careful data is void")
                    if (error != null) {
                        logger.error(error)
                    }
                }
            }
    }

    /**
     * Get the list of stations from the XML API
     * @param points list of points to use to find the stations
     * @param filters filters to apply
     */
    override fun findStationByXML(points: List<org.openstreetmap.gui.jmapviewer.Coordinate>, filters:Filters) {
        // Get the file from resources folder
        //logger.info("lat=$lat, lon=$lon, filters=$filters")
        val file = ClassLoader.getSystemClassLoader().getResource("./xml/PrixCarburants_instantane.xml")
        if (file == null) {
            logger.error("File not found")
        }
        val xml = file?.readText()
        val data = kotlinXmlMapper.readValue(xml, StationsListXML::class.java)
        if (data.pdv.size > 0) {
            var stationsList = filterStationsXML(points[0].lat, points[0].lon, filters, data.copy())
            if (points.size > 1) {
                for (i in 1 until points.size step 100) {
                    stationsList.merge(filterStationsXML(points[i].lat, points[i].lon, filters, data.copy()))
                }
            }
            this.stationsList = stationsList
        } else {
            logger.warn("Be careful data is void")
        }
        logger.info("${stationsList?.stations?.size} stations found")
    }

    /**
     * Filter the stations in the StationsListXML with the filters
     * @param lat latitude of your position
     * @param lon longitude of your position
     * @param filters filters to apply
     * @param data StationsListXML to filter
     * @return the list of stations in a radius of distance from your position matching the filters
     */
    private fun filterStationsXML(lat:Double, lon:Double, filters:Filters, data:StationsListXML) : StationsList {
        val geoDistanceHelper = GeoDistanceHelper(lat, lon)
        data.pdv = data.pdv.filter{ geoDistanceHelper.calculate(it.latitude / 100000, it.longitude / 100000) < 5000.0 } as ArrayList<Pdv>
        val stationsList = StationsList(data)
        filtrage(filters, stationsList)
        return stationsList
    }

    /**
     * Fetch the list of cities from the JSON file cities.json
     * @return the list of cities
     */
     override fun fetchAllCities() : Array<SearchData>? {
         if (villesList == null) {
             val content = DefaultCarburmapModel::class.java.getResource("/cities.json")?.readText(Charsets.UTF_8)
             val gson = Gson()
             val villes = gson.fromJson(content, Array<Ville>::class.java)
             villesList = villes.map {
                 SearchData(
                     id = it.id,
                     displayName = "${it.name} (${it.zip_code})",
                     cp = it.zip_code,
                     lat = it.gps_lat,
                     lon = it.gps_lng
                 )
             }.toTypedArray()
             logger.info("${villesList?.size} cities found")
         }
         return villesList!!

    }

    /**
     * Register an event listener to the model
     * @param datatype the type of event to listen
     * @param listener the listener to register
     */
    override fun register(datatype:ICarburMapModel.DataType, listener:PropertyChangeListener){
        pcs.addPropertyChangeListener(datatype.toString(), listener)
        logger.info("Event listener registered for $datatype")
    }

    /**
     * Unregister an event listener to the model
     * @param listener the listener to unregister
     */
    override fun unregister(listener:PropertyChangeListener){
        //TODO
    }

    /**
     * Filter the stations in the StationsList with the filters
     * @param filters filters to apply
     * @param stationsList StationsList to filter
     */
    override fun filtrage(filters: Filters, stations: StationsList) {
        if (filters.Toilet) {
            stations.stations = stations.stations.filter { it.services!!.contains("Toilettes publiques") } as ArrayList<Station>
        }
        if (filters.FoodStore) {
            stations.stations = stations.stations.filter { it.services!!.contains("Boutique non alimentaire") } as ArrayList<Station>
        }
        if (filters.InflationStation) {
            stations.stations = stations.stations.filter { it.services!!.contains("Station de gonflage") } as ArrayList<Station>
        }

        if (filters.e10) {
            stations.stations = stations.stations.filter {st ->
                st.prix!!.any { it.carburant == "E10"}
            } as ArrayList<Station>
        }
        if (filters.e85) {
            stations.stations = stations.stations.filter {st ->
                st.prix!!.any { it.carburant == "E85"}
            } as ArrayList<Station>
        }
        if (filters.sp98) {
            stations.stations = stations.stations.filter {st ->
                st.prix!!.any { it.carburant == "SP98"}
            } as ArrayList<Station>
        }
        if (filters.gazole) {
            stations.stations = stations.stations.filter {st ->
                st.prix!!.any { it.carburant == "Gazole"}
            } as ArrayList<Station>
        }
        if (filters.sp95) {
            stations.stations = stations.stations.filter {st ->
                st.prix!!.any { it.carburant == "SP95"}
            } as ArrayList<Station>
        }
        if (filters.gplc) {
            stations.stations = stations.stations.filter {st ->
                st.prix!!.any { it.carburant == "GPLc"}
            } as ArrayList<Station>
        }
        logger.info("${stations.stations.size} matching stations found")
    }

    /**
     * Update the itinerary
     * @param routingEngineRes the routing engine result
     * @param filters the filters to apply in the itinerary to filter the stations
     */
    override fun newItinerary(routingEngineRes: ResponsePath, filters: Filters) {
        val path = MapPath(routingEngineRes)
        path.filter = filters
        itinerary = path
        logger.info("New itinerary created")
    }

}