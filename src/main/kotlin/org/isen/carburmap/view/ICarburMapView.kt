package org.isen.carburmap.view

import java.beans.PropertyChangeListener

interface ICarburMapView : PropertyChangeListener {
    /**
     * Display a view
     */
    fun display()

    /**
     * Close the view
     */
    fun close()
}