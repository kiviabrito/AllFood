package com.kiviabrito.allfood.utils

class GeoUtils {
    companion object {
        private const val EQUATORIAL_EARTH_RADIUS = 6371.0
        private const val D2R = Math.PI / 180.0

        fun distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val lonDiff = (lon2 - lon1) * D2R
            val latDiff = (lat2 - lat1) * D2R
            val latSin = kotlin.math.sin(latDiff / 2.0)
            val lonSin = kotlin.math.sin(lonDiff / 2.0)
            val a = latSin * latSin + kotlin.math.cos(lat1 * D2R) * kotlin.math.cos(lat2 * D2R) * lonSin * lonSin
            val c = 2.0 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1.0 - a))
            return (EQUATORIAL_EARTH_RADIUS * c)
        }
    }
}