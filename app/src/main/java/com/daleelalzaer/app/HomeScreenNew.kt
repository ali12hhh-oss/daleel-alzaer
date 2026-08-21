package com.daleelalzaer.app

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
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HomeBg = Color(0xFF001D27)
private val HomePanel = Color(0xFF052E38)
private val HomeGold = Color(0xFFD6A84F)
private val HomeGoldBright = Color(0xFFF0C766)
private val HomeText = Color(0xFFF8F0DC)

private data class HomeAction(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
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

@Composable
fun HomeScreenNew(open: (Screen) -> Unit) {
    val date = remember {
        arabicDigits(SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(Date()))
    }

    val actions = remember {
        listOf(
            HomeAction("مسار الزائر", Icons.Default.Place, Screen.ROUTE),
            HomeAction("مسائل شرعية", Icons.Default.Balance, Screen.TOOLS),
            HomeAction("القرآن الكريم", Icons.Default.MenuBook, Screen.QURAN),
            HomeAction("مواقيت الصلاة", Icons.Default.AccessTime, Screen.PRAYER),
            HomeAction("قضاء الصلاة", Icons.Default.Home, Screen.QADA),
            HomeAction("اتجاه القبلة", Icons.Default.CompassCalibration, Screen.QIBLA),
            HomeAction("اتجاه مراقد المعصومين", Icons.Default.LocationOn, Screen.SHRINES),
            HomeAction("عداد الركعات", Icons.Default.AccessTime, Screen.TOOLS),
            HomeAction("مواقيت الأهلة", Icons.Default.NightsStay, Screen.TOOLS),
            HomeAction("الأدعية والزيارات", Icons.Default.Spa, Screen.DUA),
            HomeAction("المكتبة", Icons.Default.MenuBook, Screen.TOOLS),
            HomeAction("المسبحة الإلكترونية", Icons.Default.SelfImprovement, Screen.TASBIH),
            HomeAction("ولادات ووفيات أهل البيت", Icons.Default.Event, Screen.TOOLS),
            HomeAction("أقوال أهل البيت", Icons.Default.FormatQuote, Screen.TOOLS),
            HomeAction("أحداث معركة الطف", Icons.Default.Flag, Screen.TOOLS),
            HomeAction("خطب أهل البيت", Icons.Default.Campaign, Screen.TOOLS),
            HomeAction("مودة أهل البيت", Icons.Default.Favorite, Screen.TOOLS)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(HomeBg).padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { HomeHero() }
        item { HomePrayerPanel() }
        item { HomeDateAndCountdown(date) }
        actions.chunked(3).forEach { rowActions ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    rowActions.forEach { action ->
                        HomeTile(action, open, Modifier.weight(1f))
                    }
                    repeat(3 - rowActions.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        item { Spacer(Modifier.height(10.dp)) }
    }
}

@Composable
private fun HomeHero() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = HomeGoldBright, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(5.dp))
                    Text("كربلاء المقدسة", color = HomeText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { }) { Icon(Icons.Default.WbSunny, "الوضع النهاري", tint = HomeGoldBright) }
                    IconButton(onClick = { }) { Icon(Icons.Default.NightsStay, "الوضع الليلي", tint = HomeGoldBright) }
                    IconButton(onClick = { }) { Icon(Icons.Default.Settings, "الإعدادات", tint = HomeGoldBright) }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(226.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shrine_hero),
                    contentDescription = "صورة المرقد",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.fillMaxWidth().height(118.dp)
                        .background(Color.Black.copy(alpha = 0.38f))
                )
                Text(
                    text = "دليل الزائر",
                    color = HomeGoldBright,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 18.dp)
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
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold)
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 8.dp)) {
            Text(
                "أوقات الصلاة",
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                color = HomeGoldBright,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Row(Modifier.fillMaxWidth()) {
                prayers.forEach { (name, time) ->
                    Column(
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(name, color = HomeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(6.dp))
                        Text(time, color = HomeGoldBright, fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .9f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("الوقت المتبقي لصلاة الظهر", color = HomeText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(arabicDigits("2:22:15"), color = HomeGoldBright, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(HomeGold.copy(alpha = .35f)))
            Spacer(Modifier.height(8.dp))
            Text(date, color = HomeText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun HomeTile(action: HomeAction, open: (Screen) -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier.height(132.dp).clickable { open(action.target) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .92f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(action.icon, action.title, tint = HomeGoldBright, modifier = Modifier.size(46.dp))
            Spacer(Modifier.height(7.dp))
            Text(
                action.title,
                color = HomeText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}
