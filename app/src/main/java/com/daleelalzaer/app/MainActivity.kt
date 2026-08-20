package com.daleelalzaer.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val LightBg = Color(0xFFF6F3EA)
private val LightCard = Color(0xFFFFFFFF)
private val Emerald = Color(0xFF087A63)
private val Teal = Color(0xFF159A8A)
private val Gold = Color(0xFFC49A3A)
private val NightBg = Color(0xFF10231F)
private val NightCard = Color(0xFF19352F)
private val NightText = Color(0xFFF2F1E8)

@Serializable data class TextEntry(val id: Int, val title: String, val content: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DaleelAlzaerApp() }
    }
}

@Composable
fun DaleelAlzaerApp() {
    var dark by rememberSaveable { mutableStateOf(false) }
    val nav = rememberNavController()
    val colors = if (dark) darkColorScheme(primary = Color(0xFF5DD6C0), secondary = Color(0xFFE2C36A), background = NightBg, surface = NightCard, onBackground = NightText, onSurface = NightText)
    else lightColorScheme(primary = Emerald, secondary = Gold, background = LightBg, surface = LightCard, onBackground = Color(0xFF18322D), onSurface = Color(0xFF18322D))
    MaterialTheme(colorScheme = colors) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            NavHost(navController = nav, startDestination = "splash") {
                composable("splash") { Splash { nav.navigate("home") { popUpTo("splash") { inclusive = true } } } }
                composable("home") { Home(nav, dark) { dark = !dark } }
                composable("shrines") { Shrines(nav) }
                composable("worship") { Worship(nav) }
                composable("map") { RouteScreen(nav) }
                composable("qibla") { QiblaScreen(nav) }
                composable("prayer") { PrayerScreen(nav) }
                composable("quran") { ContentListScreen("القرآن الكريم", "قراءة القرآن دون اتصال", emptyList(), "quran", nav) }
                composable("duas") { MafatihScreen(nav) }
                composable("sahifa") { SahifaScreen(nav) }
                composable("books") { BooksScreen(nav) }
                composable("tasbih") { TasbihScreen(nav) }
                composable("more") { More(nav) }
                composable("settings") { SettingsScreen(nav, dark) { dark = it } }
                composable("detail/{source}/{id}") { entry -> ContentDetailScreen(entry.arguments?.getString("source") ?: "mafatih", entry.arguments?.getString("id")?.toIntOrNull() ?: 0, nav) }
            }
        }
    }
}

@Composable private fun Splash(onDone: () -> Unit) {
    LaunchedEffect(Unit) { kotlinx.coroutines.delay(1700); onDone() }
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Mosque, null, Modifier.size(78.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(12.dp))
            Text("دليل الزائر", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Text("رفيقك في الزيارة والعبادة", color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(28.dp)); CircularProgressIndicator(strokeWidth = 3.dp)
        }
    }
}

@Composable private fun Home(nav: NavHostController, dark: Boolean, toggle: () -> Unit) {
    Scaffold(bottomBar = { BottomBar(nav) }) { pad ->
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(pad).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("دليل الزائر", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("رفيقك في الزيارة والعبادة", color = MaterialTheme.colorScheme.secondary) }
                WaterIconButton(if (dark) Icons.Default.LightMode else Icons.Default.DarkMode, "تبديل المظهر", toggle); Spacer(Modifier.width(6.dp)); WaterIconButton(Icons.Default.Search, "بحث") { }
            }
            Spacer(Modifier.height(18.dp)); TodayCard(nav); Spacer(Modifier.height(20.dp)); SectionTitle("الوصول السريع"); Spacer(Modifier.height(10.dp)); QuickGrid(nav)
            Spacer(Modifier.height(20.dp)); JourneyCard(nav); Spacer(Modifier.height(20.dp)); SectionTitle("أعمال ومحتوى اليوم"); Spacer(Modifier.height(10.dp)); TodayContent(nav); Spacer(Modifier.height(22.dp))
        }
    }
}

@Composable private fun TodayCard(nav: NavHostController) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary), shape = RoundedCornerShape(26.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("اليوم", color = Color.White.copy(.8f)); Text("أوقات الصلاة", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold); Text("تتحدد بدقة بعد اختيار موقعك", color = Color.White.copy(.85f)) }; Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(42.dp)) }
            Spacer(Modifier.height(15.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { PrayerMini("الفجر", "—"); PrayerMini("الظهر", "—"); PrayerMini("العصر", "—"); PrayerMini("المغرب", "—") }
            Spacer(Modifier.height(14.dp)); WaterButton("عرض أوقات الصلاة", { nav.navigate("prayer") }, Modifier.fillMaxWidth(), Color.White.copy(.15f), Color.White)
        }
    }
}
@Composable private fun PrayerMini(title:String,time:String){Column(horizontalAlignment=Alignment.CenterHorizontally){Text(title,color=Color.White.copy(.75f),fontSize=12.sp);Text(time,color=Color.White,fontWeight=FontWeight.Bold)}}

@Composable private fun QuickGrid(nav: NavHostController) {
    val items = listOf("المراقد" to (Icons.Default.Mosque to "shrines"), "القبلة" to (Icons.Default.Explore to "qibla"), "الطريق" to (Icons.Default.Map to "map"), "الصلاة" to (Icons.Default.AccessTime to "prayer"), "الأدعية" to (Icons.Default.Favorite to "duas"), "القرآن" to (Icons.Default.MenuBook to "quran"))
    Column(verticalArrangement=Arrangement.spacedBy(10.dp)){items.chunked(2).forEach{row->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){row.forEach{(t,p)->WaterCard(t,p.first,Modifier.weight(1f)){nav.navigate(p.second)}}}}
    }
}
@Composable private fun JourneyCard(nav: NavHostController){Card(shape=RoundedCornerShape(24.dp),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface),modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(18.dp)){Text("رحلتك القادمة",color=MaterialTheme.colorScheme.secondary,fontWeight=FontWeight.Bold);Spacer(Modifier.height(7.dp));Text("الموقع الحالي  →  مرقد الإمام الحسين (ع)",fontWeight=FontWeight.Bold);Spacer(Modifier.height(6.dp));Text("حدد موقعك ووجهتك للحصول على المسار الحقيقي.",color=MaterialTheme.colorScheme.onSurface.copy(.65f));Spacer(Modifier.height(12.dp));WaterButton("ابدأ الطريق",{nav.navigate("map")},Modifier.fillMaxWidth())}}}
@Composable private fun TodayContent(nav:NavHostController){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(10.dp)){WaterCard("دعاء اليوم",Icons.Default.Favorite,Modifier.weight(1f)){nav.navigate("duas")};WaterCard("الصحيفة",Icons.Default.MenuBook,Modifier.weight(1f)){nav.navigate("sahifa")}}}

@Composable private fun Shrines(nav:NavHostController){
    val places=listOf("مرقد الإمام الحسين (ع) — كربلاء","مرقد أبي الفضل العباس (ع) — كربلاء","مرقد الإمام علي (ع) — النجف","مرقد الإمامين الكاظمين (ع) — بغداد","مرقد الإمامين العسكريين (ع) — سامراء","قبر النبي (ص) — المدينة المنورة","البقيع — المدينة المنورة")
    ListPage("المراقد والأماكن",Icons.Default.Mosque,nav,places){title->nav.navigate("map")}
}

@Composable private fun Worship(nav:NavHostController){Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())){PageHeader("العبادة",Icons.Default.Favorite,nav);listOf("القرآن الكريم" to "quran","الأدعية والزيارات" to "duas","الصحيفة السجادية" to "sahifa","أوقات الصلاة" to "prayer","القبلة" to "qibla","التسبيح" to "tasbih").forEach{(t,r)->WaterCard(t,Icons.Default.AutoStories,Modifier.fillMaxWidth()){nav.navigate(r)};Spacer(Modifier.height(10.dp))}}}}

@Composable private fun MafatihScreen(nav:NavHostController){val entries=rememberAssetEntries("mafatih_aljanan.json"); ContentListScreen("مفاتيح الجنان","الأدعية والزيارات",entries,"mafatih",nav)}
@Composable private fun SahifaScreen(nav:NavHostController){val entries=rememberAssetEntries("sahifa_sajjadiya.json"); ContentListScreen("الصحيفة السجادية","أدعية الإمام زين العابدين (ع)",entries,"sahifa",nav)}

@Composable private fun ContentListScreen(title:String,subtitle:String,entries:List<TextEntry>,source:String,nav:NavHostController){Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)){PageHeader(title,Icons.Default.MenuBook,nav);Text(subtitle,color=MaterialTheme.colorScheme.secondary);Spacer(Modifier.height(12.dp));if(entries.isEmpty()){Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(22.dp)){Text("سيتم ربط محتوى القرآن المحلي في خطوة البيانات التالية، دون فقدان ملفات المصدر.",Modifier.padding(20.dp),textAlign=TextAlign.Center)}}else LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){items(entries){e->WaterCard(e.title.ifBlank{"قسم ${e.id+1}"},Icons.Default.AutoStories,Modifier.fillMaxWidth()){nav.navigate("detail/$source/${e.id}")}}}}}}


@Composable private fun ContentDetailScreen(source:String,id:Int,nav:NavHostController){
    val file=if(source=="sahifa") "sahifa_sajjadiya.json" else "mafatih_aljanan.json"
    val entries=rememberAssetEntries(file)
    val entry=entries.firstOrNull{it.id==id}
    Scaffold(bottomBar={BottomBar(nav)}){pad->
        Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)){
            PageHeader(entry?.title?.ifBlank{"المحتوى"} ?: "المحتوى",Icons.Default.AutoStories,nav)
            Card(Modifier.fillMaxSize(),shape=RoundedCornerShape(22.dp)){
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)){
                    Text(entry?.content ?: "لم يتم العثور على المحتوى.",style=MaterialTheme.typography.bodyLarge,lineHeight=30.sp)
                }
            }
        }
    }
}

@Composable private fun BooksScreen(nav:NavHostController){Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)){PageHeader("الكتب",Icons.Default.MenuBook,nav);WaterCard("آهلة — PDF",Icons.Default.PictureAsPdf,Modifier.fillMaxWidth()){ };Spacer(Modifier.height(10.dp));WaterCard("مفاتيح الجنان",Icons.Default.AutoStories,Modifier.fillMaxWidth()){nav.navigate("duas")};Spacer(Modifier.height(10.dp));WaterCard("الصحيفة السجادية",Icons.Default.AutoStories,Modifier.fillMaxWidth()){nav.navigate("sahifa")}}}}

@Composable private fun TasbihScreen(nav:NavHostController){var count by rememberSaveable{mutableIntStateOf(0)};Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally){PageHeader("التسبيح",Icons.Default.Fingerprint,nav);Spacer(Modifier.height(40.dp));Text("$count",fontSize=64.sp,fontWeight=FontWeight.Bold,color=MaterialTheme.colorScheme.primary);Spacer(Modifier.height(25.dp));WaterButton("تسبيح",{count++},Modifier.fillMaxWidth());Spacer(Modifier.height(12.dp));WaterButton("تصفير",{count=0},Modifier.fillMaxWidth(),MaterialTheme.colorScheme.secondary)}}}

@Composable private fun SettingsScreen(nav:NavHostController,dark:Boolean,setDark:(Boolean)->Unit){Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)){PageHeader("الإعدادات",Icons.Default.Settings,nav);SettingRow("الوضع الليلي",dark,setDark);SettingRow("الإشعارات",true){};SettingRow("الموقع",false){};Text("الأذونات ستطلب عند الحاجة فقط للحفاظ على الخصوصية.",Modifier.padding(top=16.dp),color=MaterialTheme.colorScheme.onSurface.copy(.65f))}}}
@Composable private fun SettingRow(title:String,value:Boolean,onChange:(Boolean)->Unit){Card(Modifier.fillMaxWidth().padding(vertical=5.dp),shape=RoundedCornerShape(18.dp)){Row(Modifier.fillMaxWidth().padding(16.dp),verticalAlignment=Alignment.CenterVertically){Text(title,Modifier.weight(1f));Switch(checked=value,onCheckedChange=onChange)}}}

@Composable private fun More(nav:NavHostController){Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp).verticalScroll(rememberScrollState())){PageHeader("المزيد",Icons.Default.Menu,nav);listOf("الكتب" to "books","مفاتيح الجنان" to "duas","الصحيفة السجادية" to "sahifa","التسبيح" to "tasbih","الإعدادات" to "settings").forEach{(t,r)->WaterCard(t,Icons.Default.MenuBook,Modifier.fillMaxWidth()){nav.navigate(r)};Spacer(Modifier.height(10.dp))}}}}

@Composable private fun ListPage(title:String,icon:ImageVector,nav:NavHostController,items:List<String>,click:(String)->Unit){Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)){PageHeader(title,icon,nav);LazyColumn(verticalArrangement=Arrangement.spacedBy(10.dp)){items(items){WaterCard(it,icon,Modifier.fillMaxWidth()){click(it)}}}}}}

@Composable private fun PrayerScreen(nav:NavHostController){Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)){PageHeader("أوقات الصلاة",Icons.Default.AccessTime,nav);Card(Modifier.fillMaxWidth(),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(20.dp)){Text("تحديد الموقع مطلوب",style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));Text("لن أعرض أوقاتًا وهمية. بعد منح إذن الموقع سيتم ربط حسابات الصلاة الأصلية من مشروعك بالنسخة Native.");Spacer(Modifier.height(14.dp));WaterButton("تحديد الموقع",{requestLocationHint(nav.context)},Modifier.fillMaxWidth())}}}}}

@Composable private fun QiblaScreen(nav:NavHostController){
    val context=LocalContext.current
    var bearing by rememberSaveable{mutableStateOf<Double?>(null)}
    var message by rememberSaveable{mutableStateOf("اضغط لتحديد اتجاه القبلة من موقعك") }
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ result->
        if(result.values.any{it}){
            kotlinx.coroutines.MainScope().launch{
                val loc=LocationSupport.lastKnownLocation(context)
                if(loc!=null){bearing=LocationSupport.initialBearing(loc.latitude,loc.longitude,LocationSupport.MECCA_LAT,LocationSupport.MECCA_LNG);message="الاتجاه إلى الكعبة من موقعك"}
                else message="تعذر الحصول على موقع أخير؛ تحقق من تشغيل الموقع."
            }
        } else message="يجب السماح بالموقع لحساب اتجاه القبلة."
    }
    Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp),horizontalAlignment=Alignment.CenterHorizontally){PageHeader("القبلة",Icons.Default.Explore,nav);Spacer(Modifier.height(30.dp));Icon(Icons.Default.Explore,null,Modifier.size(120.dp),tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.height(18.dp));Text(bearing?.let{"السمت ${String.format("%.1f",it)}°"} ?: message,textAlign=TextAlign.Center);Spacer(Modifier.height(18.dp));WaterButton("تحديد القبلة",{launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))},Modifier.fillMaxWidth());Spacer(Modifier.height(10.dp));Text("هذه المرحلة تحسب الاتجاه الجغرافي إلى مكة. حساس دوران الهاتف سيضاف في مرحلة الحساسات والاختبار.",textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurface.copy(.65f),fontSize=12.sp)}}}

@Composable private fun RouteScreen(nav:NavHostController){
    val context=LocalContext.current
    var distance by rememberSaveable{mutableStateOf<Double?>(null)}
    var status by rememberSaveable{mutableStateOf("لم يتم تحديد موقعك بعد") }
    val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ result->
        if(result.values.any{it}){
            kotlinx.coroutines.MainScope().launch{val loc=LocationSupport.lastKnownLocation(context);if(loc!=null){distance=LocationSupport.distanceKm(loc.latitude,loc.longitude,LocationSupport.HUSSAIN_SHRINE_LAT,LocationSupport.HUSSAIN_SHRINE_LNG);status="من موقعك الحالي إلى مرقد الإمام الحسين (ع)"}else status="تعذر الحصول على الموقع الأخير."}}
        } else status="يجب السماح بالموقع لحساب المسافة."
    }
    Scaffold(bottomBar={BottomBar(nav)}){pad->Column(Modifier.fillMaxSize().padding(pad).padding(16.dp)){PageHeader("الخريطة والطريق",Icons.Default.Map,nav);Card(Modifier.fillMaxWidth().padding(bottom=12.dp),shape=RoundedCornerShape(24.dp)){Column(Modifier.padding(20.dp)){Text("وجهة الزيارة",color=MaterialTheme.colorScheme.secondary,fontWeight=FontWeight.Bold);Text("مرقد الإمام الحسين (ع) — كربلاء",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));Text(status);distance?.let{Text("المسافة التقريبية بخط مستقيم: ${String.format("%.1f",it)} كم",fontWeight=FontWeight.Bold)}}};WaterButton("تحديد موقعي وحساب المسافة",{launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION))},Modifier.fillMaxWidth());Spacer(Modifier.height(10.dp));WaterButton("فتح الوجهة في خرائط Google",{openGoogleMaps(context,LocationSupport.HUSSAIN_SHRINE_LAT,LocationSupport.HUSSAIN_SHRINE_LNG)},Modifier.fillMaxWidth(),MaterialTheme.colorScheme.secondary);Spacer(Modifier.height(10.dp));Text("المسار الفعلي وخرائط Offline سيتم ربطهما بعد اختبار محرك الخرائط وتخزين البلاطات، ولن يتم عرض مسار وهمي.",textAlign=TextAlign.Center,color=MaterialTheme.colorScheme.onSurface.copy(.65f),fontSize=12.sp)}}}

@Composable private fun PageHeader(title:String,icon:ImageVector,nav:NavHostController){Row(Modifier.fillMaxWidth().padding(bottom=18.dp),verticalAlignment=Alignment.CenterVertically){WaterIconButton(Icons.Default.ArrowForward,"رجوع"){nav.popBackStack()};Spacer(Modifier.width(10.dp));Icon(icon,null,tint=MaterialTheme.colorScheme.primary);Spacer(Modifier.width(8.dp));Text(title,style=MaterialTheme.typography.headlineSmall,fontWeight=FontWeight.Bold)}}
@Composable private fun SectionTitle(t:String){Text(t,style=MaterialTheme.typography.titleLarge,fontWeight=FontWeight.Bold)}
@Composable private fun WaterCard(title:String,icon:ImageVector,modifier:Modifier,onClick:()->Unit){WaterSurface(onClick,modifier){Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){Box(Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary.copy(.10f)),contentAlignment=Alignment.Center){Icon(icon,null,tint=MaterialTheme.colorScheme.primary)};Spacer(Modifier.width(10.dp));Text(title,fontWeight=FontWeight.SemiBold)}}}
@Composable private fun WaterIconButton(icon:ImageVector,desc:String,onClick:()->Unit){WaterSurface(onClick,Modifier.size(46.dp)){Icon(icon,desc,tint=MaterialTheme.colorScheme.primary)}}
@Composable private fun WaterButton(text:String,onClick:()->Unit,modifier:Modifier,container:Color?=null,content:Color?=null){WaterSurface(onClick,modifier.clip(RoundedCornerShape(18.dp))){Box(Modifier.fillMaxWidth().background(container?:MaterialTheme.colorScheme.primary,RoundedCornerShape(18.dp)).padding(vertical=13.dp),contentAlignment=Alignment.Center){Text(text,color=content?:Color.White,fontWeight=FontWeight.Bold)}}}
@Composable private fun WaterSurface(onClick:()->Unit,modifier:Modifier,content:@Composable()->Unit){var press by remember{mutableStateOf<Offset?>(null)};var wave by remember{mutableFloatStateOf(0f)};val scope=rememberCoroutineScope();Box(modifier.clip(RoundedCornerShape(22.dp)).background(MaterialTheme.colorScheme.surface).pointerInput(Unit){detectTapGestures{position->press=position;wave=0f;scope.launch{animate(0f,1f,animationSpec=tween(520,easing=FastOutSlowInEasing)){v,_->wave=v}};onClick()}}){content();press?.let{center->Canvas(Modifier.matchParentSize().alpha((1f-wave).coerceIn(0f,1f))){val r=maxOf(size.width,size.height)*(0.12f+wave*1.15f);drawCircle(MaterialTheme.colorScheme.primary.copy(alpha=.16f*(1-wave)),center,r,style=androidx.compose.ui.graphics.drawscope.Stroke(width=3.dp.toPx()));drawCircle(Color.White.copy(alpha=.08f*(1-wave)),center,r*.68f)}}}}
@Composable private fun BottomBar(nav:NavHostController){NavigationBar(containerColor=MaterialTheme.colorScheme.surface){val current=nav.currentBackStackEntryAsState().value?.destination?.route;NavItem("home","الرئيسية",Icons.Default.Home,current,nav);NavItem("shrines","المراقد",Icons.Default.Mosque,current,nav);NavItem("worship","العبادة",Icons.Default.Favorite,current,nav);NavItem("map","الخريطة",Icons.Default.Map,current,nav);NavItem("more","المزيد",Icons.Default.Menu,current,nav)}}
@Composable private fun NavItem(route:String,label:String,icon:ImageVector,current:String?,nav:NavHostController){NavigationBarItem(selected=current==route,onClick={nav.navigate(route){launchSingleTop=true;restoreState=true}},icon={Icon(icon,null)},label={Text(label,fontSize=11.sp)})}

@Composable private fun rememberAssetEntries(name:String):List<TextEntry>{val context=LocalContext.current;return remember(name){runCatching{val text=context.assets.open(name).bufferedReader().use{it.readText()};Json{ignoreUnknownKeys=true}.decodeFromString<List<TextEntry>>(text)}.getOrDefault(emptyList())}}
private fun openGoogleMaps(context:android.content.Context,lat:Double,lng:Double){val intent=Intent(Intent.ACTION_VIEW,Uri.parse("geo:$lat,$lng?q=$lat,$lng"));context.startActivity(intent)}
private fun requestLocationHint(context:android.content.Context){val intent=Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);context.startActivity(intent)}
