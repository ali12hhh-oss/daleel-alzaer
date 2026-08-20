package com.daleelalzaer.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val Green = Color(0xFF0B4F4A)
private val Gold = Color(0xFFD6A84F)
private val Cream = Color(0xFFF7F3E8)
private val DeepTeal = Color(0xFF001D27)
private val PanelTeal = Color(0xFF052E38)
private val SoftText = Color(0xFFF8F0DC)

private data class Shrine(val name: String, val lat: Double, val lon: Double, val city: String)
private data class Prayer(val name: String, val time: String)
private data class Verse(val number: Int, val text: String)

private val shrines = listOf(
    Shrine("مرقد الإمام الحسين (ع)", 32.616, 44.032, "كربلاء"),
    Shrine("مرقد أبي الفضل العباس (ع)", 32.616, 44.034, "كربلاء"),
    Shrine("مرقد الإمام علي (ع)", 31.995, 44.314, "النجف"),
    Shrine("مرقد الإمامين الكاظم والجواد (ع)", 33.378, 44.340, "بغداد"),
    Shrine("مرقد الإمامين العسكريين (ع)", 34.195, 43.874, "سامراء"),
    Shrine("مرقد الإمام الرضا (ع)", 36.260, 59.616, "مشهد"),
    Shrine("قبر الرسول ﷺ", 24.467, 39.611, "المدينة المنورة"),
    Shrine("البقيع", 24.468, 39.611, "المدينة المنورة"),
    Shrine("مسجد الكوفة", 32.029, 44.401, "الكوفة"),
    Shrine("مسجد السهلة", 32.041, 44.373, "الكوفة")
)

private val surahs = listOf(
    "الفاتحة","البقرة","آل عمران","النساء","المائدة","الأنعام","الأعراف","الأنفال","التوبة","يونس","هود","يوسف","الرعد","إبراهيم","الحجر","النحل","الإسراء","الكهف","مريم","طه","الأنبياء","الحج","المؤمنون","النور","الفرقان","الشعراء","النمل","القصص","العنكبوت","الروم","لقمان","السجدة","الأحزاب","سبأ","فاطر","يس","الصافات","ص","الزمر","غافر","فصلت","الشورى","الزخرف","الدخان","الجاثية","الأحقاف","محمد","الفتح","الحجرات","ق","الذاريات","الطور","النجم","القمر","الرحمن","الواقعة","الحديد","المجادلة","الحشر","الممتحنة","الصف","الجمعة","المنافقون","التغابن","الطلاق","التحريم","الملك","القلم","الحاقة","المعارج","نوح","الجن","المزمل","المدثر","القيامة","الإنسان","المرسلات","النبأ","النازعات","عبس","التكوير","الانفطار","المطففين","الانشقاق","البروج","الطارق","الأعلى","الغاشية","الفجر","البلد","الشمس","الليل","الضحى","الشرح","التين","العلق","القدر","البينة","الزلزلة","العاديات","القارعة","التكاثر","العصر","الهمزة","الفيل","قريش","الماعون","الكوثر","الكافرون","النصر","المسد","الإخلاص","الفلق","الناس"
)

enum class Screen(val title: String) { HOME("دليل الزائر"), PRAYER("مواقيت الصلاة"), QURAN("القرآن الكريم"), TOOLS("الأدوات"), SETTINGS("الإعدادات"), QIBLA("القبلة"), SHRINES("المراقد والمواقع"), TASBIH("المسبحة"), QADA("قضاء الصلاة"), ROUTE("الطرق والملاحة"), DUA("الأدعية والزيارات") }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DaleelApp() }
    }
}

@Composable
private fun DaleelApp() {
    val context = LocalContext.current
    var dark by remember { mutableStateOf(true) }
    val colors = if (dark) {
        darkColorScheme(
            primary = Color(0xFFF0C766),
            onPrimary = Color(0xFF17343A),
            secondary = Gold,
            background = DeepTeal,
            surface = PanelTeal,
            surfaceVariant = Color(0xFF0A3D46),
            onBackground = SoftText,
            onSurface = SoftText
        )
    } else {
        lightColorScheme(primary = Green, secondary = Gold, background = Cream)
    }
    MaterialTheme(colorScheme = colors) {
        androidx.compose.runtime.CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            var screen by remember { mutableStateOf(Screen.HOME) }
            Scaffold(
                containerColor = if (dark) DeepTeal else Cream,
                topBar = {
                    if (screen != Screen.HOME) AppBar(screen) { screen = Screen.HOME }
                },
                bottomBar = {
                    if (screen in listOf(Screen.HOME, Screen.PRAYER, Screen.QURAN, Screen.TOOLS, Screen.SETTINGS)) {
                        NavigationBar(containerColor = if (dark) PanelTeal else MaterialTheme.colorScheme.surface) {
                            NavigationBarItem(screen == Screen.HOME, { screen = Screen.HOME }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("الرئيسية") })
                            NavigationBarItem(screen == Screen.PRAYER, { screen = Screen.PRAYER }, icon = { Icon(Icons.Default.AccessTime, null) }, label = { Text("الصلاة") })
                            NavigationBarItem(screen == Screen.QURAN, { screen = Screen.QURAN }, icon = { Icon(Icons.Default.MenuBook, null) }, label = { Text("القرآن") })
                            NavigationBarItem(screen == Screen.TOOLS, { screen = Screen.TOOLS }, icon = { Icon(Icons.Default.Tune, null) }, label = { Text("الأدوات") })
                            NavigationBarItem(screen == Screen.SETTINGS, { screen = Screen.SETTINGS }, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("الإعدادات") })
                        }
                    }
                }
            ) { pad ->
                Surface(Modifier.fillMaxSize().padding(pad), color = if (dark) DeepTeal else MaterialTheme.colorScheme.background) {
                    when (screen) {
                        Screen.HOME -> HomeScreenNew { screen = it }
                        Screen.PRAYER -> PrayerScreen()
                        Screen.QURAN -> QuranScreen()
                        Screen.TOOLS -> ToolsScreen { screen = it }
                        Screen.SETTINGS -> SettingsScreen(dark) { dark = it }
                        Screen.QIBLA -> QiblaScreen()
                        Screen.SHRINES -> ShrinesScreen()
                        Screen.TASBIH -> TasbihScreen()
                        Screen.QADA -> QadaScreen()
                        Screen.ROUTE -> RouteScreen()
                        Screen.DUA -> DuaScreen()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun AppBar(screen: Screen, onHome: () -> Unit) {
    TopAppBar(
        title = { Text(screen.title, fontWeight = FontWeight.Bold) },
        navigationIcon = { if (screen !in listOf(Screen.HOME, Screen.PRAYER, Screen.QURAN, Screen.TOOLS, Screen.SETTINGS)) IconButton(onClick = onHome) { Icon(Icons.Default.ArrowBack, "رجوع") } }
    )
}

@Composable private fun HomeScreen(open: (Screen) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Green), shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(22.dp)) {
                    Text("السلام عليكم", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("دليل الزائر — رفيقك في السفر والزيارة والعبادة", color = Color.White.copy(alpha = .9f))
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = { open(Screen.PRAYER) }, label = { Text("مواقيت الصلاة") }, leadingIcon = { Icon(Icons.Default.AccessTime, null) })
                        AssistChip(onClick = { open(Screen.QIBLA) }, label = { Text("القبلة") }, leadingIcon = { Icon(Icons.Default.CompassCalibration, null) })
                    }
                }
            }
        }
        item { SectionTitle("الوصول السريع") }
        items(listOf(
            Triple("🗺️", "المراقد والمواقع", Screen.SHRINES), Triple("🧭", "القبلة والبوصلة", Screen.QIBLA),
            Triple("🛣️", "الطرق والملاحة", Screen.ROUTE), Triple("📿", "المسبحة", Screen.TASBIH),
            Triple("🕋", "قضاء الصلاة", Screen.QADA), Triple("📜", "الأدعية والزيارات", Screen.DUA)
        )) { (icon, title, target) -> FeatureCard(icon, title) { open(target) } }
        item {
            Card(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("فكرة التطبيق", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("تطبيق متكامل للزائر يجمع العبادة، الملاحة، المراقد، المحتوى الديني والأدوات اليومية في واجهة واحدة، مع تصميم واضح يعمل من اليمين إلى اليسار.")
                }
            }
        }
    }
}

@Composable private fun SectionTitle(text: String) { Text(text, fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.padding(vertical = 4.dp)) }
@Composable private fun FeatureCard(icon: String, title: String, click: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable(onClick = click), shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 27.sp); Spacer(Modifier.width(14.dp)); Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable private fun PrayerScreen() {
    val prayers = remember { approximatePrayerTimes() }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), shape = RoundedCornerShape(20.dp)) { Column(Modifier.padding(18.dp)) { Text("مواقيت اليوم", fontWeight = FontWeight.Bold, fontSize = 20.sp); Text("الحساب المحلي — يمكن تطوير طريقة الحساب من الإعدادات") } } }
        items(prayers) { p -> Card(Modifier.fillMaxWidth()) { Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(p.name, fontWeight = FontWeight.Bold); Text(p.time, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp) } } }
        item { Text("ملاحظة: مواقيت الصلاة النهائية يجب أن تعتمد على موقع المستخدم وطريقة الحساب التي يحددها في الإعدادات.", fontSize = 13.sp) }
    }
}

private fun approximatePrayerTimes(): List<Prayer> = listOf(Prayer("الفجر", "04:20"), Prayer("الشروق", "05:45"), Prayer("الظهر", "12:10"), Prayer("العصر", "15:40"), Prayer("المغرب", "18:35"), Prayer("العشاء", "19:55"))

@Composable private fun QuranScreen() {
    var selected by remember { mutableIntStateOf(0) }
    var verses by remember { mutableStateOf<List<Verse>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(selected) {
        loading = true
        verses = loadSurah(selected + 1)
        loading = false
    }
    LazyColumn(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("اختر السورة", fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.padding(8.dp)) }
        items(surahs.indices.toList()) { i ->
            Card(Modifier.fillMaxWidth().clickable { selected = i }, colors = CardDefaults.cardColors(containerColor = if (i == selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text("${i + 1}", fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp)); Text(surahs[i], fontSize = 17.sp) }
            }
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("سورة ${surahs[selected]}", fontWeight = FontWeight.Bold, fontSize = 21.sp)
                    Spacer(Modifier.height(8.dp))
                    when { loading -> Text("جارٍ تحميل النص…"); verses.isEmpty() -> Text("تعذر تحميل السورة. تحقق من اتصال الإنترنت."); else -> verses.forEach { v -> Text("${v.text}  ﴿${v.number}﴾", fontSize = 21.sp, lineHeight = 39.sp, modifier = Modifier.padding(vertical = 5.dp)) } }
                }
            }
        }
    }
}

private suspend fun loadSurah(number: Int): List<Verse> = withContext(Dispatchers.IO) {
    try {
        val connection = (URL("https://api.alquran.cloud/v1/surah/$number/quran-uthmani").openConnection() as HttpURLConnection).apply { connectTimeout = 8000; readTimeout = 10000 }
        val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        val data = json.getJSONObject("data").getJSONArray("ayahs")
        buildList { for (i in 0 until data.length()) { val a = data.getJSONObject(i); add(Verse(a.getInt("numberInSurah"), a.getString("text"))) } }
    } catch (_: Exception) { emptyList() }
}

@Composable private fun ToolsScreen(open: (Screen) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionTitle("الأدوات والخدمات") }
        items(listOf(
            Triple("🧭", "القبلة والبوصلة", Screen.QIBLA), Triple("🕌", "المراقد والمواقع", Screen.SHRINES), Triple("🛣️", "الطرق والملاحة", Screen.ROUTE),
            Triple("📿", "المسبحة الإلكترونية", Screen.TASBIH), Triple("🕋", "قضاء الصلاة", Screen.QADA), Triple("📜", "الأدعية والزيارات", Screen.DUA)
        )) { (i, t, s) -> FeatureCard(i, t) { open(s) } }
        item { FeatureCard("📅", "التقويم الهجري والمناسبات") {} }
        item { FeatureCard("❓", "الاستفتاءات والأسئلة الشرعية") {} }
        item { FeatureCard("📚", "الكتب والسيرة") {} }
    }
}

@Composable private fun QiblaScreen() {
    val context = LocalContext.current
    var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var permissionDenied by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result[Manifest.permission.ACCESS_FINE_LOCATION] == true || result[Manifest.permission.ACCESS_COARSE_LOCATION] == true) fetchLocation(context) { location = it } else permissionDenied = true
    }
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("اتجاه القبلة", fontWeight = FontWeight.Bold, fontSize = 25.sp)
        Text("من موقع المستخدم إلى الكعبة المشرفة")
        Card(shape = RoundedCornerShape(30.dp), modifier = Modifier.size(250.dp)) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.CompassCalibration, null, Modifier.size(150.dp), tint = MaterialTheme.colorScheme.primary) } }
        if (location == null) Button(onClick = { launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) }) { Icon(Icons.Default.LocationOn, null); Spacer(Modifier.width(8.dp)); Text("تحديد موقعي") }
        location?.let { (lat, lon) ->
            val bearing = bearing(lat, lon, 21.4225, 39.8262)
            Text("زاوية القبلة: ${"%.1f".format(bearing)}°", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("الموقع: ${"%.4f".format(lat)}, ${"%.4f".format(lon)}")
        }
        if (permissionDenied) Text("يجب السماح بالموقع لاستخدام القبلة.")
    }
}

private fun fetchLocation(context: Context, result: (Pair<Double, Double>) -> Unit) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return
    LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener { l -> if (l != null) result(l.latitude to l.longitude) }
}

private fun bearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val p1 = Math.toRadians(lat1); val p2 = Math.toRadians(lat2); val dl = Math.toRadians(lon2 - lon1)
    return (Math.toDegrees(atan2(sin(dl) * cos(p2), cos(p1) * sin(p2) - sin(p1) * cos(p2) * cos(dl))) + 360) % 360
}

@Composable private fun ShrinesScreen() {
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("المراقد والمواقع الدينية", fontWeight = FontWeight.Bold, fontSize = 22.sp) }
        items(shrines) { shrine -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text(shrine.name, fontWeight = FontWeight.Bold, fontSize = 18.sp); Text(shrine.city); Spacer(Modifier.height(8.dp)); Button(onClick = { openGoogleMaps(context, shrine.lat, shrine.lon) }) { Icon(Icons.Default.Directions, null); Spacer(Modifier.width(6.dp)); Text("فتح الطريق في خرائط Google") } } } }
    }
}

private fun openGoogleMaps(context: Context, lat: Double, lon: Double) {
    val nav = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$lat,$lon")); nav.setPackage("com.google.android.apps.maps")
    if (nav.resolveActivity(context.packageManager) != null) context.startActivity(nav) else context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lon")))
}

@Composable private fun RouteScreen() {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("الطرق والملاحة", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("اختر مرقداً لفتح الطريق إليه عبر خرائط Google. لاحقاً يمكن إضافة حساب المسار داخل التطبيق وحفظه للاستخدام دون اتصال.")
        shrines.take(6).forEach { s -> FilledTonalButton(onClick = { openGoogleMaps(context, s.lat, s.lon) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Place, null); Spacer(Modifier.width(8.dp)); Text(s.name) } }
    }
}

@Composable private fun TasbihScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("tasbih", Context.MODE_PRIVATE) }
    var count by remember { mutableIntStateOf(prefs.getInt("count", 0)) }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("المسبحة الإلكترونية", fontWeight = FontWeight.Bold, fontSize = 25.sp)
        Spacer(Modifier.height(30.dp)); Text(count.toString(), fontSize = 72.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(25.dp)); Button(onClick = { count++; prefs.edit().putInt("count", count).apply() }, modifier = Modifier.size(180.dp)) { Text("تسبيح", fontSize = 22.sp) }
        Spacer(Modifier.height(12.dp)); TextButton(onClick = { count = 0; prefs.edit().putInt("count", 0).apply() }) { Icon(Icons.Default.Refresh, null); Spacer(Modifier.width(5.dp)); Text("تصفير") }
    }
}

@Composable private fun QadaScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("qada", Context.MODE_PRIVATE) }
    var fajr by remember { mutableIntStateOf(prefs.getInt("fajr", 0)) }
    var dhuhr by remember { mutableIntStateOf(prefs.getInt("dhuhr", 0)) }
    var asr by remember { mutableIntStateOf(prefs.getInt("asr", 0)) }
    var maghrib by remember { mutableIntStateOf(prefs.getInt("maghrib", 0)) }
    var isha by remember { mutableIntStateOf(prefs.getInt("isha", 0)) }
    val rows = listOf("الفجر" to fajr, "الظهر" to dhuhr, "العصر" to asr, "المغرب" to maghrib, "العشاء" to isha)
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("قضاء الصلاة", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("سجّل ما تم قضاؤه، وتبقى الأرقام محفوظة على الجهاز.")
        Spacer(Modifier.height(12.dp))
        rows.forEach { (name, value) ->
            Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Text(name, fontWeight = FontWeight.Bold); Row(verticalAlignment = Alignment.CenterVertically) { Text(value.toString(), fontSize = 20.sp); IconButton(onClick = { val n = (value + 1); when(name){"الفجر" -> {fajr=n; prefs.edit().putInt("fajr",n).apply()};"الظهر"->{dhuhr=n;prefs.edit().putInt("dhuhr",n).apply()};"العصر"->{asr=n;prefs.edit().putInt("asr",n).apply()};"المغرب"->{maghrib=n;prefs.edit().putInt("maghrib",n).apply()};"العشاء"->{isha=n;prefs.edit().putInt("isha",n).apply()}} }) { Text("+") } } } }
        }
        Spacer(Modifier.height(12.dp)); Text("المجموع: ${fajr + dhuhr + asr + maghrib + isha}", fontWeight = FontWeight.Bold, fontSize = 19.sp)
    }
}

@Composable private fun DuaScreen() {
    val items = listOf("دعاء كميل", "دعاء التوسل", "دعاء الندبة", "زيارة عاشوراء", "زيارة الجامعة الكبيرة", "دعاء العهد", "الصحيفة السجادية")
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("الأدعية والزيارات", fontWeight = FontWeight.Bold, fontSize = 23.sp) }
        item { Text("قسم المحتوى الروحي. كل عنصر يفتح مساحة القراءة الخاصة به في مراحل المحتوى القادمة.") }
        items(items) { title -> Card(Modifier.fillMaxWidth().clickable { }) { Text(title, Modifier.padding(18.dp), fontSize = 18.sp, fontWeight = FontWeight.SemiBold) } }
    }
}

@Composable private fun SettingsScreen(dark: Boolean, setDark: (Boolean) -> Unit) {
    var fontScale by remember { mutableFloatStateOf(1f) }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("الإعدادات", fontWeight = FontWeight.Bold, fontSize = 24.sp) }
        item { Card { Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(if (dark) Icons.Default.NightsStay else Icons.Default.Brightness6, null); Spacer(Modifier.width(10.dp)); Text("الوضع الليلي") }; androidx.compose.material3.Switch(checked = dark, onCheckedChange = setDark) } } }
        item { Card { Column(Modifier.padding(18.dp)) { Text("حجم الخط", fontWeight = FontWeight.Bold); Slider(value = fontScale, onValueChange = { fontScale = it }, valueRange = .8f..1.3f); Text("${(fontScale * 100).toInt()}%") } } }
        item { Card { Column(Modifier.padding(18.dp)) { Text("دليل الزائر", fontWeight = FontWeight.Bold); Text("نسخة أولية معمارية جديدة — الوظائف تضاف كوحدات مستقلة دون الاعتماد على مشروع قديم.") } } }
    }
}
