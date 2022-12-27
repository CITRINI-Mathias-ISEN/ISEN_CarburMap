package org.isen.carburmap.view

import java.beans.PropertyChangeListener

interface ICarburMapView : PropertyChangeListener {
    fun display()
    fun close()
}