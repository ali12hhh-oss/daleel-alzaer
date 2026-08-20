package com.daleelalzaer.app

import android.content.Context
import kotlinx.serialization.json.Json
import java.util.regex.Pattern

object LegacySourceRepository {
    private fun read(c:Context,path:String)=runCatching{c.assets.open("migration_source/$path").bufferedReader().use{it.readText()}}.getOrDefault("")
    private fun str(v:String):String{
        val x=v.trim().removeSuffix(",").trim();val out=StringBuilder();var i=0
        while(i<x.length){when{ x.startsWith("'''",i)->{val e=x.indexOf("'''",i+3);if(e<0)break;out.append(x.substring(i+3,e));i=e+3}
            x[i]=='\''->{var j=i+1;var esc=false;while(j<x.length){val ch=x[j];if(esc)esc=false else if(ch=='\\')esc=true else if(ch=='\'')break;j++};if(j>=x.length)break;out.append(x.substring(i+1,j).replace("\\'","'").replace("\\n","\n").replace("\\t","\t"));i=j+1}
            else->i++}}
        return out.toString().trim()
    }
    private fun blocks(t:String,ctor:String):List<String>{val r=Regex("(?m)^\\s*${Pattern.quote(ctor)}\\s*\\(");val out=mutableListOf<String>();for(m in r.findAll(t)){val s=t.indexOf('(',m.range.first);var d=0;var q=false;var tr=false;var esc=false;var i=s;while(i<t.length){if(tr){if(t.startsWith("'''",i)){tr=false;i+=3;continue};i++;continue};val c=t[i];if(q){if(esc)esc=false else if(c=='\\')esc=true else if(c=='\'')q=false;i++;continue};if(t.startsWith("'''",i)){tr=true;i+=3;continue};if(c=='\''){q=true;i++;continue};if(c=='(')d++;if(c==')'){d--;if(d==0){out+=t.substring(m.range.first,i+1);break}};i++}};return out}
    private fun field(b:String,n:String):String?{val m=Regex("(?m)^\\s*${Pattern.quote(n)}\\s*:").find(b)?:return null;val s=m.range.last+1;val next=Regex("(?m)^\\s*[A-Za-z_]\\w*\\s*:").find(b,s);return b.substring(s,next?.range?.first?:b.lastIndexOf(')')).trim().removeSuffix(",").trim()}
    fun battle(c:Context)=blocks(read(c,"data/battle_data.dart"),"BattleEvent").mapNotNull{val day=field(it,"day")?.trim()?.toIntOrNull()?:return@mapNotNull null;BattleEventEntry(day,str(field(it,"title")?:""),str(field(it,"description")?:""),str(field(it,"source")?:""))}
    fun quotes(c:Context):List<QuoteEntry>{val t=read(c,"data/hussein_quotes_data.dart").substringBefore("const List<Quote> mawaddaQuotes");return blocks(t,"Quote").map{QuoteEntry(str(field(it,"text")?:""),str(field(it,"source")?:""),str(field(it,"occasion")?:""))}}
    fun mawadda(c:Context):List<QuoteEntry>{val t=read(c,"data/hussein_quotes_data.dart").substringAfter("const List<Quote> mawaddaQuotes","");return blocks(t,"Quote").map{QuoteEntry(str(field(it,"text")?:""),str(field(it,"source")?:""),str(field(it,"occasion")?:""))}}
    fun sabaya(c:Context)=blocks(read(c,"data/sabaya_data.dart"),"Quote").map{QuoteEntry(str(field(it,"text")?:""),str(field(it,"source")?:""),str(field(it,"occasion")?:""))}
    fun dates(c:Context)=blocks(read(c,"data/ahlulbayt_dates_data.dart"),"AhlulBaytEvent").map{AhlulBaytEventEntry(str(field(it,"personName")?:""),field(it,"kind")?:"",str(field(it,"description")?:""),str(field(it,"source")?:""))}
    fun scholars(c:Context)=blocks(read(c,"data/scholars_data.dart"),"Scholar").map{ScholarEntry(str(field(it,"id")?:""),str(field(it,"name")?:""),str(field(it,"title")?:""),str(field(it,"officialSite")?:""),str(field(it,"istiftaUrl")?:""),field(it,"isLiving")?.trim()?.startsWith("true")==true,field(it,"hasRss")?.trim()?.startsWith("true")==true)}
    fun cities(c:Context):List<CityEntry>{val r=Regex("IraqiCity\\(name:\\s*'((?:\\\\.|[^'])*)',\\s*lat:\\s*([-\\d.]+),\\s*lng:\\s*([-\\d.]+),\\s*approxDistanceKm:\\s*([-\\d.]+)");return r.findAll(read(c,"data/cities_data.dart")).map{m->CityEntry(m.groupValues[1],m.groupValues[2].toDouble(),m.groupValues[3].toDouble(),m.groupValues[4].toDouble())}.toList()}
    fun sacredPlaces(c:Context):List<SacredPlaceEntry>{val r=Regex("SacredPlace\\(\\s*name:\\s*'((?:\\\\.|[^'])*)',\\s*subtitle:\\s*'((?:\\\\.|[^'])*)',\\s*lat:\\s*([-\\d.]+),\\s*lng:\\s*([-\\d.]+)",RegexOption.DOT_MATCHES_ALL);return r.findAll(read(c,"screens/route_screen.dart")).map{m->SacredPlaceEntry(m.groupValues[1],m.groupValues[2],m.groupValues[3].toDouble(),m.groupValues[4].toDouble())}.toList()}
    fun ziarat(c:Context)=maps(read(c,"screens/ziarat_screen.dart"))
    fun duas(c:Context)=maps(read(c,"screens/duas_screen.dart"))
    private fun maps(t:String):List<ZiaratEntry>{val out=mutableListOf<ZiaratEntry>();var p=0;while(true){val a=t.indexOf("'title':",p);if(a<0)break;val b=t.indexOf("'subtitle':",a);val cc=t.indexOf("'content':",b);if(b<0||cc<0)break;val tm=Regex("'title':\\s*'((?:\\\\.|[^'])*)'").find(t,a)?:break;val sm=Regex("'subtitle':\\s*'((?:\\\\.|[^'])*)'").find(t,b)?:break;val s=t.indexOf("'''",cc);if(s<0)break;val e=t.indexOf("'''",s+3);if(e<0)break;out+=ZiaratEntry(str("'${tm.groupValues[1]}'"),str("'${sm.groupValues[1]}'"),t.substring(s+3,e).trim());p=e+3};return out}
    fun questionCategories(c:Context):List<String>{val b=read(c,"data/questions_data.dart").substringAfter("const List<String> questionCategories","").substringBefore("];","");return Regex("'((?:\\\\.|[^'])*)'").findAll(b).map{str("'${it.groupValues[1]}'")}.toList()}
    fun hijriMonths(c:Context):HijriCalendarData?=runCatching{Json{ignoreUnknownKeys=true}.decodeFromString<HijriCalendarData>(read(c,"data/hijri_calendar.json"))}.getOrNull()
}
