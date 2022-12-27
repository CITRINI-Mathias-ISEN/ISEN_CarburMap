package org.isen.carburmap.model

import org.isen.carburmap.data.Field
import java.beans.PropertyChangeListener

interface ICarburMapModel {
    enum class DataType {
        Stations, SelectedStation, Carto
    }
    fun register(datatype:DataType,listener:PropertyChangeListener)
    fun unregister(listener:PropertyChangeListener)
    fun findStationByJSON(lat:Double, lon:Double, radius:Long)

    fun findStationByXML(lat:Double, lon:Double, radius:Long)
    fun changeCurrentSelection(id:Long)
    fun fetchAllCities() : Array<Field>?
}