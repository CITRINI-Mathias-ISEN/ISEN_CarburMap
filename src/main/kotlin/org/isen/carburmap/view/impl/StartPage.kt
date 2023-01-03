package org.isen.carburmap.view.impl

import org.isen.carburmap.ctrl.CarburMapController
import org.isen.carburmap.data.Filters
import org.isen.carburmap.data.SearchData
import org.isen.carburmap.lib.geo.GeoCode
import org.isen.carburmap.view.ICarburMapView
import org.openstreetmap.gui.jmapviewer.Coordinate
import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.beans.PropertyChangeEvent
import javax.swing.*

class StartPage(var controller: CarburMapController) : JPanel(), ICarburMapView {
    // Search
    private val model = controller.model
    private val allCitiesArray: Array<SearchData>? = model.fetchAllCities()!!
    private val combo = JComboBox(allCitiesArray)
    private val comboIti = JComboBox(allCitiesArray)


    private val searchPanel = JPanel(BorderLayout())

    private val itineraryPanel = JPanel(BorderLayout())
    private val itineraryCheckbox = JCheckBox("Utiliser un itinéraire")

    // Fuel
    private val fuelPanel = JPanel(GridLayout(3, 2, 2, 2))

    // Others
    private val othersPanel = JPanel(GridLayout(2, 1, 2, 2))

    // Methods
    private val methodsPanel = JPanel(GridLayout(1, 2, 2, 2))

    // All
    private val box = Box.createVerticalBox()
    private var filters = Filters()

    private fun makeUI(): JPanel {
        box.add(makeSearchPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeItineraryPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeFuelPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeOthersPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeMethodsPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeButtonPanel())
        return JPanel(BorderLayout()).also {
            it.add(box, BorderLayout.NORTH)
            it.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
            it.preferredSize = Dimension(320, 400)
        }
    }

    private fun makeSearchPanel(): JPanel {
        combo.isEditable = true
        combo.selectedIndex = -1
        val field = combo.editor.editorComponent
        (field as? JTextField)?.text = ""
        field.addKeyListener(ComboKeyHandler(combo))

        searchPanel.border = BorderFactory.createTitledBorder("Search")
        searchPanel.add(combo, BorderLayout.NORTH)

        return searchPanel
    }

    private fun makeItineraryPanel(): JPanel {
        comboIti.isEditable = false
        comboIti.isEnabled = false
        comboIti.selectedIndex = -1
        val field = comboIti.editor.editorComponent
        (field as? JTextField)?.text = ""
        field.addKeyListener(ComboKeyHandler(comboIti))

        itineraryPanel.border = BorderFactory.createTitledBorder("Itinéraire")
        itineraryPanel.add(comboIti, BorderLayout.CENTER)
        itineraryCheckbox.addActionListener {e ->
            if(itineraryCheckbox.isSelected) {
                comboIti.isEnabled = true
                comboIti.isEditable = true
                comboIti.selectedIndex = -1
            } else {
                comboIti.isEnabled = false
                comboIti.isEditable = false
                comboIti.selectedIndex = -1
            }
        }
        itineraryPanel.add(itineraryCheckbox, BorderLayout.NORTH)

        return itineraryPanel
    }

    private fun makeFuelPanel(): JPanel {
        val fuelNameArray = arrayOf(
            "e10",
            "e85",
            "sp98",
            "gazole",
            "sp95",
            "gplc"
        )
        fuelPanel.border = BorderFactory.createTitledBorder("Fuel Type")
        val fuelCheckboxArray: Array<JCheckBox> = fuelNameArray.map { JCheckBox(it, false) }.toTypedArray()
        fuelCheckboxArray.forEach { fuelPanel.add(it) }
        return fuelPanel
    }

    private fun makeOthersPanel(): JPanel {
        val othersNameArray = arrayOf(
            "Toilet",
            "Food store",
            "Inflation station"
        )
        othersPanel.border = BorderFactory.createTitledBorder("Others")
        val othersCheckboxArray: Array<JCheckBox> = othersNameArray.map { JCheckBox(it, false) }.toTypedArray()
        othersCheckboxArray.forEach { othersPanel.add(it) }
        return othersPanel
    }

    private fun makeMethodsPanel(): JPanel {
        val methodsArray = arrayOf(
            "JSON",
            "XML"
        )
        methodsPanel.border = BorderFactory.createTitledBorder("Methods")
        val methodsCheckboxArray: Array<JCheckBox> = methodsArray.map { JCheckBox(it, false) }.toTypedArray()
        methodsCheckboxArray.forEach { methodsPanel.add(it) }
        methodsCheckboxArray[0].isSelected = true
        filters.json = true
        methodsCheckboxArray[0].addActionListener {
            if (methodsCheckboxArray[0].isSelected){
                filters.json = true
                filters.xml = false
                methodsCheckboxArray[1].isSelected = false
            }
            else if (!methodsCheckboxArray[0].isSelected && !methodsCheckboxArray[1].isSelected){
                filters.json = true
                filters.xml = false
                methodsCheckboxArray[0].isSelected = true
            }
        }
        methodsCheckboxArray[1].addActionListener {
            if (methodsCheckboxArray[1].isSelected){
                filters.json = false
                filters.xml = true
                methodsCheckboxArray[0].isSelected = false
            }
            else if (!methodsCheckboxArray[0].isSelected && !methodsCheckboxArray[1].isSelected){
                filters.json = true
                filters.xml = false
                methodsCheckboxArray[0].isSelected = true
            }
        }
        return methodsPanel
    }

    private fun makeButtonPanel(): JPanel {
        val buttonPanel = JPanel(GridLayout(1, 1, 2, 2))
        val searchButton = JButton("Search")
        searchButton.horizontalAlignment = SwingConstants.CENTER
        buttonPanel.add(searchButton)
        searchButton.addActionListener {
            // Fuel part
            filters.e10 = (fuelPanel.components[0] as JCheckBox).isSelected
            filters.e85 = (fuelPanel.components[1] as JCheckBox).isSelected
            filters.sp98 = (fuelPanel.components[2] as JCheckBox).isSelected
            filters.gazole = (fuelPanel.components[3] as JCheckBox).isSelected
            filters.sp95 = (fuelPanel.components[4] as JCheckBox).isSelected
            filters.gplc = (fuelPanel.components[5] as JCheckBox).isSelected

            // Others part
            filters.Toilet = (othersPanel.components[0] as JCheckBox).isSelected
            filters.FoodStore = (othersPanel.components[1] as JCheckBox).isSelected
            filters.InflationStation = (othersPanel.components[2] as JCheckBox).isSelected

            // Methods part
            filters.json = (methodsPanel.components[0] as JCheckBox).isSelected
            filters.xml = (methodsPanel.components[1] as JCheckBox).isSelected

            // Search part
            val res = checkSearch(combo, searchPanel)
            if(itineraryCheckbox.isSelected) {
                //rajouter retour value pair
                val resIt = checkSearch(comboIti, itineraryPanel)
                if(res.first && resIt.first && res.second != null && resIt.second != null){
                    controller.newItinerary(res.second!!.lat, res.second!!.lon, resIt.second!!.lat, resIt.second!!.lon)
                    controller.updateData(res.second!!.lat, res.second!!.lon, filters)
                }
            } else {
                if(res.first)
                    controller.updateData(res.second!!.lat, res.second!!.lon, filters)
            }


        }
        return buttonPanel
    }

    private fun checkSearch(combo: JComboBox<SearchData>, searchPanel: JPanel) : Pair<Boolean, Coordinate?> {
        val regex = Regex("([-+]?)(\\d{1,2})((\\.)(\\d+)(,))(\\s*)(([-+]?)(\\d{1,3})((\\.)(\\d+))?)\$")
        if (combo.selectedItem == null)
            searchPanel.border = BorderFactory.createTitledBorder("Search (Please select a city)")
        else {
            if (combo.selectedItem!!.toString().matches(regex)) {
                val lat = combo.selectedItem!!.toString().split(",")[0].toDouble()
                val lon = combo.selectedItem!!.toString().split(",")[1].toDouble()

                searchPanel.border = BorderFactory.createTitledBorder("Search")
                return Pair(true, Coordinate(lat, lon))
            } else {
                try {
                    val villeTmp = combo.selectedItem!!.toString()
                    val cityField: SearchData? =
                        if (combo.selectedItem is SearchData) combo.selectedItem as SearchData else allCitiesArray!!.find { it.displayName == villeTmp }
                    if (cityField != null) {
                        val lat = cityField.lat
                        val lon = cityField.lon

                        searchPanel.border = BorderFactory.createTitledBorder("Search")
                        return Pair(true, Coordinate(lat, lon))
                    } else {
                        searchPanel.border = BorderFactory.createTitledBorder("Search (City not found)")
                    }
                } catch (e: Exception) {
                    searchPanel.border = BorderFactory.createTitledBorder("Search (City not found)")
                }

            }
        }
        return Pair(false, null)
    }

    override fun display() {
        EventQueue.invokeLater {
            JFrame().apply {
                defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
                contentPane.add(makeUI())
                pack()
                isResizable = false
                setLocationRelativeTo(null)
                isVisible = true
            }
        }
    }

    override fun close() {
        TODO("Not yet implemented")
    }

    override fun propertyChange(evt: PropertyChangeEvent?) {}

}

private class ComboKeyHandler(private val comboBox: JComboBox<SearchData>) : KeyAdapter() {
    private val list = mutableListOf<SearchData>()
    private var extraSearchData: Pair<String, List<SearchData>?> = Pair("", mutableListOf<SearchData>())
    private val geoCode = GeoCode()

    private var shouldHide = false

    init {
        for (i in 0 until comboBox.model.size) {
            list.add(comboBox.getItemAt(i))
        }
    }

    override fun keyTyped(e: KeyEvent) {
        EventQueue.invokeLater {
            val text = (e.component as? JTextField)?.text ?: ""
            if (text.isEmpty()) {
                val m = DefaultComboBoxModel(list.toTypedArray())
                setSuggestionModel(comboBox, m, "")
                comboBox.hidePopup()
            } else {
                val m = getSuggestedModel(list, text)
                if (m.size == 0 || shouldHide) {
                    comboBox.hidePopup()
                } else {
                    setSuggestionModel(comboBox, m, text)
                    comboBox.showPopup()
                }
            }
        }
    }

    override fun keyPressed(e: KeyEvent) {
        val textField = e.component as? JTextField ?: return
        val text = textField.text
        shouldHide = false
        when (e.keyCode) {
            KeyEvent.VK_RIGHT -> for (s in list) {
                if (s.displayName.startsWith(text)) {
                    textField.text = s.displayName
                    return
                }
            }

            KeyEvent.VK_ENTER -> {
                val geoCodeResult = geoCode.getFromAddress(text)
                if(geoCodeResult.isNotEmpty()) {
                    extraSearchData = Pair(text.lowercase(), geoCodeResult.map { it.toSearchData() })
                }
                shouldHide = false
            }

            KeyEvent.VK_ESCAPE -> shouldHide = true
            // else -> {}
        }
    }

    private fun setSuggestionModel(cb: JComboBox<SearchData>, m: ComboBoxModel<SearchData>, txt: String) {
        cb.model = m
        cb.selectedIndex = -1
        (cb.editor.editorComponent as? JTextField)?.text = txt
    }

    private fun getSuggestedModel(list: List<SearchData>, text: String): ComboBoxModel<SearchData> {
        val m = DefaultComboBoxModel<SearchData>()
        if(extraSearchData.first == text.lowercase()) {
            extraSearchData.second?.forEach { m.addElement(it) }
        }
        for (s in list) {

            if (s.displayName.lowercase().contains(text.lowercase())) {
                m.addElement(s)
            }
        }
        return m
    }
}

