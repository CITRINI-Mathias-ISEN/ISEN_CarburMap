package org.isen.carburmap.view.impl

import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.ctrl.CarburMapController
import org.isen.carburmap.data.Prix
import org.isen.carburmap.data.StationsList
import org.isen.carburmap.lib.icon.IconManager
import org.isen.carburmap.lib.marker.MapMarkerStation
import org.isen.carburmap.lib.routing.MapPath
import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import org.isen.carburmap.view.ICarburMapView
import org.openstreetmap.gui.jmapviewer.JMapViewer
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.beans.PropertyChangeEvent
import javax.swing.*
import javax.swing.border.Border

class MapView(val controller: CarburMapController) : JPanel(), ICarburMapView, MouseListener {
    companion object : Logging

    private var map: JMapViewer = JMapViewer()

    private val model = DefaultListModel<MapMarkerStation>()
    private val list = object : JList<MapMarkerStation>(model) {
        override fun updateUI() {
            selectionForeground = null
            selectionBackground = null
            cellRenderer = null
            super.updateUI()
            layoutOrientation = VERTICAL
            visibleRowCount = 0
            border = BorderFactory.createEmptyBorder(5, 10, 15, 10)
            cellRenderer = ListStationListCellRenderer(controller)
            selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        }
    }

    private val frame : JFrame = JFrame().apply {
        isVisible = false
        contentPane = this@MapView
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.title = "CarburMap"
        this.preferredSize = Dimension(900,600)
        this.pack()
    }

    private fun makeGUI() {
        this.layout = BorderLayout()
        val jSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createStationListPanel(), createStationMapPanel())
        jSplitPane.minimumSize = Dimension(100, 100)
        this.add(jSplitPane, BorderLayout.CENTER)
        frame.setSize(1080, 720)
        frame.isVisible = true
        map.addMouseListener(this)
    }

    private fun createStationMapPanel(): Component {
        return JScrollPane(map)
    }

    private fun createStationListPanel(): JScrollPane {
        val scroll = JScrollPane(list)
        scroll.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        scroll.verticalScrollBar.unitIncrement = 25
        return scroll
    }

    override fun display() {
        this.makeGUI()
        this.controller.registerViewToCarburMapData(this)
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun propertyChange(evt: PropertyChangeEvent?) {
        if(evt?.propertyName == ICarburMapModel.DataType.Stations.toString()) {
            synchronized(map) {
                if(evt.oldValue != null && evt.oldValue is StationsList) {
                    (evt.oldValue as StationsList).stations.forEach { station ->
                        val toRemove = map.mapMarkerList.filter { (it is MapMarkerStation) && (it.station == station) }
                        map.mapMarkerList.removeAll(toRemove.toSet())
                        map.repaint()
                        if (controller.model is DefaultCarburmapModel) {
                            controller.model.selectedMapMarkerStation = null
                        }
                        toRemove.forEach(model::removeElement)
                    }
                }
                if(evt.newValue != null && evt.newValue is StationsList) {
                    (evt.newValue as StationsList).stations.forEach {
                        val markerIcon = MapMarkerStation(it, "./img/gas-station.png")
                        map.addMapMarker(markerIcon)
                        model.addElement(markerIcon)
                    }
                }
            }
        }

        if (evt?.propertyName == ICarburMapModel.DataType.SelectedStation.toString()) {
            if (evt.newValue !is MapMarkerStation) return
            synchronized(map) {
                evt.oldValue?.let {
                    if (it is MapMarkerStation) {
                        map.removeMapMarker(it)
                        it.isSelected = false
                        map.addMapMarker(it)
                    }
                }
                evt.newValue?.let {
                    if (it is MapMarkerStation) {
                        map.removeMapMarker(it)
                        it.isSelected = true
                        map.addMapMarker(it)
                        val rectangle: Rectangle = model.indexOf(it).let { index ->
                            list.getCellBounds(index, index)
                        }
                        list.scrollRectToVisible(rectangle)
                    }
                }
            }
        }

        if (evt?.propertyName == ICarburMapModel.DataType.Itinerary.toString()) {
            synchronized(map) {
                if(evt.oldValue != null) {
                    evt.oldValue?.let {
                        if (it is MapPath) {
                            map.removeMapPolygon(it)
                        }
                    }
                }
                if(evt.newValue!=null) {
                    evt.newValue?.let {
                        if (it is MapPath) {
                            map.addMapPolygon(it)
                        }
                    }
                }
            }
        }
    }

    private class ListStationListCellRenderer(val controller: CarburMapController) : ListCellRenderer<MapMarkerStation> {
        private val renderer = JPanel(BorderLayout())
        private val icon = JLabel(null as? Icon?, SwingConstants.CENTER)
        private val focusBorder = UIManager.getBorder("Panel.focusCellHighlightBorder") ?: BorderFactory.createEmptyBorder(1, 1, 1, 1)
        private val noFocusBorder = UIManager.getBorder("List.noFocusBorder") ?: getSynthNoFocusBorder()
        private var fieldComp : HashMap<String, JLabel> = HashMap()
        private val box: Box = Box.createVerticalBox()
        private val prixList: JList<Prix> = object : JList<Prix>() {
            override fun updateUI() {
                super.updateUI()
                isOpaque = true
                background = Color.WHITE
                layoutOrientation = HORIZONTAL_WRAP
                visibleRowCount = 0
                border = BorderFactory.createEmptyBorder(5, 0, 5, 10)
                cellRenderer = PrixListCellRenderer()
                selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            }
        }

        init {
            icon.isOpaque = false
            icon.maximumSize = Dimension(32, 32)
            renderer.border = noFocusBorder
            renderer.isOpaque = true
            renderer.border = BorderFactory.createEmptyBorder(2, 0, 2, 10)
            renderer.add(icon, BorderLayout.WEST)
            box.add(makeGridBagLayoutPanel("address","Addresse :" , JLabel("")))
            box.add(makeGridBagLayoutPanel("city","Ville :" , JLabel("")))
            box.add(makeGridBagLayoutPanel("cp","Code postal :" , JLabel("")))
            val listPanel = JPanel(BorderLayout())
            listPanel.add(prixList)
            listPanel.isOpaque = true
            val globalBox: JPanel = JPanel().apply {
                layout = BorderLayout()
                add(box, BorderLayout.CENTER)
                box.maximumSize = Dimension(800, 90)
                add(listPanel, BorderLayout.SOUTH)
                isOpaque = true
            }
            renderer.add(globalBox, BorderLayout.CENTER)
        }

        fun makeGridBagLayoutPanel(atrName : String, label: String, comp: JLabel): Component {
            val c = GridBagConstraints()
            val panel = JPanel(GridBagLayout())
            val gap = 5
            fieldComp[atrName] = comp

            c.insets = Insets(gap, gap, gap, 0)
            c.anchor = GridBagConstraints.LINE_END
            panel.add(JLabel(label), c)

            c.weightx = 1.0
            c.fill = GridBagConstraints.HORIZONTAL
            panel.add(comp, c)
            panel.maximumSize = Dimension(800, 30)

            return panel
        }

        private fun getSynthNoFocusBorder(): Border {
            val i = focusBorder.getBorderInsets(renderer)
            return BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right)
        }

        override fun getListCellRendererComponent(
            list: JList<out MapMarkerStation>,
            value: MapMarkerStation?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            if (value != null) {
                renderer.border = if (cellHasFocus) focusBorder else noFocusBorder
                if (isSelected) {
                    icon.icon = ImageIcon(value.selectedIcon.image)
                    renderer.border = focusBorder
                    renderer.foreground = list.selectionForeground
                    renderer.background = list.selectionBackground
                    renderer.isOpaque = true
                    if (controller.model is DefaultCarburmapModel) {
                        controller.model.selectedMapMarkerStation = value
                    }

                } else {
                    icon.icon = ImageIcon(value.img.image)
                    renderer.foreground = list.foreground
                    renderer.background = list.background
                    renderer.border = noFocusBorder
                    renderer.isOpaque = false
                }
                fieldComp["address"]?.text = value.station.adresse
                fieldComp["city"]?.text = value.station.ville
                fieldComp["cp"]?.text = value.station.cp
                prixList.model = DefaultListModel<Prix>().apply {
                    value.station.prix?.forEach { prix ->
                        addElement(prix)
                    }
                }
            }
            return renderer
        }
    }

    class PrixListCellRenderer : ListCellRenderer<Prix> {
        private val renderer = JPanel(BorderLayout())
        private val label = JLabel("", SwingConstants.CENTER)
        private val focusBorder = UIManager.getBorder("List.focusCellHighlightBorder")
        private val noFocusBorder = UIManager.getBorder("List.noFocusBorder")
        private val iconManager = IconManager.getInstance()

        init {
            renderer.isOpaque = false
            renderer.border = BorderFactory.createEmptyBorder(0, 0, 0, 0)
        }

        override fun getListCellRendererComponent(
            list: JList<out Prix>?,
            value: Prix,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean
        ): Component {
            val panel = JPanel(BorderLayout())

            val image = iconManager.getSimpleIcon("./img/Gas/" + value.carburant + ".png", 32)
            val icon = JLabel(null as? Icon?, SwingConstants.CENTER)
            icon.icon = ImageIcon(image.image)
            icon.border = BorderFactory.createEmptyBorder(0, 0, 0, 10)
            panel.border = BorderFactory.createEmptyBorder(5, 1, 0, 10)
            panel.add(icon, BorderLayout.WEST)

            val namePanel = JPanel(BorderLayout())
            namePanel.isOpaque = false


            val nameLabel = JLabel("<html><b>${value.carburant}</b></html>")
            nameLabel.isOpaque = false
            nameLabel.horizontalAlignment = JLabel.RIGHT
            namePanel.add(nameLabel, BorderLayout.CENTER)

            val priceLabel = JLabel("${value.valeur} €")
            namePanel.add(priceLabel, BorderLayout.SOUTH)
            panel.add(namePanel, BorderLayout.CENTER)
            panel.isOpaque = false

            return panel
        }

    }

    override fun mouseClicked(e: MouseEvent?) {
        if (e == null) return
        val marker = map.mapMarkerList.filterIsInstance<MapMarkerStation>().firstOrNull {
            it.mouseClicked(e)
        }
        if (marker != null) {
            if (controller.model is DefaultCarburmapModel) {
                controller.model.selectedMapMarkerStation = marker
                list.setSelectedValue(marker, false)
                list.updateUI()
            }
        }
    }

    override fun mousePressed(e: MouseEvent?) {}

    override fun mouseReleased(e: MouseEvent?) {}

    override fun mouseEntered(e: MouseEvent?) {}

    override fun mouseExited(e: MouseEvent?) {}

}