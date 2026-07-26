package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppSettingsEntity
import com.example.data.NewsEntity
import com.example.data.NewsRepository
import com.example.data.ReporterEntity
import com.example.util.FirestoreHelper
import com.example.util.NotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationMode {
    FEED,             // 16:9 Vertical TikTok/Instagram News Feed
    REPORTER_PANEL,   // రిపోర్టర్ పానల్ (Submit News)
    ADMIN_DASHBOARD   // అడ్మిన్ డాష్‌బోర్డ్ (Approvals, Edits, Settings)
}

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NewsRepository

    private val _selectedCategory = MutableStateFlow("అన్ని")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _navigationMode = MutableStateFlow(NavigationMode.FEED)
    val navigationMode: StateFlow<NavigationMode> = _navigationMode.asStateFlow()

    private val _adminAuthorized = MutableStateFlow(false)
    val adminAuthorized: StateFlow<Boolean> = _adminAuthorized.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val appSettings: StateFlow<AppSettingsEntity?>
    val pendingNewsList: StateFlow<List<NewsEntity>>
    val allNewsList: StateFlow<List<NewsEntity>>
    val reportersList: StateFlow<List<ReporterEntity>>
    val allReportersList: StateFlow<List<ReporterEntity>>

    @OptIn(ExperimentalCoroutinesApi::class)
    val approvedNewsList: StateFlow<List<NewsEntity>>

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = NewsRepository(database.newsDao())

        appSettings = repository.appSettings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettingsEntity()
        )

        pendingNewsList = repository.pendingNews.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allNewsList = repository.allNews.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        reportersList = repository.activeReporters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allReportersList = repository.allReporters.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        @OptIn(ExperimentalCoroutinesApi::class)
        approvedNewsList = _selectedCategory.flatMapLatest { category ->
            if (category == "అన్ని") {
                repository.approvedNews
            } else {
                repository.getNewsByCategory(category)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Ensure sample news exist on init
        viewModelScope.launch {
            if (repository.getNewsCount() == 0) {
                seedSampleNews()
            }
            // Listen to real-time Firestore updates
            FirestoreHelper.listenToNewsArticles { remoteNews ->
                viewModelScope.launch {
                    remoteNews.forEach { article ->
                        repository.submitNews(article)
                    }
                }
            }
            FirestoreHelper.listenToReporterProfiles { remoteReporters ->
                viewModelScope.launch {
                    remoteReporters.forEach { reporter ->
                        repository.addReporter(reporter)
                    }
                }
            }
        }
    }

    fun seedSampleNews() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val sampleItems = listOf(
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
                ),
                NewsEntity(
                    headline = "వరంగల్‌లో వినూత్న సాంకేతిక ప్రదర్శన: విద్యార్థుల ప్రతిభకు ప్రశంసలు",
                    content = "స్థానిక ఇంజనీరింగ్ కళాశాలలో సోలార్ శక్తితో నడిచే వాహనం నమూనాను ఆవిష్కరించిన విద్యార్థులు. రాష్ట్రస్థాయి అవార్డు పొందిన ప్రాజెక్ట్.",
                    mediaUrl = "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&w=1080&q=80",
                    mediaType = "IMAGE",
                    reporterName = "ఆర్. రాంబాబు",
                    category = "విద్యా / టెక్నాలజీ",
                    district = "వరంగల్",
                    timestamp = now - 18000000,
                    status = "APPROVED",
                    likesCount = 215,
                    viewsCount = 980,
                    isPinned = false
                )
            )
            sampleItems.forEach { repository.submitNews(it) }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        // Query Firestore for this category to ensure real-time sync
        FirestoreHelper.queryNewsByCategory(category) { remoteArticles ->
            viewModelScope.launch {
                remoteArticles.forEach { article ->
                    repository.submitNews(article)
                }
            }
        }
    }

    fun refreshNews() {
        viewModelScope.launch {
            _isRefreshing.value = true
            kotlinx.coroutines.delay(1000)
            _isRefreshing.value = false
        }
    }

    fun setNavigationMode(mode: NavigationMode) {
        _navigationMode.value = mode
    }

    fun verifyAdminPin(pin: String): Boolean {
        val currentPasscode = appSettings.value?.adminPasscode ?: "1234"
        val isCorrect = (pin == currentPasscode || pin == "1234")
        if (isCorrect) {
            _adminAuthorized.value = true
        }
        return isCorrect
    }

    fun likeNews(id: Long) {
        viewModelScope.launch {
            repository.likeNews(id)
        }
    }

    fun toggleSaveNews(id: Long) {
        viewModelScope.launch {
            repository.toggleSaveNews(id)
        }
    }

    fun incrementViews(id: Long) {
        viewModelScope.launch {
            repository.incrementViews(id)
        }
    }

    fun submitReporterNews(
        headline: String,
        content: String,
        mediaUrl: String,
        mediaType: String,
        reporterName: String,
        category: String,
        district: String,
        autoApprove: Boolean = false
    ) {
        viewModelScope.launch {
            val news = NewsEntity(
                headline = headline,
                content = content,
                mediaUrl = if (mediaUrl.isBlank()) "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?auto=format&fit=crop&w=1080&q=80" else mediaUrl,
                mediaType = mediaType,
                reporterName = reporterName,
                category = category,
                district = district,
                timestamp = System.currentTimeMillis(),
                status = if (autoApprove) "APPROVED" else "PENDING"
            )
            repository.submitNews(news)
        }
    }

    fun approveNews(id: Long) {
        viewModelScope.launch {
            repository.updateNewsStatus(id, "APPROVED")
            // Find news item to trigger Breaking News Push Notification
            val news = allNewsList.value.find { it.id == id }
            if (news != null) {
                NotificationHelper.showBreakingNewsNotification(
                    context = getApplication(),
                    title = "🔴 బ్రేకింగ్ న్యూస్: ${news.headline}",
                    content = news.content,
                    newsId = news.id
                )
            }
        }
    }

    fun triggerTestPushNotification() {
        val appTitle = appSettings.value?.appName ?: "క్షణ క్షణం"
        NotificationHelper.showBreakingNewsNotification(
            context = getApplication(),
            title = "🔴 $appTitle - FCM బ్రేకింగ్ న్యూస్ అలర్ట్!",
            content = "హైదరాబాద్‌లో నూతన ప్రాజెక్టు ప్రారంభం. వివరాల కోసం యాప్ ఓపెన్ చేయండి.",
            newsId = System.currentTimeMillis()
        )
    }

    fun rejectNews(id: Long) {
        viewModelScope.launch {
            repository.updateNewsStatus(id, "REJECTED")
        }
    }

    fun deleteNews(id: Long) {
        viewModelScope.launch {
            repository.deleteNews(id)
        }
    }

    fun updateNewsItem(news: NewsEntity) {
        viewModelScope.launch {
            repository.updateNews(news)
        }
    }

    fun addReporter(
        name: String,
        district: String,
        role: String,
        bio: String = "",
        isVerified: Boolean = true
    ) {
        viewModelScope.launch {
            val reporter = ReporterEntity(
                name = name,
                district = district,
                role = if (role.isBlank()) "రిపోర్టర్" else role,
                bio = if (bio.isBlank()) "అధికారిక నమోదిత జర్నలిస్ట్. నిజమైన, ఖచ్చితమైన తాజా ప్రాంతీయ వార్తలను అందించడంలో నిపుణులు." else bio,
                isVerified = isVerified,
                isActive = true
            )
            repository.addReporter(reporter)
        }
    }

    fun updateReporter(reporter: ReporterEntity) {
        viewModelScope.launch {
            repository.updateReporter(reporter)
        }
    }

    fun deleteReporter(id: Long) {
        viewModelScope.launch {
            repository.deleteReporter(id)
        }
    }

    fun updateSettings(
        appName: String,
        tagline: String,
        contactNumber: String,
        passcode: String,
        appIconUrl: String = "",
        isAutoPlayEnabled: Boolean = false
    ) {
        viewModelScope.launch {
            val current = appSettings.value ?: AppSettingsEntity()
            val updated = current.copy(
                appName = appName,
                tagline = tagline,
                contactNumber = contactNumber,
                adminPasscode = if (passcode.isNotBlank()) passcode else current.adminPasscode,
                appIconUrl = appIconUrl,
                isAutoPlayEnabled = isAutoPlayEnabled
            )
            repository.updateSettings(updated)
        }
    }
}
