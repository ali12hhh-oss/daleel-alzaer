package com.daleelalzaer.app

import java.util.Calendar
import kotlin.math.*

data class ShiaPrayerTimes(val fajr:Calendar,val dhuhr:Calendar,val maghrib:Calendar,val sunrise:Calendar,val sunset:Calendar)

object ShiaPrayerCalculator {
    fun calculate(lat:Double,lng:Double,date:Calendar=Calendar.getInstance()):ShiaPrayerTimes{
        val sun=sunPosition(julian(date));val dhuhr=12.0-lng/15.0-sun.eq
        val fajrT=angleTime(18.0,lat,sun.dec);val rise=angleTime(.833,lat,sun.dec);val mag=angleTime(4.5,lat,sun.dec)
        return ShiaPrayerTimes(utc(date,dhuhr-fajrT),utc(date,dhuhr),utc(date,dhuhr+mag),utc(date,dhuhr-rise),utc(date,dhuhr+rise))
    }
    private data class Sun(val dec:Double,val eq:Double)
    private fun julian(c:Calendar):Double{var y=c.get(Calendar.YEAR);var m=c.get(Calendar.MONTH)+1;val d=c.get(Calendar.DAY_OF_MONTH);if(m<=2){y--;m+=12};val a=floor(y/100.0);val b=2-a+floor(a/4.0);return floor(365.25*(y+4716))+floor(30.6001*(m+1))+d+b-1524.5}
    private fun sun(jd:Double):Sun{val d=jd-2451545.0;val g=fix(357.529+.98560028*d);val q=fix(280.459+.98564736*d);val l=fix(q+1.915*sin(r(g))+.020*sin(r(2*g)));val e=23.439-.00000036*d;var ra=Math.toDegrees(atan2(cos(r(e))*sin(r(l)),cos(r(l))))/15.0;ra=fixh(ra);return Sun(Math.toDegrees(asin(sin(r(e))*sin(r(l)))),q/15.0-ra)}
    private fun angleTime(a:Double,lat:Double,dec:Double):Double{val n=-sin(r(a))-sin(r(lat))*sin(r(dec));val d=cos(r(lat))*cos(r(dec));return Math.toDegrees(acos((n/d).coerceIn(-1.0,1.0)))/15.0}
    private fun utc(c:Calendar,h:Double):Calendar{val o=Calendar.getInstance();o.timeInMillis=c.timeInMillis;o.set(Calendar.HOUR_OF_DAY,0);o.set(Calendar.MINUTE,0);o.set(Calendar.SECOND,0);o.set(Calendar.MILLISECOND,0);o.timeInMillis+=round(h*60.0).toLong()*60000L;return o}
    private fun r(v:Double)=v*Math.PI/180.0
    private fun fix(v:Double):Double{val x=v%360.0;return if(x<0)x+360 else x}
    private fun fixh(v:Double):Double{val x=v%24.0;return if(x<0)x+24 else x}
}
