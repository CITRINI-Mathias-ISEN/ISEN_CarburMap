package org.isen.carburmap.data

/**
 * Class including all the filters used to filter the stations
 * @property e10 if the filter for E10 is active
 * @property e85 if the filter for SP95 is active
 * @property sp95 if the filter for SP95 is active
 * @property sp98 if the filter for SP98 is active
 * @property gazole if the filter for Gazole is active
 * @property gplc if the filter for GPLc is active
 * @property Toilet if the filter for Toilettes is active
 * @property FoodStore if the filter for Snack is active
 * @property InflationStation if the filter for Pneu is active
 * @property json if th search must be done with the json API
 * @property xml if th search must be done in the xml file
 */
data class Filters (
    var e10: Boolean = false,
    var e85: Boolean = false,
    var sp98: Boolean = false,
    var gazole: Boolean = false,
    var sp95: Boolean = false,
    var gplc: Boolean = false,
    var Toilet: Boolean = false,
    var FoodStore: Boolean = false,
    var InflationStation : Boolean = false,
    var json: Boolean = false,
    var xml: Boolean = false
)