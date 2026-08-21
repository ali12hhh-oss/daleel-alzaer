package com.daleelalzaer.app

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HomeBg = Color(0xFF001B23)
private val HomePanel = Color(0xFF052F38)
private val HomeGold = Color(0xFFD7A947)
private val HomeGoldBright = Color(0xFFFFD36A)
private val HomeText = Color(0xFFFFF8E8)
private val HomeMuted = Color(0xFFE5D8BA)

private data class HomeAction(
    val title: String,
    val assetKey: String,
    val target: Screen
)

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

private fun loadHomeBitmap(): Bitmap? {
    return try {
        val context = HomeAssets.context ?: return null
        val raw = context.resources.openRawResource(R.raw.home_assets).bufferedReader().use { it.readText() }
        val encoded = JSONObject(raw).getString("full")
        val bytes = Base64.decode(encoded, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Throwable) {
        null
    }
}

private object HomeAssets {
    var context: android.content.Context? = null
}

private fun cropAsset(source: Bitmap, key: String): Bitmap? {
    val x = source.width.toFloat()
    val y = source.height.toFloat()
    val rect = when (key) {
        "quran" -> floatArrayOf(.0191f,.4677f,.3241f,.5532f)
        "sharia" -> floatArrayOf(.3326f,.4677f,.6376f,.5532f)
        "route" -> floatArrayOf(.6461f,.4677f,.9798f,.5532f)
        "qibla" -> floatArrayOf(.0191f,.5610f,.3241f,.6531f)
        "qada" -> floatArrayOf(.3326f,.5610f,.6376f,.6531f)
        "prayer" -> floatArrayOf(.6461f,.5610f,.9798f,.6531f)
        "lunar" -> floatArrayOf(.0191f,.6579f,.3241f,.7506f)
        "rakats" -> floatArrayOf(.3326f,.6579f,.6376f,.7506f)
        "shrines" -> floatArrayOf(.6461f,.6579f,.9798f,.7506f)
        "tasbih" -> floatArrayOf(.0191f,.7548f,.3241f,.8481f)
        "library" -> floatArrayOf(.3326f,.7548f,.6376f,.8481f)
        "duas" -> floatArrayOf(.6461f,.7548f,.9798f,.8481f)
        "battle" -> floatArrayOf(.0191f,.8517f,.3241f,.9438f)
        "sayings" -> floatArrayOf(.3326f,.8517f,.6376f,.9438f)
        "birth_death" -> floatArrayOf(.6461f,.8517f,.9798f,.9438f)
        "mawadda" -> floatArrayOf(.1541f,.9438f,.4995f,1f)
        "sermons" -> floatArrayOf(.5048f,.9438f,.8502f,1f)
        else -> return null
    }
    val left = (rect[0] * x).toInt().coerceIn(0, source.width - 1)
    val top = (rect[1] * y).toInt().coerceIn(0, source.height - 1)
    val right = (rect[2] * x).toInt().coerceIn(left + 1, source.width)
    val bottom = (rect[3] * y).toInt().coerceIn(top + 1, source.height)
    return Bitmap.createBitmap(source, left, top, right - left, bottom - top)
}

@Composable
fun HomeScreenNew(open: (Screen) -> Unit) {
    val context = LocalContext.current
    HomeAssets.context = context
    val source = remember { loadHomeBitmap() }
    val date = remember {
        SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(Date()).map { ch ->
            when (ch) {
                '0' -> '٠'; '1' -> '١'; '2' -> '٢'; '3' -> '٣'; '4' -> '٤'
                '5' -> '٥'; '6' -> '٦'; '7' -> '٧'; '8' -> '٨'; '9' -> '٩'; else -> ch
            }
        }.joinToString("")
    }

    val actions = remember {
        listOf(
            HomeAction("مسار الزائر", "route", Screen.ROUTE),
            HomeAction("مسائل شرعية", "sharia", Screen.TOOLS),
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
        modifier = Modifier.fillMaxSize().background(HomeBg).padding(horizontal = 7.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        item { HomeHero(source) }
        item { HomePrayerPanel() }
        item { HomeDateAndCountdown(date) }
        actions.chunked(3).forEach { rowActions ->
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    rowActions.forEach { action -> HomeTile(source, action, open, Modifier.weight(1f)) }
                    repeat(3 - rowActions.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun HomeHero(source: Bitmap?) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(165.dp).clip(RoundedCornerShape(22.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            if (source != null) {
                Image(
                    bitmap = source.asImageBitmap(),
                    contentDescription = "واجهة دليل الزائر ومرقد الإمام الحسين عليه السلام",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    alignment = Alignment.TopCenter
                )
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel)
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 10.dp)) {
            Text("أوقات الصلاة", Modifier.fillMaxWidth(), color = HomeGoldBright, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(9.dp))
            Row(Modifier.fillMaxWidth()) {
                prayers.forEach { (name, time) ->
                    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel)
    ) {
        Column(Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("الوقت المتبقي لصلاة الظهر", color = HomeText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(arabicDigits("2:22:15"), color = HomeGoldBright, fontSize = 27.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(4.dp))
            Text(date, color = HomeMuted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HomeTile(source: Bitmap?, action: HomeAction, open: (Screen) -> Unit, modifier: Modifier) {
    val tile = remember(source, action.assetKey) { source?.let { cropAsset(it, action.assetKey) } }
    Card(
        modifier = modifier.height(88.dp).clickable { open(action.target) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel)
    ) {
        if (tile != null) {
            Image(
                bitmap = tile.asImageBitmap(),
                contentDescription = action.title,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}
