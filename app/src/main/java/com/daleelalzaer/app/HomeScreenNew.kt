package com.daleelalzaer.app

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val HomeBg = Color(0xFF001D27)
private val HomePanel = Color(0xFF052E38)
private val HomePanel2 = Color(0xFF073A42)
private val HomeGold = Color(0xFFD6A84F)
private val HomeGoldBright = Color(0xFFF0C766)
private val HomeText = Color(0xFFF8F0DC)

private data class HomeAction(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val target: Screen
)

@Composable
fun HomeScreenNew(open: (Screen) -> Unit) {
    val date = remember { SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(Date()) }
    val actions = remember {
        listOf(
            HomeAction("أوقات الصلاة", Icons.Default.AccessTime, Screen.PRAYER),
            HomeAction("القرآن الكريم", Icons.Default.MenuBook, Screen.QURAN),
            HomeAction("مفاتيح الجنان", Icons.Default.AutoStories, Screen.DUA),
            HomeAction("الأدعية والزيارات", Icons.Default.Spa, Screen.DUA),
            HomeAction("المراقد", Icons.Default.Place, Screen.SHRINES),
            HomeAction("الخريطة والطرق", Icons.Default.Directions, Screen.ROUTE),
            HomeAction("التقويم الهجري", Icons.Default.QrCode2, Screen.TOOLS),
            HomeAction("المسبحة", Icons.Default.Tune, Screen.TASBIH),
            HomeAction("القبلة", Icons.Default.CompassCalibration, Screen.QIBLA),
            HomeAction("اتجاه المراقد", Icons.Default.LocationOn, Screen.SHRINES),
            HomeAction("قضاء الصلاة", Icons.Default.Home, Screen.QADA),
            HomeAction("المزيد", Icons.Default.MoreHoriz, Screen.TOOLS)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(HomeBg).padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        item {
            HomeHero(date = date, open = open)
        }
        item {
            HomePrayerPanel()
        }
        items(actions.chunked(4)) { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowActions.forEach { action ->
                    HomeTile(action = action, open = open, modifier = Modifier.weight(1f))
                }
                repeat(4 - rowActions.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        item { Spacer(Modifier.height(4.dp)) }
    }
}

@Composable
private fun HomeHero(date: String, open: (Screen) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold)
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { open(Screen.SETTINGS) }) {
                    Icon(Icons.Default.Settings, contentDescription = "الإعدادات", tint = HomeGoldBright)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.NightsStay, contentDescription = "الوضع الليلي", tint = HomeGoldBright)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Brightness6, contentDescription = "المظهر", tint = HomeGoldBright)
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(178.dp).padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.shrine_hero),
                    contentDescription = "مرقد مضاء",
                    modifier = Modifier.fillMaxSize()
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("دليل الزائر", color = HomeGoldBright, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                    Text("مرجعك الديني والثقافي", color = HomeText, fontSize = 17.sp)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HomePanel2),
                border = BorderStroke(1.dp, HomeGold)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp, horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = HomeGoldBright, modifier = Modifier.size(19.dp))
                    Spacer(Modifier.width(7.dp))
                    Text("أوقات الصلاة حسب التوقيت المحلي", color = HomeText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(date, color = HomeText.copy(alpha = .78f), fontSize = 13.sp, modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp))
        }
    }
}

@Composable
private fun HomePrayerPanel() {
    val prayers = listOf(
        "الفجر" to "04:52",
        "الشروق" to "06:12",
        "الظهر" to "12:34",
        "العصر" to "03:48",
        "المغرب" to "06:48",
        "العشاء" to "08:18"
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold)
    ) {
        Column(Modifier.fillMaxWidth().padding(8.dp)) {
            Row(Modifier.fillMaxWidth()) {
                prayers.forEach { (name, time) ->
                    Column(
                        modifier = Modifier.weight(1f).padding(vertical = 5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(name, color = HomeText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(3.dp))
                        Text(time, color = HomeGoldBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(3.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(HomeGold.copy(alpha = .45f)))
            Column(Modifier.fillMaxWidth().padding(vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("الوقت المتبقي للصلاة التالية", color = HomeText.copy(alpha = .82f), fontSize = 12.sp)
                Spacer(Modifier.height(2.dp))
                Text("3:37:42", color = HomeGoldBright, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HomeTile(action: HomeAction, open: (Screen) -> Unit, modifier: Modifier) {
    Card(
        modifier = modifier.height(108.dp).clickable { open(action.target) },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HomePanel),
        border = BorderStroke(1.dp, HomeGold.copy(alpha = .9f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(action.icon, contentDescription = action.title, tint = HomeGoldBright, modifier = Modifier.size(38.dp))
            Spacer(Modifier.height(6.dp))
            Text(action.title, color = HomeText, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 2)
        }
    }
}
