package com.example

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.NavigationMode
import com.example.ui.NewsViewModel
import com.example.ui.components.TopNewsBar
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.MainFeedScreen
import com.example.ui.screens.ReporterPanelScreen
import com.example.ui.theme.KshanaKshanamTheme
import com.example.ui.theme.NewsBackgroundDark
import com.example.util.FirestoreHelper
import com.example.util.NotificationHelper
import com.example.util.TextToSpeechHelper

class MainActivity : ComponentActivity() {

    private lateinit var ttsHelper: TextToSpeechHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ttsHelper = TextToSpeechHelper(this)

        // Initialize Firebase Firestore & FCM Notification Channel
        FirestoreHelper.initialize(this)
        NotificationHelper.createNotificationChannel(this)
        NotificationHelper.subscribeToBreakingNewsTopic(this)

        // Request POST_NOTIFICATIONS permission on Android 13+ (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            KshanaKshanamTheme {
                val viewModel: NewsViewModel = viewModel()

                val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
                val approvedNews by viewModel.approvedNewsList.collectAsStateWithLifecycle()
                val pendingNews by viewModel.pendingNewsList.collectAsStateWithLifecycle()
                val allNews by viewModel.allNewsList.collectAsStateWithLifecycle()
                val reporters by viewModel.reportersList.collectAsStateWithLifecycle()
                val allReporters by viewModel.allReportersList.collectAsStateWithLifecycle()
                val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
                val navigationMode by viewModel.navigationMode.collectAsStateWithLifecycle()
                val isAdminAuthorized by viewModel.adminAuthorized.collectAsStateWithLifecycle()
                val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

                val appName = appSettings?.appName ?: "క్షణ క్షణం"
                val tagline = appSettings?.tagline ?: "తాజా వార్తల వీక్షణం"
                val appIconUrl = appSettings?.appIconUrl ?: ""
                val isAutoPlayEnabled = appSettings?.isAutoPlayEnabled ?: false

                val categories = listOf(
                    "అన్ని",
                    "తాజా వార్తలు",
                    "రాజకీయాలు",
                    "సినిమా",
                    "క్రీడలు",
                    "క్రైమ్",
                    "భక్తి/ఆధ్యాత్మికం",
                    "విద్యా / టెక్నాలజీ"
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = NewsBackgroundDark,
                    topBar = {
                        TopNewsBar(
                            appName = appName,
                            tagline = tagline,
                            appIconUrl = appIconUrl,
                            currentMode = navigationMode,
                            selectedCategory = selectedCategory,
                            categories = categories,
                            pendingCount = pendingNews.size,
                            onModeSelected = { mode -> viewModel.setNavigationMode(mode) },
                            onCategorySelected = { category -> viewModel.selectCategory(category) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (navigationMode) {
                            NavigationMode.FEED -> {
                                MainFeedScreen(
                                    newsList = approvedNews,
                                    appName = appName,
                                    tagline = tagline,
                                    ttsHelper = ttsHelper,
                                    isAutoPlayEnabled = isAutoPlayEnabled,
                                    isRefreshing = isRefreshing,
                                    onRefresh = { viewModel.refreshNews() },
                                    onLikeClick = { id -> viewModel.likeNews(id) },
                                    onSaveClick = { id -> viewModel.toggleSaveNews(id) },
                                    onViewIncrement = { id -> viewModel.incrementViews(id) }
                                )
                            }

                            NavigationMode.REPORTER_PANEL -> {
                                ReporterPanelScreen(
                                    reporters = if (reporters.isNotEmpty()) reporters else allReporters,
                                    categories = categories,
                                    onSubmitNews = { headline, content, mediaUrl, mediaType, reporterName, category, district ->
                                        viewModel.submitReporterNews(
                                            headline = headline,
                                            content = content,
                                            mediaUrl = mediaUrl,
                                            mediaType = mediaType,
                                            reporterName = reporterName,
                                            category = category,
                                            district = district,
                                            autoApprove = false
                                        )
                                    }
                                )
                            }

                            NavigationMode.ADMIN_DASHBOARD -> {
                                AdminDashboardScreen(
                                    isAuthorized = isAdminAuthorized,
                                    pendingNews = pendingNews,
                                    allNews = allNews,
                                    reporters = allReporters,
                                    appSettings = appSettings ?: com.example.data.AppSettingsEntity(),
                                    onVerifyPin = { pin -> viewModel.verifyAdminPin(pin) },
                                    onApproveNews = { id -> viewModel.approveNews(id) },
                                    onRejectNews = { id -> viewModel.rejectNews(id) },
                                    onDeleteNews = { id -> viewModel.deleteNews(id) },
                                    onUpdateNews = { news -> viewModel.updateNewsItem(news) },
                                    onAddReporter = { name, district, role, bio, isVerified -> viewModel.addReporter(name, district, role, bio, isVerified) },
                                    onUpdateReporter = { reporter -> viewModel.updateReporter(reporter) },
                                    onDeleteReporter = { id -> viewModel.deleteReporter(id) },
                                    onUpdateSettings = { newName, newTagline, contact, passcode, iconUrl, autoPlay ->
                                        viewModel.updateSettings(newName, newTagline, contact, passcode, iconUrl, autoPlay)
                                    },
                                    onTestFcmNotification = { viewModel.triggerTestPushNotification() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::ttsHelper.isInitialized) {
            ttsHelper.shutdown()
        }
    }
}
