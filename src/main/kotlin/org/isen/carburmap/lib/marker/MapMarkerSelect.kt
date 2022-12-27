package org.isen.carburmap.lib.marker

import org.isen.carburmap.lib.icon.Icon
import org.isen.carburmap.lib.icon.IconManager
import org.openstreetmap.gui.jmapviewer.Coordinate
import java.awt.Image
import java.awt.event.MouseEvent

open class MapMarkerSelect(coord: Coordinate, imgPath: String, imgSelectPath: String = "./img/gas-station-selected.png") : MapMarkerIcon(coord, imgPath) {
    var selectedIcon: Icon = IconManager.getInstance().getIcon(imgSelectPath)
        private set
    var isSelected : Boolean = false
    override fun imageToDraw(radio: Int): Image {
        if (!isSelected) {
            return super.imageToDraw(radio)
        }
        return getImageSizeToDraw(radio, selectedIcon)
    }

    fun mouseClicked(e: MouseEvent, autoEditSelectStatus: Boolean = false) : Boolean {
        if (position == null || size == null) return false
        val p = e.point
        val halfWidth = size!!.width / 2
        if (p.x >= position!!.x - halfWidth && p.x <= position!!.x + halfWidth && p.y >= position!!.y - size!!.height && p.y <= position!!.y) {
            if (autoEditSelectStatus) isSelected = !isSelected
            return true
        }
        return false
    }
}