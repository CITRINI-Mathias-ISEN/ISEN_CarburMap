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
import org.isen.carburmap.data.*
import org.isen.carburmap.data.json.StationsListJSON
import org.isen.carburmap.data.xml.Pdv
import org.isen.carburmap.data.xml.StationsListXML
import org.isen.carburmap.lib.filedl.FileDownloader
import org.isen.carburmap.lib.geo.GeoDistanceHelper
import org.isen.carburmap.lib.marker.MapMarkerStation
import org.isen.carburmap.lib.routing.MapPath
import org.isen.carburmap.model.ICarburMapModel
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Polygon
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon
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

    private var stationsList : StationsList? = null

    private var stationsListTemp: StationsList? = null

    private var stationsListFinal : StationsList? by Delegates.observable(null) {
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
        logger.info("update itinerary $newValue")
        //itinerary?.let { getPolygone(it) }
        if (newValue != null) {
            for (i in 0 until newValue.points.size - 1 step 100) {
                val p1 = newValue.points[i]
                findStationByJSON(p1.lat, p1.lon, Filters(), true)
            }
        }
        pcs.firePropertyChange(ICarburMapModel.DataType.Itinerary.toString(), oldValue, newValue)
    }

    fun getPolygone(path : MapPath) {
        val points = path.points
        // Draw a line around the path
        val area = Area()
        // For each 10 points, draw a line
        for (i in 0 until points.size - 1 step 10) {
            val point1: Point2D = Point2D.Double(points[i].lat * 100000, points[i].lon * 100000)
            val point2: Point2D = Point2D.Double(points[i + 1].lat * 100000, points[i + 1].lon * 100000)
            val ln = Line2D.Double(point1.getX(), point1.getY(), point2.getX(), point2.getY())
            val indent = 15.0 // distance from central line
            val length = ln.p1.distance(ln.p2)
            val dx_li = (ln.getX2() - ln.getX1()) / length * indent
            val dy_li = (ln.getY2() - ln.getY1()) / length * indent

            // moved p1 point
            val p1X = ln.getX1() - dx_li
            val p1Y = ln.getY1() - dy_li

            // line moved to the left
            val lX1 = ln.getX1() - dy_li
            val lY1 = ln.getY1() + dx_li
            val lX2 = ln.getX2() - dy_li
            val lY2 = ln.getY2() + dx_li

            // moved p2 point
            val p2X = ln.getX2() + dx_li
            val p2Y = ln.getY2() + dy_li

            // line moved to the right
            val rX1_ = ln.getX1() + dy_li
            val rY1 = ln.getY1() - dx_li
            val rX2 = ln.getX2() + dy_li
            val rY2 = ln.getY2() - dx_li
            val p: Path2D = Path2D.Double()
            p.moveTo(lX1, lY1)
            p.lineTo(lX2, lY2)
            p.lineTo(p2X, p2Y)
            p.lineTo(rX2, rY2)
            p.lineTo(rX1_, rY1)
            p.lineTo(p1X, p1Y)
            p.lineTo(lX1, lY1)
            area.add(Area(p))
        }
        // Transform area to polygon
        val pathIterator = area.getPathIterator(null)
        val coords = DoubleArray(6)
        val polygon = java.awt.Polygon()
        val x : ArrayList<Int> = ArrayList()
        val y : ArrayList<Int> = ArrayList()
        while (!pathIterator.isDone) {
            val type = pathIterator.currentSegment(coords)
            if (type == PathIterator.SEG_LINETO || type == PathIterator.SEG_MOVETO) {
                x.add(coords[0].toInt())
                y.add(coords[1].toInt())
            }
            pathIterator.next()
        }
    }

    /**
     * Get the list of stations from the JSON API
     * @param lat latitude of your position
     * @param lon longitude of your position
     * @return the list of stations in a radius of distance from your position
     */
    override fun findStationByJSON(lat:Double, lon:Double, filters:Filters, merge:Boolean) {
        logger.info("lat=$lat, lon=$lon, filters=$filters")
            "https://data.economie.gouv.fr//api/records/1.0/search/?dataset=prix-carburants-fichier-instantane-test-ods-copie&q=&rows=-1&geofilter.distance=$lat%2C+$lon%2C+10000"
            .httpGet()
            .responseObject(StationsListJSON.Deserializer()) { request, response, result ->
                val (data, error) = result
                if (data != null) {
                    stationsList = StationsList(data)
                    filtrage(filters)
                    if(stationsListFinal != null && merge) {
                        stationsListTemp = stationsListFinal!!.copy()
                        stationsListFinal!!.merge(stationsList!!)
                    }
                    else {
                        stationsListFinal = stationsList
                        stationsListTemp = stationsListFinal!!.copy()
                    }
                    logger.info("${stationsListFinal?.stations?.size} stations found")
                } else {
                    logger.warn("Be careful data is void")
                }
            }
    }

    /**
     * Get the list of stations from the XML API
     * @param lat latitude of your position
     * @param lon longitude of your position
     * @return the list of stations in a radius of distance from your position
     */
    override fun findStationByXML(lat:Double, lon:Double, filters:Filters) {
        // Get the file from resources folder
        logger.info("lat=$lat, lon=$lon, filters=$filters")
        val file = ClassLoader.getSystemClassLoader().getResource("./xml/PrixCarburants_instantane.xml")
        if (file == null) {
            logger.error("File not found")
        }
        val xml = file.readText()
        val data = kotlinXmlMapper.readValue(xml, StationsListXML::class.java)
        if (data.pdv.size > 0) {
            val geoDistanceHelper = GeoDistanceHelper(lat, lon)
            data.pdv = data.pdv.filter{ geoDistanceHelper.calculate(it.latitude / 100000, it.longitude / 100000) < 10000.0 } as ArrayList<Pdv>
            stationsList = StationsList(data)
            filtrage(filters)
            stationsListFinal = stationsList
        } else {
            logger.warn("Be careful data is void")
        }
        logger.info("${stationsListFinal?.stations?.size} stations found")
    }

     override fun fetchAllCities() : Array<SearchData>? {
         if (villesList == null) {
             val content = ClassLoader.getSystemClassLoader().getResource("./cities.json")?.readText(Charsets.UTF_8)
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

    override fun register(datatype:ICarburMapModel.DataType, listener:PropertyChangeListener){
        pcs.addPropertyChangeListener(datatype.toString(), listener)
        logger.info("Event listener registered for $datatype")
    }
    override fun unregister(listener:PropertyChangeListener){
        //TODO
    }
    override fun changeCurrentSelection(id:Long){
        //TODO
    }

    override fun filtrage(filters: Filters) {
        if (filters.Toilet) {
            stationsList!!.stations = stationsList!!.stations.filter { it.services!!.contains("Toilettes publiques") } as ArrayList<Station>
        }
        if (filters.FoodStore) {
            stationsList!!.stations = stationsList!!.stations.filter { it.services!!.contains("Boutique non alimentaire") } as ArrayList<Station>
        }
        if (filters.InflationStation) {
            stationsList!!.stations = stationsList!!.stations.filter { it.services!!.contains("Station de gonflage") } as ArrayList<Station>
        }

        if (filters.e10) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "E10"}
            } as ArrayList<Station>
        }
        if (filters.e85) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "E85"}
            } as ArrayList<Station>
        }
        if (filters.sp98) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "SP98"}
            } as ArrayList<Station>
        }
        if (filters.gazole) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "Gazole"}
            } as ArrayList<Station>
        }
        if (filters.sp95) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "SP95"}
            } as ArrayList<Station>
        }
        if (filters.gplc) {
            stationsList!!.stations = stationsList!!.stations.filter {st ->
                st.prix!!.any { it.carburant == "GPLc"}
            } as ArrayList<Station>
        }
        logger.info("${stationsList!!.stations.size} matching stations found")
    }

    override fun newItinerary(routingEngineRes: ResponsePath) {
        val path = MapPath(routingEngineRes)
        itinerary = path
        logger.info("New itinerary created")
    }

}