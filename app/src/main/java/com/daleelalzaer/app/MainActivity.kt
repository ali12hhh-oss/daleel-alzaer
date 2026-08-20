package com.daleelalzaer.app
import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
private val LightBg = Color(0xFFF4EBDD)
private val LightSurface = Color(0xFFFFFCF5)
private val LightAlt = Color(0xFFE4F3ED)
private val Green = Color(0xFF087A63)
private val Gold = Color(0xFFC49A3A)
private val Coral = Color(0xFFD87558)
private val Ink = Color(0xFF173A33)
private val NightBg = Color(0xFF102A24)
private val NightSurface = Color(0xFF1A4037)
private val NightAlt = Color(0xFF24564A)
private val NightText = Color(0xFFF5F1E7)
private val Mint = Color(0xFF68D7C0)
private val NightGold = Color(0xFFE4C56D)
@Serializable data class TextEntry(val id: Int, val title: String, val content: String)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DaleelAlzaerApp() }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaleelAlzaerApp() {
    var dark by rememberSaveable { mutableStateOf(false) }
    val nav = rememberNavController()
    val scheme = if (dark) darkColorScheme(
        primary = Mint, onPrimary = NightBg, secondary = NightGold,
        tertiary = Coral, background = NightBg, surface = NightSurface,
        surfaceContainerHighest = NightAlt, onBackground = NightText, onSurface = NightText,
        onSurfaceVariant = Color(0xFFD6E5DE)
    ) else lightColorScheme(
        primary = Green, onPrimary = Color.White, secondary = Gold,
        tertiary = Coral, background = LightBg, surface = LightSurface,
        surfaceContainerHighest = LightAlt, onBackground = Ink, onSurface = Ink,
        onSurfaceVariant = Color(0xFF47655D)
    )
    MaterialTheme(colorScheme = scheme) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            NavHost(navController = nav, startDestination = "splash") {
                composable("splash") { Splash { nav.navigate("home") { popUpTo("splash") { inclusive = true } } } }
                composable("home") { Home(nav, dark) { dark = !dark } }
                composable("route") { RouteScreen(nav) }
                composable("scholars") { ScholarsScreen(nav) }
                composable("prayer") { PrayerScreen(nav) }
                composable("rakah") { RakahCounterScreen(nav) }
                composable("qada") { QadaScreen(nav) }
                composable("qibla") { QiblaScreen(nav) }
                composable("compass") { CompassScreen(nav) }
                composable("crescent") { CrescentScreen(nav) }
                composable("duasHub") { DuasHub(nav) }
                composable("duas") { DuasList(nav) }
                composable("ziarat") { ZiaratList(nav) }
                composable("books") { Books(nav) }
                composable("quran") { QuranNotice(nav) }
                composable("mafatih") { BookList(nav, "مفاتيح الجنان", "mafatih_aljanan.json") }
                composable("sahifa") { BookList(nav, "الصحيفة السجادية", "sahifa_sajjadiya.json") }
                composable("tasbih") { Tasbih(nav) }
                composable("dates") { Dates(nav) }
                composable("hussein") { Quotes(nav, "أقوال الإمام الحسين عليه السلام", "hussein") }
                composable("battle") { Battle(nav) }
                composable("sabaya") { Quotes(nav, "خطب اهل البيت ", "sabaya") }
                composable("mawadda") { Quotes(nav, "مودة أهل البيت عليهم السلام", "mawadda") }
                composable("settings") { Settings(nav, dark) { dark = it } }
                composable("detail/{type}/{index}") { e ->
                    Detail(nav, e.arguments?.getString("type") ?: "", e.arguments?.getString("index")?.toIntOrNull() ?: 0)
                }
            }
        }
    }
}
@Composable
private fun Splash(done: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        runCatching {
            val a = context.assets.openFd("sounds/splash_sound.mp3")
            MediaPlayer().also { p ->
                p.setDataSource(a.fileDescriptor, a.startOffset, a.length)
                a.close()
 p.setOnCompletionListener { it.release() }
 p.prepare()
 p.start()
            }
        }
        kotlinx.coroutines.delay(3000)
        done()
    }
    Box(Modifier.fillMaxSize().background(Green), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val bitmap = remember { runCatching { android.graphics.BitmapFactory.decodeStream(context.assets.open("images/hussain_shrine.png")) }.getOrNull() }
            if (bitmap != null) Image(bitmap.asImageBitmap(), null, Modifier.size(205.dp).clip(RoundedCornerShape(28.dp)))
            Spacer(Modifier.height(24.dp))
            Text("دليل الزائر", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(7.dp))
            Text("رفيقك في الزيارة والعبادة", color = Color.White.copy(.82f), fontSize = 14.sp)
            Spacer(Modifier.height(30.dp))
            CircularProgressIndicator(color = Color.White.copy(.8f), strokeWidth = 3.dp)
        }
    }
}
@Composable
private fun Home(nav: NavHostController, dark: Boolean, toggle: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دليل الزائر", fontWeight = FontWeight.Bold) },
                navigationIcon = { ControlButton(Icons.Default.Settings, "الإعدادات") { nav.navigate("settings") } },
                actions = { ControlButton(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    if (dark) "الوضع النهاري" else "الوضع الليلي", toggle) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { p ->
        LazyColumn(
            Modifier.fillMaxSize().padding(p),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { HeroCard() }
            item { HomeCard(nav, "دليل مسار الزائر", "حدد موقعك واعرف أقرب المسارات", Icons.Default.DirectionsWalk,
                "route") }
            item { HomeCard(nav, "المسائل الشرعية", "اختر المرجع الديني واطّلع على الأجوبة الشرعية", Icons.Default.MenuBook,
                "scholars") }
            item { HomeCard(nav, "مواقيت الصلاة", "حسب كتيب مواقيت الصلاة للسيد السيستاني", Icons.Default.AccessTime,
                "prayer") }
            item { HomeCard(nav, "عداد الركعات", "عداد مستقل للركعات أثناء الصلاة", Icons.Default.FormatListNumbered,
                "rakah") }
            item { HomeCard(nav, "قضاء الصلاة", "متابعة الصلوات الفائتة وخطة القضاء وسجل الإنجاز", Icons.Default.CheckCircleOutline,
                "qada") }
            item { HomeCard(nav, "اتجاه القبلة", "حساب اتجاه القبلة حسب موقعك", Icons.Default.Explore, "qibla") }
            item { HomeCard(nav, "اتجاه مراقد المعصومين (ع)", "حدد موقع المراقد الشريفة والاماكن المقدسة حسب موقعك",
                Icons.Default.Explore, "compass") }
            item { HomeCard(nav, "مواقيت الأهلة", "حسب كراس الأهلة للسيد السيستاني", Icons.Default.NightlightRound,
                "crescent") }
            item { HomeCard(nav, "الأدعية والزيارات", "بعض الزيارات والادعية, لمراجعة كل الزيارات والادعية راجع قسم المكتبة",
                Icons.Default.Mosque, "duasHub") }
            item { HomeCard(nav, "المكتبة", "القرآن الكريم، مفاتيح الجنان، الصحيفة السجادية ", Icons.Default.LibraryBooks,
                "books") }
            item { HomeCard(nav, "المسبحة الإلكترونية", "تسبيح الزهراء عليها السلام والأذكار", Icons.Default.Fingerprint,
                "tasbih") }
            item { HomeCard(nav, "ولادات ووفيات أهل البيت", "تواريخ ولادة واستشهاد المعصومين عليهم السلام",
                Icons.Default.CalendarMonth, "dates") }
            item { HomeCard(nav, "أقوال الإمام الحسين عليه السلام", "خطبه وكلماته في كربلاء", Icons.Default.FormatQuote,
                "hussein") }
            item { HomeCard(nav, "احداث معركة الطف", "أحداث الأيام العشرة من محرم في كربلاء", Icons.Default.HistoryEdu,
                "battle") }
            item { HomeCard(nav, "خطب اهل البيت ", "خطب أهل البيت السبايا من كربلاء إلى الشام والمدينة",
                Icons.Default.RecordVoiceOver, "sabaya") }
            item { HomeCard(nav, "مودة أهل البيت عليهم السلام", "أحاديث النبي صلى الله عليه وآله في حب أهل البيت",
                Icons.Default.Favorite, "mawadda") }
        }
    }
}
@Composable private fun HeroCard() {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp), RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.primary,
        tonalElevation = 5.dp) {
        Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Mosque, null, Modifier.size(42.dp), tint = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("اللهم صل على محمد وال محمد ", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text("دليلك الشامل لزيارة المراقد والاماكن المقدسة", color = Color.White.copy(.9f), textAlign = TextAlign.Center,
                fontSize = 13.sp)
        }
    }
}
@Composable
private fun HomeCard(nav: NavHostController, title: String, subtitle: String, icon: ImageVector, route: String) {
    AnimatedSurface(Modifier.fillMaxWidth().padding(horizontal = 16.dp), { nav.navigate(route) }) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(54.dp).clip(RoundedCornerShape(17.dp)).background(MaterialTheme.colorScheme.primary.copy(.13f)),
                contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
            }
            Box(Modifier.size(42.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary.copy(.15f)),
                contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ArrowBackIosNew, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
            }
        }
    }
}
@Composable
private fun AnimatedSurface(modifier: Modifier, onClick: (() -> Unit)? = null, content: @Composable () -> Unit) {
    val source = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .975f else 1f, spring(stiffness = 700f), label = "press")
    Surface(
        modifier.graphicsLayer(scaleX = scale, scaleY = scale).clip(RoundedCornerShape(22.dp)).clickable(source,
            null) { onClick?.invoke() },
        shape = RoundedCornerShape(22.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp
    ) { content() }
}
@Composable
private fun ControlButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    val source = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) .88f else 1f, spring(stiffness = 700f), label = "control")
    Box(
        Modifier.padding(horizontal = 7.dp).size(50.dp).graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.12f)).clickable(source,
                null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(icon, description, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(23.dp)) }
}
@Composable
private fun Page(title: String, icon: ImageVector, nav: NavHostController, body: @Composable (PaddingValues) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Box(Modifier.padding(start = 10.dp).size(50.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.12f)).clickable { nav.popBackStack() },
                        contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ArrowForward, "رجوع", tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(23.dp))
                    }
                },
                title = { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp))
 Spacer(Modifier.width(9.dp))
 Text(title, fontWeight = FontWeight.Bold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        content = body
    )
}
@Composable private fun Info(title: String, subtitle: String, icon: ImageVector) {
    Card(Modifier.fillMaxWidth(), RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.14f)),
                contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
 Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 19.sp) }
        }
    }
}
@Composable private fun ActionButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary, contentColor: Color = Color.White) {
    AnimatedSurface(modifier, onClick) {
        Box(Modifier.fillMaxWidth().background(color, RoundedCornerShape(18.dp)).padding(vertical = 14.dp),
            contentAlignment = Alignment.Center) { Text(text, color = contentColor, fontWeight = FontWeight.Bold) }
    }
}
@Composable private fun RouteScreen(nav: NavHostController) {
    val c = LocalContext.current
    val places = remember { LegacySourceRepository.sacredPlaces(c) }
    var selected by remember { mutableStateOf(places.firstOrNull()) }
    var loc by remember { mutableStateOf<android.location.Location?>(null) }
    val launch = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) kotlinx.coroutines.MainScope().launch { loc = LocationSupport.lastKnownLocation(c) }
    }
    Page("دليل مسار الزائر", Icons.Default.DirectionsWalk, nav) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            item { Info("اختر المرقد أو المكان المقدس", "بعد الاختيار ستنتقل إلى صفحة الخرائط، ثم تختار خرائط كوكل.",
                Icons.Default.Map) }
            items(places) { x ->
                AnimatedSurface(Modifier.fillMaxWidth(), { selected = x }) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(46.dp).clip(CircleShape).background(if (x == selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(.1f)),
                        contentAlignment = Alignment.Center) { Icon(Icons.Default.Mosque, null, tint = if (x == selected) Color.White else MaterialTheme.colorScheme.primary) }
                        Spacer(Modifier.width(11.dp))
 Column(Modifier.weight(1f)) { Text(x.name, fontWeight = FontWeight.Bold)
 Text(x.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        if (x == selected) Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
            item {
                selected?.let { x ->
                    Card(Modifier.fillMaxWidth(), RoundedCornerShape(22.dp)) {
                        Column(Modifier.padding(17.dp)) {
                            Text(x.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
 Text(x.subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            loc?.let { Text("المسافة التقريبية بخط مستقيم: ${LocationSupport.distanceKm(it.latitude, it.longitude, x.lat, x.lng).f1()} كم",
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 7.dp)) }
                            Spacer(Modifier.height(11.dp))
 ActionButton("موقعي الحالي", { launch.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
     Modifier.fillMaxWidth())
                            Spacer(Modifier.height(8.dp))
 ActionButton("خرائط كوكل", { openMaps(c, x.lat, x.lng) }, Modifier.fillMaxWidth(), MaterialTheme.colorScheme.secondary)
                        }
                    }
                }
            }
        }
    }
}
@Composable private fun ScholarsScreen(nav: NavHostController) {
    val c = LocalContext.current
 val data = remember { LegacySourceRepository.scholars(c) }
 val cats = remember { LegacySourceRepository.questionCategories(c) }
    Page("المسائل الشرعية", Icons.Default.MenuBook, nav) { p ->
        LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            item { Info("اختر المرجع الديني", "اختر من قائمة المراجع الموجودة في ملفات المشروع الأصلية.",
                Icons.Default.MenuBook) }
            items(data) { x ->
                AnimatedSurface(Modifier.fillMaxWidth(), { if (x.officialSite.isNotBlank()) openUrl(c,
                    x.officialSite) }) { Column(Modifier.padding(16.dp)) { Text(x.name, fontSize = 17.sp, fontWeight = FontWeight.Bold)
 Text(x.title, color = MaterialTheme.colorScheme.secondary, fontSize = 13.sp)
 if (x.officialSite.isNotBlank()) Text("الموقع الرسمي", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary,
     modifier = Modifier.padding(top = 5.dp)) } }
            }
            item { Text("تصنيفات الأسئلة", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 7.dp)) }
            items(cats) { Text(it, Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceContainerHighest,
                RoundedCornerShape(14.dp)).padding(11.dp), fontSize = 12.sp) }
        }
    }
}
@Composable private fun PrayerScreen(nav: NavHostController) {
    val c = LocalContext.current
 var times by remember { mutableStateOf<ShiaPrayerTimes?>(null) }
    val launch = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result.values.any { it }) kotlinx.coroutines.MainScope().launch { LocationSupport.lastKnownLocation(c)?.let { times = ShiaPrayerCalculator.calculate(it.latitude,
            it.longitude) } }
    }
    Page("مواقيت الصلاة", Icons.Default.AccessTime, nav) { p ->
        Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Info("حسب كتيب مواقيت الصلاة للسيد السيستاني", "تعرض هذه الشاشة الفجر والظهر والمغرب فقط، كما في النسخة الأصلية.",
                Icons.Default.AccessTime)
            Spacer(Modifier.height(10.dp))
 TimeRow("الفجر", times?.fajr, Icons.Default.NightsStay)
 TimeRow("الظهر", times?.dhuhr, Icons.Default.WbSunny)
 TimeRow("المغرب", times?.maghrib, Icons.Default.WbTwilight)
            Spacer(Modifier.height(10.dp))
 ActionButton("تحديد الموقع", { launch.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
     Modifier.fillMaxWidth())
        }
    }
}
@Composable private fun TimeRow(title: String, value: Calendar?, icon: ImageVector) {
    Card(Modifier.fillMaxWidth().padding(vertical = 5.dp), RoundedCornerShape(19.dp)) { Row(Modifier.padding(17.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(45.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.1f)),
            contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) }
        Spacer(Modifier.width(11.dp))
 Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 17.sp)
 Text(value?.let { SimpleDateFormat("hh:mm a", Locale("ar")).format(it.time) } ?: "—", color = MaterialTheme.colorScheme.secondary,
     fontSize = 19.sp, fontWeight = FontWeight.Bold)
    } }
}
@Composable
private fun RakahCounterScreen(nav: NavHostController) {
    var count by rememberSaveable { mutableIntStateOf(0) }
    Page("عداد الركعات", Icons.Default.FormatListNumbered, nav) { p ->
        Column(
            Modifier.fillMaxSize()
                .padding(p)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Info(
                "عداد الركعات",
                "عداد مستقل للركعات أثناء الصلاة",
                Icons.Default.FormatListNumbered
            )
            Spacer(Modifier.height(24.dp))
            Card(
                Modifier.fillMaxWidth(),
                RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "عدد الركعات",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier.size(190.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier.size(158.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                count.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 58.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(Modifier.height(22.dp))
                    ActionButton(
                        "إضافة ركعة",
                        { count++ },
                        Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    ActionButton(
                        "تصفير العداد",
                        { count = 0 },
                        Modifier.fillMaxWidth(),
                        MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
@Composable private fun QadaScreen(nav: NavHostController) {
    val names = listOf("الفجر", "الظهر", "العصر", "المغرب", "العشاء")
    val counts = remember { mutableStateMapOf<String, Int>().apply { names.forEach { this[it] = 0 } } }
    Page("قضاء الصلاة", Icons.Default.CheckCircleOutline, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Info("سجل قضاء الصلاة", "متابعة الصلوات الفائتة وخطة القضاء وسجل الإنجاز", Icons.Default.CheckCircleOutline) }
        item { Text("إجمالي الصلوات الفائتة", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
        items(names) { n -> Card(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp)) { Row(Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically) { Text(n, Modifier.weight(1f), fontWeight = FontWeight.Bold)
 IconButton({ counts[n] = (counts[n] ?: 0) + 1 }) { Icon(Icons.Default.AddCircle, "تم قضاء صلاة") }
 Text((counts[n] ?: 0).toString(), Modifier.width(38.dp), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
 IconButton({ counts[n] = ((counts[n] ?: 0) - 1).coerceAtLeast(0) }) { Icon(Icons.Default.RemoveCircleOutline,
     "تراجع") } } } }
        item { Text("سجل الإنجاز حسب التاريخ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
 Text("العداد المحلي متاح الآن، وتفاصيل السجل والنسخ الاحتياطي تبقى من بيانات الخدمة الأصلية.", fontSize = 12.sp,
     color = MaterialTheme.colorScheme.onSurfaceVariant) }
    } }
}
@Composable private fun QiblaScreen(nav: NavHostController) {
    val c = LocalContext.current
 var bearing by rememberSaveable { mutableStateOf<Double?>(null) }
    val launch = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result -> if (result.values.any { it }) kotlinx.coroutines.MainScope().launch { LocationSupport.lastKnownLocation(c)?.let { bearing = LocationSupport.initialBearing(it.latitude,
        it.longitude, LocationSupport.MECCA_LAT, LocationSupport.MECCA_LNG) } } }
    Page("اتجاه القبلة", Icons.Default.Explore, nav) { p -> Column(Modifier.fillMaxSize().padding(p).padding(17.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("حساب اتجاه القبلة حسب موقعك", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
 Spacer(Modifier.height(25.dp))
        Box(Modifier.size(215.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) { Box(Modifier.size(178.dp).clip(CircleShape).border(2.dp,
            MaterialTheme.colorScheme.secondary, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Navigation,
            null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(112.dp).rotate((bearing ?: 0.0).toFloat())) } }
        Spacer(Modifier.height(18.dp))
 Text(bearing?.let { "السمت ${it.f1()}°" } ?: "اضغط لتحديد اتجاه القبلة من موقعك", fontWeight = FontWeight.Bold,
     textAlign = TextAlign.Center)
 Spacer(Modifier.height(15.dp))
 ActionButton("تحديد القبلة", { launch.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) },
     Modifier.fillMaxWidth())
    } }
}
@Composable private fun CompassScreen(nav: NavHostController) {
    val c = LocalContext.current
 val places = remember { LegacySourceRepository.sacredPlaces(c) }
 var loc by remember { mutableStateOf<android.location.Location?>(null) }
    val launch = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result -> if (result.values.any { it }) kotlinx.coroutines.MainScope().launch { loc = LocationSupport.lastKnownLocation(c) } }
    Page("اتجاه مراقد المعصومين (ع)", Icons.Default.Explore, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Info("حدد موقع المراقد الشريفة والاماكن المقدسة حسب موقعك", "يعرض الاتجاه الجغرافي والمسافة عند تحديد موقعك.",
            Icons.Default.Explore) }
        items(places) { x -> val b = loc?.let { LocationSupport.initialBearing(it.latitude, it.longitude,
            x.lat, x.lng) }
 AnimatedSurface(Modifier.fillMaxWidth()) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Mosque,
     null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
 Spacer(Modifier.width(10.dp))
 Column(Modifier.weight(1f)) { Text(x.name, fontWeight = FontWeight.Bold)
 Text(x.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
 b?.let { Text("${it.f1()}°", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold) } } } }
        item { ActionButton("تحديد موقعي", { launch.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)) }, Modifier.fillMaxWidth()) }
    } }
}
@Composable private fun CrescentScreen(nav: NavHostController) {
    val c = LocalContext.current
 val data = remember { LegacySourceRepository.hijriMonths(c) }
    Page("مواقيت الأهلة", Icons.Default.NightlightRound, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p),
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Info("حسب كراس الأهلة للسيد السيستاني", "بيانات التقويم الموجودة في ملفات المشروع الأصلية.",
            Icons.Default.NightlightRound) }
        items(data?.hijriMonths ?: emptyList()) { m -> AnimatedSurface(Modifier.fillMaxWidth()) { Row(Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(43.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondary.copy(.15f)),
            contentAlignment = Alignment.Center) { Text(m.number.toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary) }
 Spacer(Modifier.width(11.dp))
 Column(Modifier.weight(1f)) { Text(m.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
 Text("${m.hijriYear} هـ • ${m.startDate} • ${m.days} يوم", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) } } } }
        item { Text("آخر تحديث للبيانات: ${data?.lastUpdated ?: "—"}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    } }
}
@Composable private fun DuasHub(nav: NavHostController) { Page("الأدعية والزيارات", Icons.Default.Mosque,
    nav) { p -> Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(16.dp)) { Info("الأدعية والزيارات",
    "اختر القسم الذي تريده", Icons.Default.Mosque)
 Spacer(Modifier.height(15.dp))
 HomeCard(nav, "الأدعية", "دعاء كميل، التوسل، الفرج، الاستخارة، وأدعية أخرى", Icons.Default.AutoStories, "duas")
 Spacer(Modifier.height(10.dp))
 HomeCard(nav, "الزيارات", "عاشوراء، وارث، الأربعين، العباس، علي الأكبر، الأصحاب", Icons.Default.Mosque,
     "ziarat") } } }
@Composable private fun DuasList(nav: NavHostController) { val c = LocalContext.current
 val d = remember { LegacySourceRepository.duas(c) }
 SimpleList(nav, "الأدعية", Icons.Default.AutoStories, d.map { it.title to it.subtitle }, "dua") }
@Composable private fun ZiaratList(nav: NavHostController) { val c = LocalContext.current
 val d = remember { LegacySourceRepository.ziarat(c) }
 SimpleList(nav, "الزيارات", Icons.Default.Mosque, d.map { it.title to it.subtitle }, "ziarat") }
@Composable private fun SimpleList(nav: NavHostController, title: String, icon: ImageVector, data: List<Pair<String,
    String>>, type: String) { Page(title, icon, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p),
    contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { items(data.withIndex().toList()) { x -> AnimatedSurface(Modifier.fillMaxWidth(),
    { nav.navigate("detail/$type/${x.index}") }) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon,
    null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
 Spacer(Modifier.width(11.dp))
 Column(Modifier.weight(1f)) { Text(x.value.first, fontWeight = FontWeight.Bold)
 Text(x.value.second, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
 Icon(Icons.Default.ArrowBackIosNew, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp)) } } } } } }
@Composable private fun Books(nav: NavHostController) { Page("الكتب الدينية", Icons.Default.LibraryBooks,
    nav) { p -> Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(16.dp)) { HomeCard(nav,
    "القرآن الكريم", "نص القرآن الكريم كاملاً تصفح حسب السور", Icons.Default.MenuBook, "quran")
 Spacer(Modifier.height(12.dp))
 HomeCard(nav, "مفاتيح الجنان", "الأدعية والزيارات والمناجاة - تصفح الكتاب كاملاً مع إمكانية الانتقال لأي صفحة",
     Icons.Default.AutoStories, "mafatih")
 Spacer(Modifier.height(12.dp))
 HomeCard(nav, "الصحيفة السجادية", "أدعية ومناجاة الإمام زين العابدين عليه السلام كاملة", Icons.Default.MenuBook,
     "sahifa") } } }
@Composable private fun QuranNotice(nav: NavHostController) { Page("القرآن الكريم", Icons.Default.MenuBook,
    nav) { p -> Column(Modifier.fillMaxSize().padding(p).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center) { Icon(Icons.Default.MenuBook, null, tint = MaterialTheme.colorScheme.primary,
    modifier = Modifier.size(70.dp))
 Text("القرآن الكريم", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 15.dp))
 Text("شاشة المصدر الأصلي تعتمد على مكتبة quran الخارجية لعرض أسماء السور ونصوصها. هذه البيانات غير موجودة ضمن ملفات المشروع المصدرية، لذلك لن أضع نصًا قرآنيًا من مصدر خارجي من عندي.",
     textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 24.sp,
     modifier = Modifier.padding(top = 10.dp)) } } }
@Composable private fun BookList(nav: NavHostController, title: String, file: String) { val data = rememberAssetList(file)
 Page(title, Icons.Default.AutoStories, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p), contentPadding = PaddingValues(16.dp),
     verticalArrangement = Arrangement.spacedBy(8.dp)) { items(data) { e -> AnimatedSurface(Modifier.fillMaxWidth(),
     { nav.navigate("detail/${if (file.startsWith("sahifa")) "sahifa" else "mafatih"}/${e.id}") }) { Row(Modifier.padding(15.dp),
     verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.AutoStories, null, tint = MaterialTheme.colorScheme.primary)
 Spacer(Modifier.width(10.dp))
 Text(e.title.ifBlank { "قسم ${e.id + 1}" }, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
 Icon(Icons.Default.ArrowBackIosNew, null, tint = MaterialTheme.colorScheme.secondary) } } } } } }
@Composable private fun Tasbih(nav: NavHostController) {
    val zahra = listOf("الله أكبر" to 34, "الحمد لله" to 33, "سبحان الله" to 33)
    val other = listOf("أستغفر الله" to 100, "لا حول ولا قوة إلا بالله" to 100, "سبحان الله وبحمده سبحان الله العظيم" to 100,
        "اللهم صل على محمد وآل محمد" to 100)
    var mode by rememberSaveable { mutableIntStateOf(0) }
 var index by rememberSaveable { mutableIntStateOf(0) }
 var count by rememberSaveable { mutableIntStateOf(0) }
    val list = if (mode == 0) zahra else other
 val current = list[index.coerceIn(0, list.lastIndex)]
    Page("المسبحة الإلكترونية", Icons.Default.Fingerprint, nav) { p -> Column(Modifier.fillMaxSize().padding(p).padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Toggle("تسبيح الزهراء",
            mode == 0) { mode = 0
 index = 0
 count = 0 }
 Toggle("أذكار أخرى", mode == 1) { mode = 1
 index = 0
 count = 0 } }
        Spacer(Modifier.height(30.dp))
 Text(current.first, fontSize = 28.sp, fontWeight = FontWeight.Bold)
 Text("$count / ${current.second}", fontSize = 20.sp, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
 Spacer(Modifier.height(20.dp))
        Surface(Modifier.size(190.dp), CircleShape, color = MaterialTheme.colorScheme.primary) { Box(contentAlignment = Alignment.Center) { Surface(Modifier.size(162.dp).clickable { count++
 if (count >= current.second) { count = 0
 if (mode == 0 && index < list.lastIndex) index++ } }, CircleShape, color = MaterialTheme.colorScheme.surface) { Column(horizontalAlignment = Alignment.CenterHorizontally,
     verticalArrangement = Arrangement.Center) { Icon(Icons.Default.Fingerprint, null, tint = MaterialTheme.colorScheme.primary,
     modifier = Modifier.size(52.dp))
 Text("تسبيح", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) } } } }
        Spacer(Modifier.height(18.dp))
 ActionButton("إعادة البدء", { count = 0 }, Modifier.fillMaxWidth(), MaterialTheme.colorScheme.secondary)
        list.forEachIndexed { i, x -> Text("${x.first} (${x.second})", Modifier.fillMaxWidth().padding(4.dp).clickable { index = i
 count = 0 }.background(if (i == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
     RoundedCornerShape(12.dp)).padding(9.dp), color = if (i == index) Color.White else MaterialTheme.colorScheme.onSurface) }
    } }
}
@Composable private fun Toggle(text: String, selected: Boolean, onClick: () -> Unit) { Surface(Modifier.weight(1f).clickable(onClick = onClick),
    RoundedCornerShape(15.dp), color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest) { Box(Modifier.fillMaxWidth().padding(vertical = 12.dp),
    contentAlignment = Alignment.Center) { Text(text, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
    fontWeight = FontWeight.Bold, fontSize = 13.sp) } } }
@Composable private fun Dates(nav: NavHostController) { val c = LocalContext.current
 val data = remember { LegacySourceRepository.dates(c) }
 Page("ولادات ووفيات أهل البيت", Icons.Default.CalendarMonth, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p),
     contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { item { Info("تواريخ ولادة واستشهاد المعصومين عليهم السلام",
     "المصدر: ahlulbayt_dates_data.dart", Icons.Default.CalendarMonth) }
 items(data) { e -> AnimatedSurface(Modifier.fillMaxWidth()) { Column(Modifier.padding(15.dp)) { Text(e.personName,
     fontWeight = FontWeight.Bold, fontSize = 16.sp)
 Text(if (e.kind.contains("birth")) "ولادة" else "وفاة/استشهاد", color = MaterialTheme.colorScheme.secondary,
     fontSize = 12.sp)
 Text(e.description, fontSize = 13.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 6.dp))
 if (e.source.isNotBlank()) Text(e.source, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
     modifier = Modifier.padding(top = 5.dp)) } } } } } }
@Composable private fun Quotes(nav: NavHostController, title: String, type: String) { val c = LocalContext.current
 val data = remember { when (type) { "hussein" -> LegacySourceRepository.quotes(c)
 "sabaya" -> LegacySourceRepository.sabaya(c)
 else -> LegacySourceRepository.mawadda(c) } }
 Page(title, if (type == "sabaya") Icons.Default.RecordVoiceOver else Icons.Default.FormatQuote, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p),
     contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { items(data.withIndex().toList()) { x -> AnimatedSurface(Modifier.fillMaxWidth(),
     { nav.navigate("detail/$type/${x.index}") }) { Column(Modifier.padding(16.dp)) { Text(x.value.text, fontSize = 15.sp,
     lineHeight = 25.sp, fontWeight = FontWeight.Medium)
 if (x.value.occasion.isNotBlank()) Text(x.value.occasion, color = MaterialTheme.colorScheme.secondary,
     fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
 if (x.value.source.isNotBlank()) Text(x.value.source, color = MaterialTheme.colorScheme.onSurfaceVariant,
     fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp)) } } } } } }
@Composable private fun Battle(nav: NavHostController) { val c = LocalContext.current
 val data = remember { LegacySourceRepository.battle(c) }
 Page("احداث معركة الطف", Icons.Default.HistoryEdu, nav) { p -> LazyColumn(Modifier.fillMaxSize().padding(p),
     contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { items(data) { e -> AnimatedSurface(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("اليوم ${e.day}",
     color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
 Text(e.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
 Text(e.description, fontSize = 13.sp, lineHeight = 21.sp, modifier = Modifier.padding(top = 6.dp))
 Text(e.source, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 5.dp)) } } } } } }
@Composable private fun Settings(nav: NavHostController, dark: Boolean, setDark: (Boolean) -> Unit) { val c = LocalContext.current
 var notifications by rememberSaveable { mutableStateOf(true) }
 Page("الإعدادات", Icons.Default.Settings, nav) { p -> Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(16.dp)) { Setting("الوضع الليلي",
     dark, setDark)
 Setting("تفعيل الإشعارات", notifications) { notifications = it }
 Setting("تذكير بمناسبة", true) {}
 Spacer(Modifier.height(12.dp))
 Info("تواصل معنا", "mhtraf6@gmail.com", Icons.Default.Email)
 Spacer(Modifier.height(10.dp))
 Info("شارك التطبيق", "انشر التطبيق مع الأصدقاء والعائلة", Icons.Default.Share)
 Spacer(Modifier.height(10.dp))
 ActionButton("فتح إعدادات الموقع", { runCatching { c.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) } },
     Modifier.fillMaxWidth(), MaterialTheme.colorScheme.secondary) } } }
@Composable private fun Setting(text: String, value: Boolean, onChange: (Boolean) -> Unit) { Card(Modifier.fillMaxWidth().padding(vertical = 5.dp),
    RoundedCornerShape(18.dp)) { Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Text(text,
    Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
 Switch(checked = value, onCheckedChange = onChange) } } }
@Composable private fun Detail(nav: NavHostController, type: String, index: Int) {
    val c = LocalContext.current
    when (type) {
        "mafatih", "sahifa" -> { val data = rememberAssetList(if (type == "sahifa") "sahifa_sajjadiya.json" else "mafatih_aljanan.json")
 val e = data.firstOrNull { it.id == index } ?: data.getOrNull(index)
 TextDetail(nav, e?.title ?: "المحتوى", e?.content ?: "لم يتم العثور على المحتوى.") }
        "dua" -> { val e = remember { LegacySourceRepository.duas(c) }.getOrNull(index)
 TextDetail(nav, e?.title ?: "الدعاء", e?.content ?: "لم يتم العثور على المحتوى.") }
        "ziarat" -> { val e = remember { LegacySourceRepository.ziarat(c) }.getOrNull(index)
 TextDetail(nav, e?.title ?: "الزيارة", e?.content ?: "لم يتم العثور على المحتوى.") }
        else -> { val data = remember { when (type) { "hussein" -> LegacySourceRepository.quotes(c)
 "sabaya" -> LegacySourceRepository.sabaya(c)
 else -> LegacySourceRepository.mawadda(c) } }
 val e = data.getOrNull(index)
 TextDetail(nav, when (type) { "hussein" -> "أقوال الإمام الحسين عليه السلام"
 "sabaya" -> "خطب اهل البيت "
 else -> "مودة أهل البيت عليهم السلام" }, buildString { append(e?.text ?: "")
 if (!e?.occasion.isNullOrBlank()) { append("\n\n")
 append(e?.occasion) }
 if (!e?.source.isNullOrBlank()) { append("\n\n")
 append(e?.source) } }) }
    }
}
@Composable private fun TextDetail(nav: NavHostController, title: String, content: String) { Page(title,
    Icons.Default.AutoStories, nav) { p -> Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(18.dp)) { Card(Modifier.fillMaxWidth(),
    RoundedCornerShape(22.dp)) { Text(content, Modifier.padding(20.dp), fontSize = 18.sp, lineHeight = 32.sp) } } } }
@Composable private fun rememberAssetList(file: String): List<TextEntry> { val c = LocalContext.current
 return remember(file) { LegacyAssets.loadList(c, file, TextEntry.serializer()) } }
private fun openMaps(c: Context, lat: Double, lng: Double) { val direct = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=$lat,$lng")).setPackage("com.google.android.apps.maps")
 val fallback = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng"))
 runCatching { c.startActivity(direct) }.onFailure { c.startActivity(fallback) } }
private fun openUrl(c: Context, url: String) { runCatching { c.startActivity(Intent(Intent.ACTION_VIEW,
    Uri.parse(url))) } }
private fun Double.f1() = String.format(Locale.US, "%.1f", this)
