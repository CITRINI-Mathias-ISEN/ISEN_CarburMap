import org.openstreetmap.gui.jmapviewer.JMapViewer
import org.openstreetmap.gui.jmapviewer.MapMarkerDot
import org.isen.carburmap.model.impl.DefaultCarburmapModel
import org.isen.carburmap.model.ICarburMapModel
import org.isen.carburmap.ctrl.CarburMapController
import java.awt.*
import javax.swing.*

fun main() {
    val frame = JFrame("Carte interactive mieux que Fressel")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    val map = JMapViewer()
    val panel = JPanel(BorderLayout())
    panel.add(JScrollPane(map))
    frame.add(panel)
    frame.setSize(800, 600)
    frame.isVisible = true
    val marker = MapMarkerDot(48.8567, 2.3508)
    marker.color = Color.RED;
    marker.name = "Paris"
    map.addMapMarker(marker)
    val model = DefaultCarburmapModel()
    model.findStation(1000)
}