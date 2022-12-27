import kotlin.math.*

class GeoDistanceHelper(val lat_a:Number, val lon_a:Number) {
    companion object {
        const val EARTH_DIAMETER = 6371e3
    }

    val lat_a_rad = lat_a.toDouble() * PI / 180
    val lon_a_rad = lon_a.toDouble() * PI / 180

    fun calculate(lat_b:Number, lon_b:Number): Double {
        val lat_b_rad = lat_b.toDouble() * PI / 180
        val lon_b_rad = lon_b.toDouble() * PI / 180
        val delta_lat = lat_b_rad - lat_a_rad
        val delta_lon = lon_b_rad - lon_a_rad
        val a = sin(delta_lat / 2) * sin(delta_lat / 2) +
                cos(lat_a_rad) * cos(lat_b_rad) *
                sin(delta_lon / 2) * sin(delta_lon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_DIAMETER * c
    }
}
