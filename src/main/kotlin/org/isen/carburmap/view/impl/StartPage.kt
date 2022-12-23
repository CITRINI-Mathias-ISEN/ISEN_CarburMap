package example

import java.awt.* // ktlint-disable no-wildcard-imports
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.* // ktlint-disable no-wildcard-imports

class StartPage : JPanel() {
    private fun makeUI() : JPanel{
        val box = Box.createVerticalBox()
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
        val searchPanel = JPanel(BorderLayout())
        searchPanel.border = BorderFactory.createTitledBorder("Search")
        searchPanel.add(JTextField(), BorderLayout.NORTH)
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
        val fuelPanel = JPanel(GridLayout(3, 2, 2, 2))
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
        val othersPanel = JPanel(GridLayout(2, 1, 2, 2))
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
            println("Search")
        }
        return buttonPanel
    }

    override fun show(){
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




