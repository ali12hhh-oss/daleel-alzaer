package com.daleelalzaer.app

import java.util.Calendar
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.sin

data class ShiaPrayerTimes(
    val fajr: Calendar,
    val dhuhr: Calendar,
    val maghrib: Calendar,
    val sunrise: Calendar,
    val sunset: Calendar
)

object ShiaPrayerCalculator {
    fun calculate(
        lat: Double,
        lng: Double,
        date: Calendar = Calendar.getInstance()
    ): ShiaPrayerTimes {
        val sunData = sun(julian(date))
        val solarNoon = 12.0 - lng / 15.0 - sunData.eq
        val fajrHourAngle = angleTime(18.0, lat, sunData.dec)
        val sunriseHourAngle = angleTime(0.833, lat, sunData.dec)
        val maghribHourAngle = angleTime(4.5, lat, sunData.dec)

        return ShiaPrayerTimes(
            fajr = utc(date, solarNoon - fajrHourAngle),
            dhuhr = utc(date, solarNoon),
            maghrib = utc(date, solarNoon + maghribHourAngle),
            sunrise = utc(date, solarNoon - sunriseHourAngle),
            sunset = utc(date, solarNoon + sunriseHourAngle)
        )
    }

    private data class Sun(
        val dec: Double,
        val eq: Double
    )

    private fun julian(c: Calendar): Double {
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH) + 1
        val day = c.get(Calendar.DAY_OF_MONTH)

        if (month <= 2) {
            year--
            month += 12
        }

        val a = floor(year / 100.0)
        val b = 2.0 - a + floor(a / 4.0)

        return floor(365.25 * (year + 4716)) +
            floor(30.6001 * (month + 1)) +
            day + b - 1524.5
    }

    private fun sun(jd: Double): Sun {
        val days = jd - 2451545.0
        val meanAnomaly = fix(357.529 + 0.98560028 * days)
        val meanLongitude = fix(280.459 + 0.98564736 * days)
        val eclipticLongitude = fix(
            meanLongitude +
                1.915 * sin(radians(meanAnomaly)) +
                0.020 * sin(radians(2.0 * meanAnomaly))
        )
        val obliquity = 23.439 - 0.00000036 * days

        var rightAscension = Math.toDegrees(
            atan2(
                cos(radians(obliquity)) * sin(radians(eclipticLongitude)),
                cos(radians(eclipticLongitude))
            )
        ) / 15.0
        rightAscension = fixHours(rightAscension)

        val declination = Math.toDegrees(
            asin(
                sin(radians(obliquity)) * sin(radians(eclipticLongitude))
            )
        )

        val equationOfTime = meanLongitude / 15.0 - rightAscension
        return Sun(declination, equationOfTime)
    }

    private fun angleTime(angle: Double, lat: Double, dec: Double): Double {
        val numerator =
            -sin(radians(angle)) -
                sin(radians(lat)) * sin(radians(dec))
        val denominator =
            cos(radians(lat)) * cos(radians(dec))
        val cosineHourAngle = (numerator / denominator).coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(cosineHourAngle)) / 15.0
    }

    private fun utc(c: Calendar, hours: Double): Calendar {
        val result = Calendar.getInstance()
        result.timeInMillis = c.timeInMillis
        result.set(Calendar.HOUR_OF_DAY, 0)
        result.set(Calendar.MINUTE, 0)
        result.set(Calendar.SECOND, 0)
        result.set(Calendar.MILLISECOND, 0)
        result.timeInMillis += round(hours * 60.0).toLong() * 60000L
        return result
    }

    private fun radians(value: Double): Double = value * PI / 180.0

    private fun fix(value: Double): Double {
        val result = value % 360.0
        return if (result < 0.0) result + 360.0 else result
    }

    private fun fixHours(value: Double): Double {
        val result = value % 24.0
        return if (result < 0.0) result + 24.0 else result
    }
}
