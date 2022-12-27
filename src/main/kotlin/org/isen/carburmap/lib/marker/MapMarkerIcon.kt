package org.isen.carburmap.lib.marker

import org.isen.carburmap.lib.icon.Icon
import org.isen.carburmap.lib.icon.IconManager
import org.openstreetmap.gui.jmapviewer.Coordinate
import org.openstreetmap.gui.jmapviewer.MapObjectImpl
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker.STYLE
import java.awt.*
import java.awt.geom.AffineTransform


open class MapMarkerIcon(var coord: Coordinate, imgPath : String = "./img/gas-station.png") : MapObjectImpl(null, null, null), MapMarker  {
    var img: Icon = IconManager.getInstance().getIcon(imgPath)
        private set
    private var rotation = 0.0
    protected var position: Point? = null
    protected var size: Dimension? = null

    override fun paint(g: Graphics, position: Point, radio: Int) {
        val imgToDraw = imageToDraw(radio)
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
        this.size = Dimension(imgToDraw.getWidth(null), imgToDraw.getHeight(null))
    }
    open fun imageToDraw(radio: Int) : Image {
        return getImageSizeToDraw(radio, img)
    }

    open fun getImageSizeToDraw(radio: Int, icon: Icon) : Image{
        var imgToDraw = icon.image
        if(radio < 1000) {
            imgToDraw = icon.smallImage
        } else if(radio > 400000) {
            imgToDraw = icon.bigImage
        }
        return imgToDraw
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