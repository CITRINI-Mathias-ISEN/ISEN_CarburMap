package example

import org.isen.carburmap.ctrl.CarburMapController
import org.isen.carburmap.data.Field
import org.isen.carburmap.data.Filters
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import org.isen.carburmap.view.impl.MapView
import java.awt.* // ktlint-disable no-wildcard-imports
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.* // ktlint-disable no-wildcard-imports

class StartPage(var controller: CarburMapController, var view: MapView) : JPanel()  {
    // Search
    private val model = DefaultCarburmapModel()
    private val allCitiesArray : Array<Field>? = model.fetchAllCities()!!
    private val array : Array<String> = allCitiesArray!!.map { it.name }.distinct().toTypedArray()
    private val combo = JComboBox(array)

    private val searchPanel = JPanel(BorderLayout())

    // Fuel
    private val fuelPanel = JPanel(GridLayout(3, 2, 2, 2))

    // Others
    private val othersPanel = JPanel(GridLayout(2, 1, 2, 2))

    // All
    private val box = Box.createVerticalBox()
    private var filters = Filters()

    init {
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
        controller.registerViewToCarburMapData(view)
    }
    private fun makeUI() : JPanel{
        box.add(makeSearchPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeFuelPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeOthersPanel())
        box.add(Box.createVerticalStrut(5))
        box.add(makeButtonPanel())
        return JPanel(BorderLayout()).also {
            it.add(box, BorderLayout.NORTH)
            it.border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
            it.preferredSize = Dimension(320, 275)
        }
    }

    private fun makeSearchPanel(): JPanel{
        combo.isEditable = true
        combo.selectedIndex = -1
        val field = combo.editor.editorComponent
        (field as? JTextField)?.text = ""
        field.addKeyListener(ComboKeyHandler(combo))

        searchPanel.border = BorderFactory.createTitledBorder("Search")
        searchPanel.add(combo, BorderLayout.NORTH)

        return searchPanel
    }

    private fun makeFuelPanel(): JPanel{
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

    private fun makeOthersPanel(): JPanel{
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
            println(filters)

            // Search part
            val regex = Regex("([-+]?)([\\d]{1,2})(((\\.)(\\d+)(,)))(\\s*)(([-+]?)([\\d]{1,3})((\\.)(\\d+))?)\$")
            var lat = 0.0
            var lon = 0.0

            if (combo.selectedItem == null)
                searchPanel.border = BorderFactory.createTitledBorder("Search (Please select a city)")
            else {
                if (combo.selectedItem!!.toString().matches(regex)){
                    lat = combo.selectedItem!!.toString().split(",")[0].toDouble()
                    lon = combo.selectedItem!!.toString().split(",")[1].toDouble()
                }
                else {
                    val cityField = allCitiesArray!!.find { it.name == combo.selectedItem!!.toString() }
                    if (cityField != null) {
                        lat = cityField.gps_lat
                        lon = cityField.gps_lng
                    }
                    else {
                        searchPanel.border = BorderFactory.createTitledBorder("Search (Please select a city)")
                        return@addActionListener
                    }
                }
                searchPanel.border = BorderFactory.createTitledBorder("Search")
                controller.displayViews(lat, lon, filters)
            }


        }
        return buttonPanel
    }

}

private class ComboKeyHandler(private val comboBox: JComboBox<String>) : KeyAdapter() {
    private val list = mutableListOf<String>()
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
                if (s.startsWith(text)) {
                    textField.text = s
                    return
                }
            }
            KeyEvent.VK_ENTER -> {
                if (!list.contains(text)) {
                    list.add(text)
                    list.sort()
                    setSuggestionModel(comboBox, getSuggestedModel(list, text), text)
                }
                shouldHide = true
            }
            KeyEvent.VK_ESCAPE -> shouldHide = true
            // else -> {}
        }
    }

    private fun setSuggestionModel(cb: JComboBox<String>, m: ComboBoxModel<String>, txt: String) {
        cb.model = m
        cb.selectedIndex = -1
        (cb.editor.editorComponent as? JTextField)?.text = txt
    }

    private fun getSuggestedModel(list: List<String>, text: String): ComboBoxModel<String> {
        val m = DefaultComboBoxModel<String>()
        for (s in list) {
            if (s.startsWith(text)) {
                m.addElement(s)
            }
        }
        return m
    }
}

