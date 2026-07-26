package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [NewsEntity::class, ReporterEntity::class, AppSettingsEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kshana_kshanam_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.newsDao())
                }
            }
        }

        suspend fun populateInitialData(dao: NewsDao) {
            // App settings default
            dao.updateAppSettings(
                AppSettingsEntity(
                    id = 1,
                    appName = "క్షణ క్షణం",
                    tagline = "తాజా వార్తల వీక్షణం",
                    contactNumber = "+91 98765 43210",
                    adminPasscode = "1234"
                )
            )

            // Initial Reporters
            val reporters = listOf(
                ReporterEntity(name = "వి. ప్రకాష్ రెడ్డి", district = "హైదరాబాద్", role = "చీఫ్ రిపోర్టర్"),
                ReporterEntity(name = "కె. సతీష్ కుమార్", district = "విజయవాడ", role = "సీనియర్ రిపోర్టర్"),
                ReporterEntity(name = "ఎస్. అనురాధ", district = "విశాఖపట్నం", role = "స్పోర్ట్స్ రిపోర్టర్"),
                ReporterEntity(name = "ఆర్. రాంబాబు", district = "వరంగల్", role = "క్రైమ్ అండ్ క్రైమ్ బిట్"),
                ReporterEntity(name = "జి. శ్రీనివాస్", district = "తిరుపతి", role = "ప్రత్యేక ప్రతినిధి")
            )
            reporters.forEach { dao.insertReporter(it) }

            // Initial Approved News Stories
            val now = System.currentTimeMillis()
            val initialNews = listOf(
                NewsEntity(
                    headline = "హైదరాబాద్‌లో కొత్త మెట్రో లైన్ పనులకు శంకుస్థాపన చేసిన ప్రభుత్వం",
                    content = "నగర రవాణా సౌకర్యాన్ని మరింత వేగవంతం చేసేందుకు రూ. 2,500 కోట్ల వ్యయంతో రెండవ విడత మెట్రో విస్తరణ పనులు ప్రారంభమైనట్లు అధికారులు తెలిపారు. ఈ మార్గం ద్వారా లక్షలాది మంది ప్రయాణికులకు సమయం ఆదా కానుంది.",
                    mediaUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=1080&q=80",
                    mediaType = "IMAGE",
                    reporterName = "వి. ప్రకాష్ రెడ్డి",
                    category = "రాజకీయాలు",
                    district = "హైదరాబాద్",
                    timestamp = now,
                    status = "APPROVED",
                    likesCount = 342,
                    viewsCount = 1250,
                    isPinned = true
                ),
                NewsEntity(
                    headline = "డిజిటల్ విప్లవంతో గ్రామీణ రైతులకు ప్రత్యక్ష లబ్ధి: కొత్త సేద్యపు సమాచార వ్యవస్థ",
                    content = "రైతులకు వాతావరణ హెచ్చరికలు, మార్కెట్ ధరలు క్షణ క్షణం అందిస్తూ పంట నష్టాలను అరికట్టేందుకు సరికొత్త యాప్‌ను ప్రవేశపెట్టారు. వ్యవసాయ నిపుణుల సలహాలు నేరుగా వాట్సాప్ ద్వారా పొందే అవకాశం కూడా కల్పించారు.",
                    mediaUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=1080&q=80",
                    mediaType = "IMAGE",
                    reporterName = "కె. సతీష్ కుమార్",
                    category = "తాజా వార్తలు",
                    district = "విజయవాడ",
                    timestamp = now - 3600000,
                    status = "APPROVED",
                    likesCount = 512,
                    viewsCount = 2100,
                    isPinned = false
                ),
                NewsEntity(
                    headline = "ఉత్కంఠభరిత విజయం సాధించి టీమిండియా సంచలనం: క్రికెట్ అభిమానుల సంబరాలు",
                    content = "అద్భుత ప్రదర్శనతో ఆఖరి ఓవర్‌లో సిక్సర్‌తో మ్యాచ్‌ను గెలిపించి సిరీస్‌ను కైవసం చేసుకుంది భారత జట్టు. స్టేడియంలో వేలాది మంది అభిమానులు విజయోత్సవం జరుపుకున్నారు.",
                    mediaUrl = "https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=1080&q=80",
                    mediaType = "IMAGE",
                    reporterName = "ఎస్. అనురాధ",
                    category = "క్రీడలు",
                    district = "విశాఖపట్నం",
                    timestamp = now - 7200000,
                    status = "APPROVED",
                    likesCount = 890,
                    viewsCount = 3400,
                    isPinned = false
                ),
                NewsEntity(
                    headline = "భారీ బడ్జెట్ పాన్-ఇండియా చిత్రం టీజర్ విడుదల: రికార్డు వ్యూస్‌తో ట్రెండింగ్",
                    content = "అత్యంత అద్భుతమైన విజువల్ ఎఫెక్ట్స్‌తో తెరకెక్కిన కొత్త సినిమా టీజర్ యూట్యూబ్‌లో కొన్ని గంటల్లోనే కోటి మందికి పైగా వీక్షించి నంబర్ వన్ స్థానంలో ట్రెండ్ అవుతోంది.",
                    mediaUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1080&q=80",
                    mediaType = "VIDEO",
                    reporterName = "ఆర్. రాంబాబు",
                    category = "సినిమా",
                    district = "హైదరాబాద్",
                    timestamp = now - 10800000,
                    status = "APPROVED",
                    likesCount = 640,
                    viewsCount = 2800,
                    isPinned = false
                ),
                NewsEntity(
                    headline = "పర్యాటక శోభతో వెలిగిపోతున్న తిరుపతి పరిసరాలు: ఆధ్యాత్మిక ఉత్సవాల సందడి",
                    content = "ప్రత్యేక అలంకరణలతో వర్ధిల్లుతున్న పుణ్యక్షేత్రం. దేశం నలుమూలల నుండి విశేషంగా తరలివచ్చిన భక్తులకు క్యూలైన్లలో అల్పాహారం, తాగునీటి సదుపాయాలు నిరంతరం అందుబాటులో ఉంచారు.",
                    mediaUrl = "https://images.unsplash.com/photo-1561361513-2d000a50f0dc?auto=format&fit=crop&w=1080&q=80",
                    mediaType = "IMAGE",
                    reporterName = "జి. శ్రీనివాస్",
                    category = "భక్తి/ఆధ్యాత్మికం",
                    district = "తిరుపతి",
                    timestamp = now - 14400000,
                    status = "APPROVED",
                    likesCount = 420,
                    viewsCount = 1900,
                    isPinned = false
                )
            )
            initialNews.forEach { dao.insertNews(it) }

            // Initial Pending Sample News for Reporter / Admin Demonstration
            dao.insertNews(
                NewsEntity(
                    headline = "వరంగల్‌లో వినూత్న సాంకేతిక ప్రదర్శన: విద్యార్థుల ప్రతిభకు ప్రశంసలు",
                    content = "స్థానిక ఇంజనీరింగ్ కళాశాలలో సోలార్ శక్తితో నడిచే వాహనం నమూనాను ఆవిష్కరించిన విద్యార్థులు. రాష్ట్రస్థాయి అవార్డు పొందిన ప్రాజెక్ట్.",
                    mediaUrl = "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&w=1080&q=80",
                    mediaType = "IMAGE",
                    reporterName = "ఆర్. రాంబాబు",
                    category = "విద్యా / టెక్నాలజీ",
                    district = "వరంగల్",
                    timestamp = now - 1800000,
                    status = "PENDING",
                    likesCount = 0,
                    viewsCount = 1,
                    isPinned = false
                )
            )
        }
    }
}
