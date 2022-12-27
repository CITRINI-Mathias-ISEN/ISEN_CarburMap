package org.isen.carburmap.view.impl

import org.apache.logging.log4j.kotlin.Logging
import org.isen.carburmap.ctrl.CarburMapController
import org.isen.carburmap.data.StationsList
import org.isen.carburmap.lib.marker.MapMarkerStation
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
            selectionForeground = null // Nimbus
            selectionBackground = null // Nimbus
            cellRenderer = null
            super.updateUI()
            layoutOrientation = VERTICAL
            visibleRowCount = 0
            border = BorderFactory.createEmptyBorder(5, 10, 5, 10)
            cellRenderer = ListStationListCellRenderer(controller)
            selectionModel.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
        }
    }

    private val frame : JFrame = JFrame().apply {
        isVisible = false
        contentPane = this@MapView
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        this.title = "CarburMap"
        this.preferredSize = Dimension(700,500)
        this.pack()
    }

    private fun makeGUI() {
        this.layout = BorderLayout()
        val jSplitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createStationListPanel(), createStationMapPanel())
        jSplitPane.minimumSize = Dimension(100, 100)
        this.add(jSplitPane, BorderLayout.CENTER)

        frame.setSize(800, 600)
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
        println(evt?.propertyName)
        if(evt?.propertyName == ICarburMapModel.DataType.Stations.toString()) {
            if (evt.newValue !is StationsList) return
            synchronized(map) {
                (evt.newValue as StationsList).stations.forEach {
                    val markerIcon = MapMarkerStation(it, "./img/gas-station.png")
                    map.addMapMarker(markerIcon)
                    model.addElement(markerIcon)
                    println("Station at ${it.coordonnees[0]} ${it.coordonnees[1]}")
                }
                if(evt.oldValue == null) return
                (evt.oldValue as StationsList).stations.forEach { station ->
                    val toRemove = map.mapMarkerList.filter { (it is MapMarkerStation) && (it.station == station) }
                    map.mapMarkerList.removeAll(toRemove)
                    toRemove.forEach(model::removeElement)
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
                    }
                }
            }
        }
    }

    private class ListStationListCellRenderer(val controller: CarburMapController) : ListCellRenderer<MapMarkerStation> {
        private val renderer = JPanel(BorderLayout())
        private val icon = JLabel(null as? Icon?, SwingConstants.CENTER)
        private val label = JLabel("", SwingConstants.CENTER)
        private val focusBorder = UIManager.getBorder("List.focusCellHighlightBorder")
        private val noFocusBorder = UIManager.getBorder("List.noFocusBorder") ?: getSynthNoFocusBorder()
        private var fieldComp : HashMap<String, JLabel> = HashMap()

        init {
            icon.isOpaque = false
            icon.maximumSize = Dimension(32, 32)
            label.foreground = renderer.foreground
            label.background = renderer.background
            label.border = noFocusBorder
            renderer.isOpaque = false
            renderer.border = BorderFactory.createEmptyBorder(2, 2, 2, 2)
            renderer.add(icon, BorderLayout.WEST)
            renderer.add(label, BorderLayout.SOUTH)
            val box = Box.createVerticalBox()
            box.add(makeGridBagLayoutPanel("address","Addresse :" , JLabel("")))
            box.add(makeGridBagLayoutPanel("city","Ville :" , JLabel("")))
            box.add(makeGridBagLayoutPanel("cp","Code postal :" , JLabel("")))

            renderer.add(box, BorderLayout.CENTER)
        }

        fun makeGridBagLayoutPanel(atrName : String, label: String, comp: JLabel): Component {
            val c = GridBagConstraints()
            val panel = JPanel(GridBagLayout())
            val GAP = 5
            fieldComp[atrName] = comp

            c.insets = Insets(GAP, GAP, GAP, 0)
            c.anchor = GridBagConstraints.LINE_END
            panel.add(JLabel(label), c)

            c.weightx = 1.0
            c.fill = GridBagConstraints.HORIZONTAL
            panel.add(comp, c)

            return panel
        }

        private fun getSynthNoFocusBorder(): Border {
            val i = focusBorder.getBorderInsets(label)
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
                label.text = value.station.id.toString()
                label.border = if (cellHasFocus) focusBorder else noFocusBorder
                if (isSelected) {
                    icon.icon = ImageIcon(value.selectedIcon.image)
                    label.foreground = list.selectionForeground
                    label.background = list.selectionBackground
                    label.isOpaque = true
                    if (controller.model is DefaultCarburmapModel) {
                        controller.model.selectedMapMarkerStation = value
                    }

                } else {
                    icon.icon = ImageIcon(value.img.image)
                    label.foreground = list.foreground
                    label.background = list.background
                    label.isOpaque = false
                }
                fieldComp["address"]?.text = value.station.adresse
                fieldComp["city"]?.text = value.station.ville
                fieldComp["cp"]?.text = value.station.cp
            }
            return renderer
        }
    }

    override fun mouseClicked(e: MouseEvent?) {
        println("Mouse clicked")
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