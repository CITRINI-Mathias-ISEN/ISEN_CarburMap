package org.isen.carburmap.lib.marker

import org.isen.carburmap.lib.icon.Icon
import org.isen.carburmap.lib.icon.IconManager
import org.openstreetmap.gui.jmapviewer.Coordinate
import org.openstreetmap.gui.jmapviewer.MapObjectImpl
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker.STYLE
import java.awt.*
import java.awt.geom.AffineTransform


class MapMarkerIcon(var coord: Coordinate, private var imgPath : String = "./img/gas-station.png") : MapObjectImpl(null, null, null), MapMarker  {
    private var img: Icon = IconManager.getInstance().getIcon(imgPath)
    private var rotation = 0.0
    private var position: Point? = null

    override fun paint(g: Graphics, position: Point, radio: Int) {
        var imgToDraw = img.image
        if(radio < 1000) {
            imgToDraw = img.smallImage
        } else if(radio > 400000) {
            imgToDraw = img.bigImage
        }
        val halfWidth = imgToDraw.getWidth(null) / 2
        val halfHeight = imgToDraw.getHeight(null) / 2
        if (g is Graphics2D) {
            val t = AffineTransform()
            t.translate((position.x - halfWidth).toDouble(), (position.y - imgToDraw.getHeight(null)).toDouble())
            t.rotate(rotation, halfWidth.toDouble(), halfHeight.toDouble())
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            )
            g.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
            )
            g.drawImage(imgToDraw, t, null)
        }
        if (layer == null || layer!!.isVisibleTexts) paintText(g, position)
        this.position = position
    }

    override fun getCoordinate(): Coordinate {
        return coord
    }

    override fun getLat(): Double {
        return coord.lat
    }

    override fun getLon(): Double {
        return coord.lon
    }

    override fun getRadius(): Double {
        return 3.0
    }

    override fun getMarkerStyle(): STYLE? {
        return null
    }

    override fun toString(): String {
        return "MapMarker at $lat $lon"
    }

    override fun setLat(lat: Double) {
        coord.lat = lat
    }

    override fun setLon(lon: Double) {
        coord.lon = lon
    }

}