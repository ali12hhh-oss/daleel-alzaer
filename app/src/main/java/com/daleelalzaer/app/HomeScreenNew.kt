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
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HomeBg = Color(0xFF001A20)
private val HomePanel = Color(0xFF06353B)
private val HomePanel2 = Color(0xFF083F42)
private val HomeGold = Color(0xFFD5A23A)
private val HomeGoldBright = Color(0xFFFFD36A)
private val HomeText = Color(0xFFFFF7E5)

private data class HomeAction(val title: String, val index: Int, val target: Screen)

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

private fun loadB64Bitmap(context: Context, resourceId: Int): Bitmap? = try {
    val encoded = context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        .replace("\\s".toRegex(), "")
    val bytes = Base64.decode(encoded, Base64.DEFAULT)
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
} catch (_: Throwable) {
    null
}

private fun cropGridAsset(source: Bitmap, index: Int): Bitmap? {
    val columns = 3
    val rows = 6
    val col = index % columns
    val row = index / columns
    if (row >= rows) return null

    val cellW = source.width.toFloat() / columns
    val cellH = source.height.toFloat() / rows
    val insetX = cellW * 0.025f
    val topInset = cellH * 0.035f
    val imageBottom = cellH * 0.72f

    val left = (col * cellW + insetX).toInt().coerceIn(0, source.width - 2)
    val top = (row * cellH + topInset).toInt().coerceIn(0, source.height - 2)
    val right = ((col + 1) * cellW - insetX).toInt().coerceIn(left + 1, source.width)
    val bottom = (row * cellH + imageBottom).toInt().coerceIn(top + 1, source.height)
    return Bitmap.createBitmap(source, left, top, right - left, bottom - top)
}

@Composable
fun HomeScreenNew(open: (Screen) -> Unit) {
    val context = LocalContext.current
    val hero = remember { loadB64Bitmap(context, R.raw.home_hero_b64) }
    val grid = remember { loadB64Bitmap(context, R.raw.home_grid_b64) }
    val date = remember {
        arabicDigits(SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(Date()))
    }

    // ترتيب الواجهة الرئيسي كما تم اعتماده سابقاً، مع حذف:
    // 1) أحداث معركة الطف
    // 2) مودة أهل البيت
    // ولا يوجد زر باسم «أقوال أهل البيت»؛ الزر المعتمد هو «خطب أهل البيت» فقط.
    val actions = remember {
        listOf(
            HomeAction("مسار الزائر", 0, Screen.ROUTE),
            HomeAction("مسائل شرعية", 1, Screen.TOOLS),
            HomeAction("القرآن الكريم", 2, Screen.QURAN),
            HomeAction("مواقيت الصلاة", 3, Screen.PRAYER),
            HomeAction("قضاء الصلاة", 4, Screen.QADA),
            HomeAction("اتجاه القبلة", 5, Screen.QIBLA),
            HomeAction("اتجاه مراقد المعصومين", 6, Screen.SHRINES),
            HomeAction("عداد الركعات", 7, Screen.TOOLS),
            HomeAction("مواقيت الأهلة", 8, Screen.TOOLS),
            HomeAction("الأدعية والزيارات", 9, Screen.DUA),
            HomeAction("المكتبة", 10, Screen.TOOLS),
            HomeAction("المسبحة الإلكترونية", 11, Screen.TASBIH),
            HomeAction("ولادات ووفيات أهل البيت", 12, Screen.TOOLS),
            HomeAction("خطب أهل البيت", 13, Screen.TOOLS)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(HomeBg).padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        item { HomeHeader(hero) }
        item { HomePrayerPanel() }
        item { HomeDateAndCountdown(date) }

        actions.chunked(3).forEach { rowActions ->
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowActions.forEach { action ->
                        HomeTile(grid, action, open, Modifier.weight(1f))
                    }
                    repeat(3 - rowActions.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun HomeHeader(hero: Bitmap?) {
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
                    modifier = Modifier.clip(RoundedCornerShape(24.dp))
                        .background(HomePanel2)
                        .padding(horizontal = 12.dp, vertical = 7.dp),
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

            Box(
                Modifier.fillMaxWidth().height(170.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                if (hero != null) {
                    Image(
                        bitmap = hero.asImageBitmap(),
                        contentDescription = "صورة حقيقية لمرقد الإمام الحسين عليه السلام",
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize().background(HomePanel2))
                }
                Box(
                    Modifier.padding(start = 18.dp, bottom = 12.dp)
                        .background(Color.Black.copy(alpha = .38f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
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
            Text(
                "أوقات الصلاة",
                Modifier.fillMaxWidth(),
                color = HomeGoldBright,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(9.dp))
            Row(Modifier.fillMaxWidth()) {
                prayers.forEach { (name, time) ->
                    Column(
                        Modifier.weight(1f).padding(horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            name,
                            color = HomeText,
                            fontSize = if (name == "منتصف الليل") 9.sp else 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            time,
                            color = HomeGoldBright,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeDateAndCountdown(date: String) {
    Card(
        Modifier.fillMaxWidth(), RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .75f))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
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
private fun HomeTile(grid: Bitmap?, action: HomeAction, open: (Screen) -> Unit, modifier: Modifier) {
    val tile = remember(grid, action.index) { grid?.let { cropGridAsset(it, action.index) } }
    Card(
        modifier = modifier.height(132.dp).clickable { open(action.target) },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .88f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(86.dp),
                contentAlignment = Alignment.Center
            ) {
                if (tile != null) {
                    Image(
                        bitmap = tile.asImageBitmap(),
                        contentDescription = action.title,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(11.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            Text(
                text = action.title,
                color = HomeText,
                fontSize = when {
                    action.title.length > 22 -> 10.sp
                    action.title.length > 15 -> 11.sp
                    else -> 12.sp
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp)
            )
        }
    }
}
