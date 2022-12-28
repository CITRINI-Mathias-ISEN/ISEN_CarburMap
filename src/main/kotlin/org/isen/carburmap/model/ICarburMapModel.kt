package org.isen.carburmap.model

import org.isen.carburmap.data.*
import java.beans.PropertyChangeListener

interface ICarburMapModel {
    enum class DataType {
        Stations, SelectedStation, Carto
    }
    fun register(datatype:DataType,listener:PropertyChangeListener)
    fun unregister(listener:PropertyChangeListener)
    fun findStationByJSON(lat:Double, lon:Double, filters: Filters)

    fun findStationByXML(lat:Double, lon:Double, filters: Filters)
    fun changeCurrentSelection(id:Long)
    fun fetchAllCities() : Array<Ville>?
    fun filtrage(filters: Filters)
}