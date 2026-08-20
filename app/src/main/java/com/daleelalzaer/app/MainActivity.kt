package com.daleelalzaer.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// ============================================================
// الألوان
// ============================================================

private val LightBg = Color(0xFFF6F3EA)
private val LightCard = Color(0xFFFFFFFF)

private val Emerald = Color(0xFF087A63)
private val Teal = Color(0xFF159A8A)
private val Gold = Color(0xFFC49A3A)

private val NightBg = Color(0xFF10231F)
private val NightCard = Color(0xFF19352F)
private val NightText = Color(0xFFF2F1E8)

// ============================================================
// نموذج البيانات
// ============================================================

@Serializable
data class TextEntry(
    val id: Int,
    val title: String,
    val content: String
)

// ============================================================
// MainActivity
// ============================================================

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DaleelAlzaerApp()
        }
    }
}

// ============================================================
// التطبيق الرئيسي
// ============================================================

@Composable
fun DaleelAlzaerApp() {

    var dark by rememberSaveable {
        mutableStateOf(false)
    }

    val nav = rememberNavController()

    val colors = if (dark) {

        darkColorScheme(
            primary = Color(0xFF5DD6C0),
            secondary = Color(0xFFE2C36A),
            background = NightBg,
            surface = NightCard,
            onBackground = NightText,
            onSurface = NightText
        )

    } else {

        lightColorScheme(
            primary = Emerald,
            secondary = Gold,
            background = LightBg,
            surface = LightCard,
            onBackground = Color(0xFF18322D),
            onSurface = Color(0xFF18322D)
        )
    }

    MaterialTheme(
        colorScheme = colors
    ) {

        CompositionLocalProvider(
            LocalLayoutDirection provides LayoutDirection.Rtl
        ) {

            NavHost(
                navController = nav,
                startDestination = "splash"
            ) {

                composable("splash") {
                    Splash {
                        nav.navigate("home") {
                            popUpTo("splash") {
                                inclusive = true
                            }
                        }
                    }
                }

                composable("home") {
                    Home(
                        nav = nav,
                        dark = dark,
                        toggle = {
                            dark = !dark
                        }
                    )
                }

                composable("shrines") {
                    Shrines(nav)
                }

                composable("worship") {
                    Worship(nav)
                }

                composable("map") {
                    RouteScreen(nav)
                }

                composable("qibla") {
                    QiblaScreen(nav)
                }

                composable("prayer") {
                    PrayerScreen(nav)
                }

                composable("quran") {
                    ContentListScreen(
                        title = "القرآن الكريم",
                        subtitle = "قراءة القرآن دون اتصال",
                        entries = emptyList(),
                        source = "quran",
                        nav = nav
                    )
                }

                composable("duas") {
                    MafatihScreen(nav)
                }

                composable("sahifa") {
                    SahifaScreen(nav)
                }

                composable("books") {
                    BooksScreen(nav)
                }

                composable("tasbih") {
                    TasbihScreen(nav)
                }

                composable("more") {
                    More(nav)
                }

                composable("settings") {
                    SettingsScreen(
                        nav = nav,
                        dark = dark,
                        setDark = {
                            dark = it
                        }
                    )
                }

                composable("detail/{source}/{id}") { entry ->

                    val source =
                        entry.arguments?.getString("source")
                            ?: "mafatih"

                    val id =
                        entry.arguments?.getString("id")
                            ?.toIntOrNull()
                            ?: 0

                    ContentDetailScreen(
                        source = source,
                        id = id,
                        nav = nav
                    )
                }
            }
        }
    }
}

// ============================================================
// شاشة البداية
// ============================================================

@Composable
private fun Splash(
    onDone: () -> Unit
) {

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1700)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Mosque,
                contentDescription = null,
                modifier = Modifier.size(78.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "دليل الزائر",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "رفيقك في الزيارة والعبادة",
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            CircularProgressIndicator(
                strokeWidth = 3.dp
            )
        }
    }
}

// ============================================================
// الرئيسية
// ============================================================

@Composable
private fun Home(
    nav: NavHostController,
    dark: Boolean,
    toggle: () -> Unit
) {

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(pad)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "دليل الزائر",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "رفيقك في الزيارة والعبادة",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                WaterIconButton(
                    icon = if (dark) {
                        Icons.Default.LightMode
                    } else {
                        Icons.Default.DarkMode
                    },
                    desc = "تبديل المظهر",
                    onClick = toggle
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                WaterIconButton(
                    icon = Icons.Default.Search,
                    desc = "بحث",
                    onClick = {}
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            TodayCard(nav)

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            SectionTitle("الوصول السريع")

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            QuickGrid(nav)

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            JourneyCard(nav)

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            SectionTitle("أعمال ومحتوى اليوم")

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            TodayContent(nav)

            Spacer(
                modifier = Modifier.height(22.dp)
            )
        }
    }
}

// ============================================================
// بطاقة اليوم
// ============================================================

@Composable
private fun TodayCard(
    nav: NavHostController
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(26.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "اليوم",
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Text(
                        text = "أوقات الصلاة",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "تتحدد بدقة بعد اختيار موقعك",
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                PrayerMini("الفجر", "—")
                PrayerMini("الظهر", "—")
                PrayerMini("العصر", "—")
                PrayerMini("المغرب", "—")
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            WaterButton(
                text = "عرض أوقات الصلاة",
                onClick = {
                    nav.navigate("prayer")
                },
                modifier = Modifier.fillMaxWidth(),
                container = Color.White.copy(alpha = 0.15f),
                content = Color.White
            )
        }
    }
}

@Composable
private fun PrayerMini(
    title: String,
    time: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 12.sp
        )

        Text(
            text = time,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================
// الوصول السريع
// ============================================================

@Composable
private fun QuickGrid(
    nav: NavHostController
) {

    val quickItems = listOf(
        Triple("المراقد", Icons.Default.Mosque, "shrines"),
        Triple("القبلة", Icons.Default.Explore, "qibla"),
        Triple("الطريق", Icons.Default.Map, "map"),
        Triple("الصلاة", Icons.Default.AccessTime, "prayer"),
        Triple("الأدعية", Icons.Default.Favorite, "duas"),
        Triple("القرآن", Icons.Default.MenuBook, "quran")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        quickItems
            .chunked(2)
            .forEach { row ->

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    row.forEach { item ->

                        WaterCard(
                            title = item.first,
                            icon = item.second,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                nav.navigate(item.third)
                            }
                        )
                    }
                }
            }
    }
}

// ============================================================
// الرحلة
// ============================================================

@Composable
private fun JourneyCard(
    nav: NavHostController
) {

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "رحلتك القادمة",
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "الموقع الحالي  →  مرقد الإمام الحسين (ع)",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "حدد موقعك ووجهتك للحصول على المسار الحقيقي.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WaterButton(
                text = "ابدأ الطريق",
                onClick = {
                    nav.navigate("map")
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ============================================================
// محتوى اليوم
// ============================================================

@Composable
private fun TodayContent(
    nav: NavHostController
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        WaterCard(
            title = "دعاء اليوم",
            icon = Icons.Default.Favorite,
            modifier = Modifier.weight(1f),
            onClick = {
                nav.navigate("duas")
            }
        )

        WaterCard(
            title = "الصحيفة",
            icon = Icons.Default.MenuBook,
            modifier = Modifier.weight(1f),
            onClick = {
                nav.navigate("sahifa")
            }
        )
    }
}

// ============================================================
// المراقد
// ============================================================

@Composable
private fun Shrines(
    nav: NavHostController
) {

    val places = listOf(
        "مرقد الإمام الحسين (ع) — كربلاء",
        "مرقد أبي الفضل العباس (ع) — كربلاء",
        "مرقد الإمام علي (ع) — النجف",
        "مرقد الإمامين الكاظمين (ع) — بغداد",
        "مرقد الإمامين العسكريين (ع) — سامراء",
        "قبر النبي (ص) — المدينة المنورة",
        "البقيع — المدينة المنورة"
    )

    ListPage(
        title = "المراقد والأماكن",
        icon = Icons.Default.Mosque,
        nav = nav,
        items = places
    ) {
        nav.navigate("map")
    }
}

// ============================================================
// العبادة
// ============================================================

@Composable
private fun Worship(
    nav: NavHostController
) {

    val items = listOf(
        "القرآن الكريم" to "quran",
        "الأدعية والزيارات" to "duas",
        "الصحيفة السجادية" to "sahifa",
        "أوقات الصلاة" to "prayer",
        "القبلة" to "qibla",
        "التسبيح" to "tasbih"
    )

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            PageHeader(
                title = "العبادة",
                icon = Icons.Default.Favorite,
                nav = nav
            )

            items.forEach { item ->

                WaterCard(
                    title = item.first,
                    icon = Icons.Default.AutoStories,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        nav.navigate(item.second)
                    }
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }
}

// ============================================================
// مفاتيح الجنان
// ============================================================

@Composable
private fun MafatihScreen(
    nav: NavHostController
) {

    val entries =
        rememberAssetEntries("mafatih_aljanan.json")

    ContentListScreen(
        title = "مفاتيح الجنان",
        subtitle = "الأدعية والزيارات",
        entries = entries,
        source = "mafatih",
        nav = nav
    )
}

// ============================================================
// الصحيفة
// ============================================================

@Composable
private fun SahifaScreen(
    nav: NavHostController
) {

    val entries =
        rememberAssetEntries("sahifa_sajjadiya.json")

    ContentListScreen(
        title = "الصحيفة السجادية",
        subtitle = "أدعية الإمام زين العابدين (ع)",
        entries = entries,
        source = "sahifa",
        nav = nav
    )
}

// ============================================================
// قائمة المحتوى
// ============================================================

@Composable
private fun ContentListScreen(
    title: String,
    subtitle: String,
    entries: List<TextEntry>,
    source: String,
    nav: NavHostController
) {

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            PageHeader(
                title = title,
                icon = Icons.Default.MenuBook,
                nav = nav
            )

            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (entries.isEmpty()) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {

                    Text(
                        text = "سيتم ربط محتوى القرآن المحلي في خطوة البيانات التالية، دون فقدان ملفات المصدر.",
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }

            } else {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    items(
                        items = entries,
                        key = { entry ->
                            entry.id
                        }
                    ) { entry ->

                        WaterCard(
                            title = entry.title.ifBlank {
                                "قسم ${entry.id + 1}"
                            },
                            icon = Icons.Default.AutoStories,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                nav.navigate(
                                    "detail/$source/${entry.id}"
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// تفاصيل المحتوى
// ============================================================

@Composable
private fun ContentDetailScreen(
    source: String,
    id: Int,
    nav: NavHostController
) {

    val file =
        if (source == "sahifa") {
            "sahifa_sajjadiya.json"
        } else {
            "mafatih_aljanan.json"
        }

    val entries =
        rememberAssetEntries(file)

    val entry =
        entries.firstOrNull { item ->
            item.id == id
        }

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            PageHeader(
                title = entry?.title?.ifBlank {
                    "المحتوى"
                } ?: "المحتوى",
                icon = Icons.Default.AutoStories,
                nav = nav
            )

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(22.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {

                    Text(
                        text = entry?.content
                            ?: "لم يتم العثور على المحتوى.",
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 30.sp
                    )
                }
            }
        }
    }
}

// ============================================================
// الكتب
// ============================================================

@Composable
private fun BooksScreen(
    nav: NavHostController
) {

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            PageHeader(
                title = "الكتب",
                icon = Icons.Default.MenuBook,
                nav = nav
            )

            WaterCard(
                title = "آهلة — PDF",
                icon = Icons.Default.PictureAsPdf,
                modifier = Modifier.fillMaxWidth(),
                onClick = {}
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            WaterCard(
                title = "مفاتيح الجنان",
                icon = Icons.Default.AutoStories,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    nav.navigate("duas")
                }
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            WaterCard(
                title = "الصحيفة السجادية",
                icon = Icons.Default.AutoStories,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    nav.navigate("sahifa")
                }
            )
        }
    }
}

// ============================================================
// التسبيح
// ============================================================

@Composable
private fun TasbihScreen(
    nav: NavHostController
) {

    var count by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PageHeader(
                title = "التسبيح",
                icon = Icons.Default.Fingerprint,
                nav = nav
            )

            Spacer(
                modifier = Modifier.height(40.dp)
            )

            Text(
                text = count.toString(),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            WaterButton(
                text = "تسبيح",
                onClick = {
                    count++
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            WaterButton(
                text = "تصفير",
                onClick = {
                    count = 0
                },
                modifier = Modifier.fillMaxWidth(),
                container = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// ============================================================
// الإعدادات
// ============================================================

@Composable
private fun SettingsScreen(
    nav: NavHostController,
    dark: Boolean,
    setDark: (Boolean) -> Unit
) {

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            PageHeader(
                title = "الإعدادات",
                icon = Icons.Default.Settings,
                nav = nav
            )

            SettingRow(
                title = "الوضع الليلي",
                value = dark,
                onChange = setDark
            )

            SettingRow(
                title = "الإشعارات",
                value = true,
                onChange = {}
            )

            SettingRow(
                title = "الموقع",
                value = false,
                onChange = {}
            )

            Text(
                text = "الأذونات ستطلب عند الحاجة فقط للحفاظ على الخصوصية.",
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    value: Boolean,
    onChange: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(18.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = title,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = value,
                onCheckedChange = onChange
            )
        }
    }
}

// ============================================================
// المزيد
// ============================================================

@Composable
private fun More(
    nav: NavHostController
) {

    val items = listOf(
        "الكتب" to "books",
        "مفاتيح الجنان" to "duas",
        "الصحيفة السجادية" to "sahifa",
        "التسبيح" to "tasbih",
        "الإعدادات" to "settings"
    )

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            PageHeader(
                title = "المزيد",
                icon = Icons.Default.Menu,
                nav = nav
            )

            items.forEach { item ->

                WaterCard(
                    title = item.first,
                    icon = Icons.Default.MenuBook,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        nav.navigate(item.second)
                    }
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }
    }
}

// ============================================================
// صفحة القائمة
// ============================================================

@Composable
private fun ListPage(
    title: String,
    icon: ImageVector,
    nav: NavHostController,
    items: List<String>,
    click: (String) -> Unit
) {

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            PageHeader(
                title = title,
                icon = icon,
                nav = nav
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = items
                ) { item ->

                    WaterCard(
                        title = item,
                        icon = icon,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            click(item)
                        }
                    )
                }
            }
        }
    }
}

// ============================================================
// أوقات الصلاة
// ============================================================

@Composable
private fun PrayerScreen(nav: NavHostController) {

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            PageHeader(
                title = "أوقات الصلاة",
                icon = Icons.Default.AccessTime,
                nav = nav
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "تحديد الموقع مطلوب",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = "لن أعرض أوقاتًا وهمية. بعد منح إذن الموقع سيتم ربط حسابات الصلاة الأصلية من مشروعك بالنسخة Native."
                    )

                    Spacer(
                        modifier = Modifier.height(14.dp)
                    )

                    WaterButton(
                        text = "تحديد الموقع",
                        onClick = {
                            requestLocationHint(context)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// ============================================================
// القبلة
// ============================================================

@Composable
private fun QiblaScreen(
    nav: NavHostController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var bearing by rememberSaveable {
        mutableStateOf<Double?>(null)
    }

    var message by rememberSaveable {
        mutableStateOf(
            "اضغط لتحديد اتجاه القبلة من موقعك"
        )
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->

            val granted =
                result.values.any { isGranted ->
                    isGranted
                }

            if (granted) {

                scope.launch {

                    val loc =
                        LocationSupport.lastKnownLocation(context)

                    if (loc != null) {

                        bearing =
                            LocationSupport.initialBearing(
                                loc.latitude,
                                loc.longitude,
                                LocationSupport.MECCA_LAT,
                                LocationSupport.MECCA_LNG
                            )

                        message =
                            "الاتجاه إلى الكعبة من موقعك"

                    } else {

                        message =
                            "تعذر الحصول على موقع أخير؛ تحقق من تشغيل الموقع."
                    }
                }

            } else {

                message =
                    "يجب السماح بالموقع لحساب اتجاه القبلة."
            }
        }

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PageHeader(
                title = "القبلة",
                icon = Icons.Default.Explore,
                nav = nav
            )

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = bearing?.let {
                    "السمت ${String.format("%.1f", it)}°"
                } ?: message,
                textAlign = TextAlign.Center
            )

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            WaterButton(
                text = "تحديد القبلة",
                onClick = {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "هذه المرحلة تحسب الاتجاه الجغرافي إلى مكة. حساس دوران الهاتف سيضاف في مرحلة الحساسات والاختبار.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                fontSize = 12.sp
            )
        }
    }
}

// ============================================================
// الطريق
// ============================================================

@Composable
private fun RouteScreen(
    nav: NavHostController
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var distance by rememberSaveable {
        mutableStateOf<Double?>(null)
    }

    var status by rememberSaveable {
        mutableStateOf(
            "لم يتم تحديد موقعك بعد"
        )
    }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->

            val granted =
                result.values.any { isGranted ->
                    isGranted
                }

            if (granted) {

                scope.launch {

                    val loc =
                        LocationSupport.lastKnownLocation(context)

                    if (loc != null) {

                        distance =
                            LocationSupport.distanceKm(
                                loc.latitude,
                                loc.longitude,
                                LocationSupport.HUSSAIN_SHRINE_LAT,
                                LocationSupport.HUSSAIN_SHRINE_LNG
                            )

                        status =
                            "من موقعك الحالي إلى مرقد الإمام الحسين (ع)"

                    } else {

                        status =
                            "تعذر الحصول على الموقع الأخير."
                    }
                }

            } else {

                status =
                    "يجب السماح بالموقع لحساب المسافة."
            }
        }

    Scaffold(
        bottomBar = {
            BottomBar(nav)
        }
    ) { pad ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp)
        ) {

            PageHeader(
                title = "الخريطة والطريق",
                icon = Icons.Default.Map,
                nav = nav
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "وجهة الزيارة",
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "مرقد الإمام الحسين (ع) — كربلاء",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(
                        text = status
                    )

                    distance?.let { value ->

                        Text(
                            text = "المسافة التقريبية بخط مستقيم: ${
                                String.format("%.1f", value)
                            } كم",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            WaterButton(
                text = "تحديد موقعي وحساب المسافة",
                onClick = {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            WaterButton(
                text = "فتح الوجهة في خرائط Google",
                onClick = {
                    openGoogleMaps(
                        context = context,
                        lat = LocationSupport.HUSSAIN_SHRINE_LAT,
                        lng = LocationSupport.HUSSAIN_SHRINE_LNG
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                container = MaterialTheme.colorScheme.secondary
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "المسار الفعلي وخرائط Offline سيتم ربطهما بعد اختبار محرك الخرائط وتخزين البلاطات، ولن يتم عرض مسار وهمي.",
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                fontSize = 12.sp
            )
        }
    }
}

// ============================================================
// رأس الصفحات
// ============================================================

@Composable
private fun PageHeader(
    title: String,
    icon: ImageVector,
    nav: NavHostController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        WaterIconButton(
            icon = Icons.Default.ArrowForward,
            desc = "رجوع",
            onClick = {
                nav.popBackStack()
            }
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================
// عنوان القسم
// ============================================================

@Composable
private fun SectionTitle(
    text: String
) {

    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold
    )
}

// ============================================================
// بطاقة
// ============================================================

@Composable
private fun WaterCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {

    WaterSurface(
        onClick = onClick,
        modifier = modifier
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.10f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = title,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ============================================================
// زر أيقونة
// ============================================================

@Composable
private fun WaterIconButton(
    icon: ImageVector,
    desc: String,
    onClick: () -> Unit
) {

    WaterSurface(
        onClick = onClick,
        modifier = Modifier.size(46.dp)
    ) {

        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

// ============================================================
// زر
// ============================================================

@Composable
private fun WaterButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    container: Color? = null,
    content: Color? = null
) {

    WaterSurface(
        onClick = onClick,
        modifier = modifier.clip(
            RoundedCornerShape(18.dp)
        )
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = container
                        ?: MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(vertical = 13.dp),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = text,
                color = content ?: Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================================
// تأثير الضغط
// ============================================================

@Composable
private fun WaterSurface(
    onClick: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {

    var press by remember {
        mutableStateOf<Offset?>(null)
    }

    var wave by remember {
        mutableFloatStateOf(0f)
    }

    val scope =
        rememberCoroutineScope()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(
                MaterialTheme.colorScheme.surface
            )
            .pointerInput(Unit) {

                detectTapGestures { position ->

                    press = position
                    wave = 0f

                    scope.launch {

                        animate(
                            initialValue = 0f,
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = 520,
                                easing = FastOutSlowInEasing
                            )
                        ) { value, _ ->

                            wave = value
                        }
                    }

                    onClick()
                }
            }
    ) {

        content()

        press?.let { center ->

            Canvas(
                modifier = Modifier.matchParentSize()
            ) {

                val radius =
                    maxOf(
                        size.width,
                        size.height
                    ) * (
                        0.12f + wave * 1.15f
                    )

                drawCircle(
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.16f * (1f - wave)
                    ),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 3.dp.toPx()
                    )
                )

                drawCircle(
                    color = Color.White.copy(
                        alpha = 0.08f * (1f - wave)
                    ),
                    radius = radius * 0.68f,
                    center = center
                )
            }
        }
    }
}

// ============================================================
// الشريط السفلي
// ============================================================

@Composable
private fun BottomBar(
    nav: NavHostController
) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        val current =
            nav.currentBackStackEntryAsState()
                .value
                ?.destination
                ?.route

        NavItem(
            route = "home",
            label = "الرئيسية",
            icon = Icons.Default.Home,
            current = current,
            nav = nav
        )

        NavItem(
            route = "shrines",
            label = "المراقد",
            icon = Icons.Default.Mosque,
            current = current,
            nav = nav
        )

        NavItem(
            route = "worship",
            label = "العبادة",
            icon = Icons.Default.Favorite,
            current = current,
            nav = nav
        )

        NavItem(
            route = "map",
            label = "الخريطة",
            icon = Icons.Default.Map,
            current = current,
            nav = nav
        )

        NavItem(
            route = "more",
            label = "المزيد",
            icon = Icons.Default.Menu,
            current = current,
            nav = nav
        )
    }
}

// ============================================================
// عنصر الشريط السفلي
// ============================================================

@Composable
private fun NavItem(
    route: String,
    label: String,
    icon: ImageVector,
    current: String?,
    nav: NavHostController
) {

    NavigationBarItem(
        selected = current == route,
        onClick = {

            nav.navigate(route) {

                launchSingleTop = true
                restoreState = true
            }
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp
            )
        }
    )
}

// ============================================================
// قراءة ملفات JSON من assets
// ============================================================

@Composable
private fun rememberAssetEntries(
    name: String
): List<TextEntry> {

    val context = LocalContext.current

    return remember(name) {

        runCatching {

            val text =
                context.assets
                    .open(name)
                    .bufferedReader()
                    .use { reader ->
                        reader.readText()
                    }

            Json {
                ignoreUnknownKeys = true
            }.decodeFromString<List<TextEntry>>(text)

        }.getOrDefault(emptyList())
    }
}

// ============================================================
// فتح خرائط Google
// ============================================================

private fun openGoogleMaps(
    context: Context,
    lat: Double,
    lng: Double
) {

    val uri =
        Uri.parse(
            "geo:$lat,$lng?q=$lat,$lng"
        )

    val intent =
        Intent(
            Intent.ACTION_VIEW,
            uri
        )

    context.startActivity(intent)
}

// ============================================================
// فتح إعدادات الموقع
// ============================================================

private fun requestLocationHint(
    context: Context
) {

    val intent =
        Intent(
            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
        )

    context.startActivity(intent)
}
