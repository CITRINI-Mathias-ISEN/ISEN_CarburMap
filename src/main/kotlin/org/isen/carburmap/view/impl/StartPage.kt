package example

import org.isen.carburmap.data.Field
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import java.awt.* // ktlint-disable no-wildcard-imports
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.* // ktlint-disable no-wildcard-imports

class StartPage : JPanel()  {
    // Search
    private val model = DefaultCarburmapModel()
    private val allCitiesArray : Array<Field>? = model.fetchAllCities()!!
    private val array : Array<String> = allCitiesArray!!.map { it.name }.distinct().toTypedArray()
    private val combo = JComboBox(array)

    // Fuel
    private val fuelPanel = JPanel(GridLayout(3, 2, 2, 2))

    // Others
    private val othersPanel = JPanel(GridLayout(2, 1, 2, 2))

    // All
    private val box = Box.createVerticalBox()

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

        val searchPanel = JPanel(BorderLayout())
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
        val fuelCheckboxArray: Array<JCheckBox> = fuelNameArray.map { JCheckBox(it, true) }.toTypedArray()
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
        val othersCheckboxArray: Array<JCheckBox> = othersNameArray.map { JCheckBox(it, true) }.toTypedArray()
        othersCheckboxArray.forEach { othersPanel.add(it) }
        return othersPanel
    }

    private fun makeButtonPanel(): JPanel {
        val buttonPanel = JPanel(GridLayout(1, 1, 2, 2))
        val searchButton = JButton("Search")
        searchButton.horizontalAlignment = SwingConstants.CENTER
        buttonPanel.add(searchButton)
        searchButton.addActionListener {
            println("Search bar : " + combo.selectedItem)
            // combo.selectedItem in array
            for (i in 0 until allCitiesArray!!.size) {
                if (allCitiesArray[i].name == combo.selectedItem) {
                    println("City found : " + allCitiesArray[i].name + " " + allCitiesArray[i].gps_lat + " " + allCitiesArray[i].gps_lng)
                    break
                }
            }
            println("Fuel : ")
            fuelPanel.components.forEach {
                if (it is JCheckBox) {
                    println(it.text + " : " + it.isSelected)
                }
            }
            println("Others : ")
            othersPanel.components.forEach {
                if (it is JCheckBox) {
                    println(it.text + " : " + it.isSelected)
                }
            }
        }
        return buttonPanel
    }

    @Deprecated("Deprecated in Java")
    fun display(){
        EventQueue.invokeLater {
            JFrame().apply {
                defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
                contentPane.add(makeUI())
                pack()
                setLocationRelativeTo(null)
                isVisible = true
            }
        }
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

fun main() {
    StartPage().display()
}
