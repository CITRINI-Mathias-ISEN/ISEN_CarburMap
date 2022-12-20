package org.isen.carburmap.model

import java.beans.PropertyChangeListener

interface ICarburMapModel {
    fun register(datatype:String?,listener:PropertyChangeListener)
    fun unregister(listener:PropertyChangeListener)
    fun findStation(radius:Long)
    fun changeCurrentSelection(id:Long)
}