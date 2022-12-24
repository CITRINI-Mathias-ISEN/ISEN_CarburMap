package org.isen.carburmap.model

import java.beans.PropertyChangeListener

interface ICarburMapModel {
    fun register(datatype:String?,listener:PropertyChangeListener)
    fun unregister(listener:PropertyChangeListener)
    fun findStationByJSON(x:Double, y:Double, radius:Long)

    fun findStationByXML(x:Double, y:Double, radius:Long)
    fun changeCurrentSelection(id:Long)
    fun fetchAllCities()
}