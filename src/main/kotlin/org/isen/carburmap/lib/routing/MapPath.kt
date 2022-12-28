package org.isen.carburmap.lib.routing

import com.graphhopper.ResponsePath
import org.openstreetmap.gui.jmapviewer.Coordinate
import org.openstreetmap.gui.jmapviewer.MapObjectImpl
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon
import java.awt.*
import java.awt.geom.Path2D

class MapPath(private var path : ResponsePath) : MapObjectImpl(null, null, null), MapPolygon {
    private val points = path.points.map { Coordinate(it.lat, it.lon) }
    private var isMainPoint = true
    override fun getPoints(): List<Coordinate> {
        return points
    }

    override fun paint(g: Graphics, points: MutableList<Point>?) {
        if(points == null) return
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        val p2: Path2D = Path2D.Double()
        isMainPoint = true
        for (p in points) {
            draw(p2, p)
        }
        g2.color = Color(25, 102, 208)
        g2.stroke = BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.draw(p2)

        g2.color = Color(0, 175, 253)
        g2.stroke = BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        g2.draw(p2)
        g2.dispose()
    }

    private fun draw(p2: Path2D, p: Point) {
        if(isMainPoint) {
            p2.moveTo(p.x.toDouble(), p.y.toDouble())
            isMainPoint = false
        } else {
            p2.lineTo(p.x.toDouble(), p.y.toDouble())
        }
    }

    override fun paint(g: Graphics?, polygon: Polygon?) {
        TODO("Not yet implemented")
    }
}