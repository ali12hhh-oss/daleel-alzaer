package com.daleelalzaer.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HomeBg = Color(0xFF001A20)
private val HomePanel = Color(0xFF06353B)
private val HomePanel2 = Color(0xFF083F42)
private val HomeGold = Color(0xFFD5A23A)
private val HomeGoldBright = Color(0xFFFFD36A)
private val HomeText = Color(0xFFFFF7E5)
private val HomeMuted = Color(0xFFE4D7B9)

private data class HomeAction(val title: String, val assetKey: String, val target: Screen)

private fun arabicDigits(value: String): String = value.map { ch ->
    when (ch) {
        '0' -> '٠'; '1' -> '١'; '2' -> '٢'; '3' -> '٣'; '4' -> '٤'
        '5' -> '٥'; '6' -> '٦'; '7' -> '٧'; '8' -> '٨'; '9' -> '٩'
        else -> ch
    }
}.joinToString("")

private fun arabicTime(hour24: Int, minute: Int): String {
    val suffix = if (hour24 < 12) "ص" else "م"
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    return "${arabicDigits("%d:%02d".format(Locale.US, hour12, minute))} $suffix"
}

private fun loadHomeBitmap(context: Context): Bitmap? = try {
    val raw = context.resources.openRawResource(R.raw.home_assets).bufferedReader().use { it.readText() }
    val encoded = JSONObject(raw).optString("full")
    if (encoded.isBlank()) null else {
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
} catch (_: Throwable) {
    null
}

private fun cropAsset(source: Bitmap, key: String): Bitmap? {
    val rect = when (key) {
        "route" -> floatArrayOf(.019f,.468f,.324f,.553f)
        "fiqh" -> floatArrayOf(.333f,.468f,.638f,.553f)
        "quran" -> floatArrayOf(.646f,.468f,.980f,.553f)
        "qibla" -> floatArrayOf(.019f,.561f,.324f,.653f)
        "qada" -> floatArrayOf(.333f,.561f,.638f,.653f)
        "prayer" -> floatArrayOf(.646f,.561f,.980f,.653f)
        "lunar" -> floatArrayOf(.019f,.658f,.324f,.751f)
        "rakats" -> floatArrayOf(.333f,.658f,.638f,.751f)
        "shrines" -> floatArrayOf(.646f,.658f,.980f,.751f)
        "tasbih" -> floatArrayOf(.019f,.755f,.324f,.848f)
        "library" -> floatArrayOf(.333f,.755f,.638f,.848f)
        "duas" -> floatArrayOf(.646f,.755f,.980f,.848f)
        "battle" -> floatArrayOf(.019f,.852f,.324f,.944f)
        "sayings" -> floatArrayOf(.333f,.852f,.638f,.944f)
        "birth_death" -> floatArrayOf(.646f,.852f,.980f,.944f)
        "mawadda" -> floatArrayOf(.154f,.944f,.500f,1f)
        "sermons" -> floatArrayOf(.505f,.944f,.850f,1f)
        else -> return null
    }
    val left = (source.width * rect[0]).toInt().coerceIn(0, source.width - 1)
    val top = (source.height * rect[1]).toInt().coerceIn(0, source.height - 1)
    val right = (source.width * rect[2]).toInt().coerceIn(left + 1, source.width)
    val bottom = (source.height * rect[3]).toInt().coerceIn(top + 1, source.height)
    return Bitmap.createBitmap(source, left, top, right - left, bottom - top)
}

@Composable
fun HomeScreenNew(open: (Screen) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val source = remember { loadHomeBitmap(context) }
    val date = remember {
        arabicDigits(SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(Date()))
    }
    val actions = remember {
        listOf(
            HomeAction("مسار الزائر", "route", Screen.ROUTE),
            HomeAction("مسائل شرعية", "fiqh", Screen.TOOLS),
            HomeAction("القرآن الكريم", "quran", Screen.QURAN),
            HomeAction("مواقيت الصلاة", "prayer", Screen.PRAYER),
            HomeAction("قضاء الصلاة", "qada", Screen.QADA),
            HomeAction("اتجاه القبلة", "qibla", Screen.QIBLA),
            HomeAction("اتجاه مراقد المعصومين", "shrines", Screen.SHRINES),
            HomeAction("عداد الركعات", "rakats", Screen.TOOLS),
            HomeAction("مواقيت الأهلة", "lunar", Screen.TOOLS),
            HomeAction("الأدعية والزيارات", "duas", Screen.DUA),
            HomeAction("المكتبة", "library", Screen.TOOLS),
            HomeAction("المسبحة الإلكترونية", "tasbih", Screen.TASBIH),
            HomeAction("ولادات ووفيات أهل البيت", "birth_death", Screen.TOOLS),
            HomeAction("أقوال أهل البيت", "sayings", Screen.TOOLS),
            HomeAction("أحداث معركة الطف", "battle", Screen.TOOLS),
            HomeAction("خطب أهل البيت", "sermons", Screen.TOOLS),
            HomeAction("مودة أهل البيت", "mawadda", Screen.TOOLS)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(HomeBg).padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        item { HomeHeader(source) }
        item { HomePrayerPanel() }
        item { HomeDateAndCountdown(date) }
        actions.chunked(3).forEach { rowActions ->
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowActions.forEach { action -> HomeTile(source, action, open, Modifier.weight(1f)) }
                    repeat(3 - rowActions.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        item { Spacer(Modifier.height(14.dp)) }
    }
}

@Composable
private fun HomeHeader(source: Bitmap?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .9f))
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clip(RoundedCornerShape(24.dp)).background(HomePanel2).padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = HomeGoldBright, modifier = Modifier.size(21.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("كربلاء المقدسة", color = HomeText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(3.dp))
                    Icon(Icons.Default.KeyboardArrowDown, null, tint = HomeGoldBright, modifier = Modifier.size(19.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) { Icon(Icons.Default.Brightness6, "الوضع", tint = HomeGoldBright) }
                    IconButton(onClick = {}) { Icon(Icons.Default.NightsStay, "الليل", tint = HomeGoldBright) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Settings, "الإعدادات", tint = HomeGoldBright) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Notifications, "التنبيهات", tint = HomeGoldBright) }
                }
            }
            Box(Modifier.fillMaxWidth().height(176.dp), contentAlignment = Alignment.Center) {
                if (source != null) {
                    Image(
                        bitmap = source.asImageBitmap(),
                        contentDescription = "مرقد الإمام الحسين عليه السلام",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(HomePanel2))
                }
                Box(
                    Modifier.align(Alignment.BottomStart).padding(start = 22.dp, bottom = 12.dp)
                        .background(Color.Black.copy(alpha = .34f), RoundedCornerShape(12.dp)).padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text("دليل الزائر", color = HomeGoldBright, fontSize = 29.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun HomePrayerPanel() {
    val prayers = listOf(
        "الفجر" to arabicTime(4, 52),
        "الشروق" to arabicTime(6, 12),
        "الظهر" to arabicTime(12, 34),
        "الغروب" to arabicTime(17, 46),
        "المغرب" to arabicTime(18, 48),
        "منتصف الليل" to arabicTime(23, 57)
    )
    Card(
        Modifier.fillMaxWidth(), RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .85f))
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 10.dp)) {
            Text("أوقات الصلاة", Modifier.fillMaxWidth(), color = HomeGoldBright, fontSize = 21.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(9.dp))
            Row(Modifier.fillMaxWidth()) {
                prayers.forEach { (name, time) ->
                    Column(Modifier.weight(1f).padding(horizontal = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(name, color = HomeText, fontSize = if (name == "منتصف الليل") 9.sp else 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(5.dp))
                        Text(time, color = HomeGoldBright, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeDateAndCountdown(date: String) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = HomePanel), border = BorderStroke(1.dp, HomeGold.copy(alpha = .75f))) {
        Row(Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("الوقت المتبقي لصلاة الظهر", color = HomeText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(arabicDigits("2:22:15"), color = HomeGoldBright, fontSize = 25.sp, fontWeight = FontWeight.Black)
            }
            Box(Modifier.width(1.dp).height(46.dp).background(HomeGold.copy(alpha = .45f)))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("التاريخ", color = HomeGoldBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(date, color = HomeText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2)
            }
        }
    }
}

@Composable
private fun HomeTile(source: Bitmap?, action: HomeAction, open: (Screen) -> Unit, modifier: Modifier) {
    val tile = remember(source, action.assetKey) { source?.let { cropAsset(it, action.assetKey) } }
    Card(
        modifier = modifier.height(105.dp).clickable { open(action.target) },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .88f))
    ) {
        if (tile != null) {
            Image(bitmap = tile.asImageBitmap(), contentDescription = action.title, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(17.dp)), contentScale = ContentScale.FillBounds)
        } else {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(action.title, color = HomeText, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}
