package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.AppSettingsEntity
import com.example.data.NewsEntity
import com.example.data.ReporterEntity
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NewsBackgroundDark
import com.example.ui.theme.NewsSurfaceDark
import com.example.ui.theme.WhatsAppGreen

@Composable
fun AdminDashboardScreen(
    isAuthorized: Boolean,
    pendingNews: List<NewsEntity>,
    allNews: List<NewsEntity>,
    reporters: List<ReporterEntity>,
    appSettings: AppSettingsEntity,
    onVerifyPin: (String) -> Boolean,
    onApproveNews: (Long) -> Unit,
    onRejectNews: (Long) -> Unit,
    onDeleteNews: (Long) -> Unit,
    onUpdateNews: (NewsEntity) -> Unit,
    onAddReporter: (name: String, district: String, role: String, bio: String, isVerified: Boolean) -> Unit,
    onUpdateReporter: (ReporterEntity) -> Unit,
    onDeleteReporter: (Long) -> Unit,
    onUpdateSettings: (appName: String, tagline: String, contactNumber: String, passcode: String, appIconUrl: String, isAutoPlayEnabled: Boolean) -> Unit,
    onTestFcmNotification: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    if (!isAuthorized) {
        // PIN Entry Screen
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NewsBackgroundDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Admin Lock",
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(54.dp)
                    )

                    Text(
                        text = "అడ్మిన్ డాష్‌బోర్డ్ ప్రవేశం",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )

                    Text(
                        text = "ముఖ్యమైన వివరాలు, రిపోర్టర్లు, యాప్ పేరు మార్చేందుకు PIN నంబర్ ఎంటర్ చేయండి (Default: 1234)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.LightGray
                        )
                    )

                    OutlinedTextField(
                        value = pinInput,
                        onValueChange = {
                            pinInput = it
                            pinError = false
                        },
                        label = { Text("అడ్మిన్ PIN పాస్‌కోడ్", color = Color.LightGray) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = pinError,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CrimsonPrimary
                        ),
                        singleLine = true
                    )

                    if (pinError) {
                        Text(
                            text = "తప్పు పాస్‌కోడ్! సరైన PIN నంబర్ ఎంటర్ చేయండి.",
                            color = LiveRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val success = onVerifyPin(pinInput)
                            if (!success) {
                                pinError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("admin_login_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                    ) {
                        Text("ప్రవేశించండి (Login)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
        return
    }

    // Authorized Admin Dashboard with 4 Dedicated Tabs
    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: All News, 2: Reporters Profiles, 3: App Settings

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NewsBackgroundDark)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = NewsSurfaceDark,
            contentColor = CrimsonPrimary,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = CrimsonPrimary
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("పరిశీలన (${pendingNews.size})", fontWeight = FontWeight.Bold) }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("అన్ని వార్తలు (${allNews.size})", fontWeight = FontWeight.Bold) }
            )

            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("రిపోర్టర్ల ప్రొఫైల్స్ (${reporters.size})", fontWeight = FontWeight.Bold) }
            )

            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("యాప్ సెట్టింగ్స్", fontWeight = FontWeight.Bold) }
            )
        }

        when (selectedTab) {
            0 -> PendingApprovalsTab(
                pendingNews = pendingNews,
                onApproveNews = onApproveNews,
                onRejectNews = onRejectNews
            )
            1 -> AllNewsManagementTab(
                allNews = allNews,
                onUpdateNews = onUpdateNews,
                onDeleteNews = onDeleteNews
            )
            2 -> ReportersManagementTab(
                reporters = reporters,
                onAddReporter = onAddReporter,
                onUpdateReporter = onUpdateReporter,
                onDeleteReporter = onDeleteReporter
            )
            3 -> AppSettingsTab(
                appSettings = appSettings,
                onUpdateSettings = onUpdateSettings,
                onTestFcmNotification = onTestFcmNotification
            )
        }
    }
}

@Composable
fun PendingApprovalsTab(
    pendingNews: List<NewsEntity>,
    onApproveNews: (Long) -> Unit,
    onRejectNews: (Long) -> Unit
) {
    if (pendingNews.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = WhatsAppGreen,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "పరిశీలనలో ఉన్న వార్తలేవీ లేవు!",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "రిపోర్టర్లు పంపే వార్తలు ఇక్కడ ప్రత్యక్షమవుతాయి.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(pendingNews, key = { it.id }) { news ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "✍️ రిపోర్టర్: ${news.reporterName} (${news.district})",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(LiveRed)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("పరిశీలనలో ఉంది", color = Color.White, fontSize = 10.sp)
                            }
                        }

                        Text(
                            text = news.headline,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(
                            text = news.content,
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            maxLines = 3
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { onRejectNews(news.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3E2723)),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = LiveRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("తిరస్కరించు", color = LiveRed, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onApproveNews(news.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("అప్రూవ్ చేయి (Approve)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllNewsManagementTab(
    allNews: List<NewsEntity>,
    onUpdateNews: (NewsEntity) -> Unit,
    onDeleteNews: (Long) -> Unit
) {
    var editingNewsItem by remember { mutableStateOf<NewsEntity?>(null) }

    if (editingNewsItem != null) {
        val item = editingNewsItem!!
        var editedHeadline by remember { mutableStateOf(item.headline) }
        var editedContent by remember { mutableStateOf(item.content) }
        var editedCategory by remember { mutableStateOf(item.category) }
        var editedDistrict by remember { mutableStateOf(item.district) }
        var editedReporterName by remember { mutableStateOf(item.reporterName) }
        var editedMediaUrl by remember { mutableStateOf(item.mediaUrl) }
        var editedMediaType by remember { mutableStateOf(item.mediaType) }
        var editedStatus by remember { mutableStateOf(item.status) }

        val adminMediaPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                editedMediaUrl = it.toString()
            }
        }

        AlertDialog(
            onDismissRequest = { editingNewsItem = null },
            containerColor = NewsSurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = CrimsonPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("వార్తను ఎడిట్ చేయండి", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("📰 శీర్షిక (Headline):", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editedHeadline,
                            onValueChange = { editedHeadline = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    item {
                        Text("📝 వార్త వివరాలు (Content):", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editedContent,
                            onValueChange = { editedContent = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            maxLines = 5
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("వర్గం (Category):", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = editedCategory,
                                    onValueChange = { editedCategory = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("జిల్లా (District):", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = editedDistrict,
                                    onValueChange = { editedDistrict = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        Text("✍️ రిపోర్టర్ పేరు (Reporter):", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = editedReporterName,
                            onValueChange = { editedReporterName = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    item {
                        Text("📷 మీడియా అప్‌లోడ్ / URL:", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = {
                                val mime = if (editedMediaType == "VIDEO") "video/*" else "image/*"
                                adminMediaPickerLauncher.launch(mime)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("📁 గ్యాలరీ నుండి మీడియా అప్‌లోడ్ చేయండి", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = editedMediaUrl,
                            onValueChange = { editedMediaUrl = it },
                            label = { Text("లేదా మీడియా URL లింక్", color = Color.LightGray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("పరిస్థితి (Status):", color = Color.White, fontSize = 13.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(
                                    onClick = { editedStatus = "APPROVED" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (editedStatus == "APPROVED") WhatsAppGreen else Color.DarkGray
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("అప్రూవ్డ్", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { editedStatus = "PENDING" },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (editedStatus == "PENDING") GoldAccent else Color.DarkGray
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("పరిశీలన", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateNews(
                            item.copy(
                                headline = editedHeadline,
                                content = editedContent,
                                category = editedCategory,
                                district = editedDistrict,
                                reporterName = editedReporterName,
                                mediaUrl = editedMediaUrl,
                                mediaType = editedMediaType,
                                status = editedStatus
                            )
                        )
                        editingNewsItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("సేవ్ చేయి", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNewsItem = null }) {
                    Text("రద్దు చేయి", color = Color.Gray)
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(allNews, key = { it.id }) { news ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${news.category} • ${news.reporterName}",
                            color = GoldAccent,
                            fontSize = 11.sp
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    onUpdateNews(news.copy(isPinned = !news.isPinned))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = "Pin",
                                    tint = if (news.isPinned) GoldAccent else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(onClick = { editingNewsItem = news }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                            }

                            IconButton(onClick = { onDeleteNews(news.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = LiveRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Text(news.headline, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

/**
 * DEDICATED REPORTERS MANAGEMENT DASHBOARD TAB
 * Room Database backed CRUD for Reporter Profiles (Name, Bio, District, Role, Verification Status)
 */
@Composable
fun ReportersManagementTab(
    reporters: List<ReporterEntity>,
    onAddReporter: (name: String, district: String, role: String, bio: String, isVerified: Boolean) -> Unit,
    onUpdateReporter: (ReporterEntity) -> Unit,
    onDeleteReporter: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingReporterItem by remember { mutableStateOf<ReporterEntity?>(null) }
    var reporterToDelete by remember { mutableStateOf<ReporterEntity?>(null) }

    // Add New Reporter Dialog
    if (showAddDialog) {
        var addName by remember { mutableStateOf("") }
        var addDistrict by remember { mutableStateOf("") }
        var addRole by remember { mutableStateOf("సీనియర్ రిపోర్టర్") }
        var addBio by remember { mutableStateOf("") }
        var addVerified by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = NewsSurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("కొత్త రిపోర్టర్ ప్రొఫైల్‌ని జత చేయండి", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("రిపోర్టర్ పూర్తి పేరు *", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = addName,
                            onValueChange = { addName = it },
                            placeholder = { Text("ఉదా: పి. సురేష్ కుమార్", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_reporter_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            singleLine = true
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("జిల్లా / ప్రాంతం *", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = addDistrict,
                                    onValueChange = { addDistrict = it },
                                    placeholder = { Text("హైదరాబాద్", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("హోదా (Role)", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = addRole,
                                    onValueChange = { addRole = it },
                                    placeholder = { Text("చీఫ్ రిపోర్టర్", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    item {
                        Text("బయో / ప్రొఫైల్ వివరాలు (Bio)", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = addBio,
                            onValueChange = { addBio = it },
                            placeholder = { Text("అధికారిక జర్నలిస్ట్ వివరాలు, అనుభవం...", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            maxLines = 3
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Text("వెరిఫైడ్ బ్యాడ్జ్ (Verified Badge):", color = Color.White, fontSize = 13.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = addVerified,
                                    onCheckedChange = { addVerified = it },
                                    colors = CheckboxDefaults.colors(checkedColor = WhatsAppGreen, checkmarkColor = Color.White)
                                )
                                Text(if (addVerified) "ఆన్" else "ఆఫ్", color = if (addVerified) WhatsAppGreen else Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (addName.isNotBlank() && addDistrict.isNotBlank()) {
                            onAddReporter(addName, addDistrict, addRole, addBio, addVerified)
                            showAddDialog = false
                        }
                    },
                    enabled = addName.isNotBlank() && addDistrict.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("సేవ్ చేయి (Save)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("రద్దు చేయి", color = Color.Gray)
                }
            }
        )
    }

    // Edit Reporter Dialog
    if (editingReporterItem != null) {
        val rep = editingReporterItem!!
        var repName by remember { mutableStateOf(rep.name) }
        var repDistrict by remember { mutableStateOf(rep.district) }
        var repRole by remember { mutableStateOf(rep.role) }
        var repBio by remember { mutableStateOf(rep.bio) }
        var repVerified by remember { mutableStateOf(rep.isVerified) }

        AlertDialog(
            onDismissRequest = { editingReporterItem = null },
            containerColor = NewsSurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = GoldAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("రిపోర్టర్ ప్రొఫైల్ సవరణ", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("రిపోర్టర్ పేరు:", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = repName,
                            onValueChange = { repName = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("జిల్లా:", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = repDistrict,
                                    onValueChange = { repDistrict = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("హోదా:", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = repRole,
                                    onValueChange = { repRole = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        Text("బయో వివరాలు (Bio):", color = GoldAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = repBio,
                            onValueChange = { repBio = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                            maxLines = 4
                        )
                    }

                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("వెరిఫైడ్ హోదా (Verified):", color = Color.White, fontSize = 13.sp)
                            Button(
                                onClick = { repVerified = !repVerified },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (repVerified) WhatsAppGreen else Color.DarkGray
                                )
                            ) {
                                Text(if (repVerified) "✔ Verified" else "Unverified", fontSize = 11.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateReporter(
                            rep.copy(
                                name = repName,
                                district = repDistrict,
                                role = repRole,
                                bio = repBio,
                                isVerified = repVerified
                            )
                        )
                        editingReporterItem = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary)
                ) {
                    Text("సేవ్ చేయి (Update)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingReporterItem = null }) {
                    Text("రద్దు చేయి", color = Color.Gray)
                }
            }
        )
    }

    // Delete Reporter Confirmation Dialog
    if (reporterToDelete != null) {
        val rep = reporterToDelete!!
        AlertDialog(
            onDismissRequest = { reporterToDelete = null },
            containerColor = NewsSurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = LiveRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("రిపోర్టర్ తొలిగింపు", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("'${rep.name}' ప్రొఫైల్‌ను రూమ్ డాటాబేస్ నుండి ఖచ్చితంగా తొలగించాలనుకుంటున్నారా?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteReporter(rep.id)
                        reporterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                ) {
                    Text("తొలగించు (Delete)", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { reporterToDelete = null }) {
                    Text("రద్దు చేయి", color = Color.Gray)
                }
            }
        )
    }

    val filteredReporters = reporters.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.district.contains(searchQuery, ignoreCase = true) ||
                it.role.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header Banner & Add Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "రిపోర్టర్ల నిర్వహణ పానల్",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "మొత్తం నమోదైన రిపోర్టర్లు: ${reporters.size}",
                                style = MaterialTheme.typography.bodySmall.copy(color = GoldAccent)
                            )
                        }

                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("open_add_reporter_dialog")
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("కొత్తది +", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Search Input Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("రిపోర్టర్ పేరు లేదా జిల్లా ద్వారా వెతకండి...", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldAccent) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Gray)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CrimsonPrimary
                        ),
                        singleLine = true
                    )
                }
            }
        }

        if (filteredReporters.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ఏ రిపోర్టర్ ప్రొఫైల్స్ లభ్యం కాలేదు", color = Color.Gray, fontSize = 14.sp)
                }
            }
        } else {
            items(filteredReporters, key = { it.id }) { reporter ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar Circle
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(CrimsonPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = reporter.name,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 16.sp
                                            )
                                        )

                                        if (reporter.isVerified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Verified Reporter",
                                                tint = WhatsAppGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = "📍 ${reporter.district} • ${reporter.role}",
                                        color = GoldAccent,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Actions Row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        onUpdateReporter(reporter.copy(isVerified = !reporter.isVerified))
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Toggle Verification",
                                        tint = if (reporter.isVerified) WhatsAppGreen else Color.DarkGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(onClick = { editingReporterItem = reporter }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                                }

                                IconButton(onClick = { reporterToDelete = reporter }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Profile", tint = LiveRed, modifier = Modifier.size(20.dp))
                                }
                            }
                        }

                        if (reporter.bio.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1B1B26))
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = reporter.bio,
                                    color = Color.LightGray,
                                    fontSize = 12.sp,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSettingsTab(
    appSettings: AppSettingsEntity,
    onUpdateSettings: (appName: String, tagline: String, contactNumber: String, passcode: String, appIconUrl: String, isAutoPlayEnabled: Boolean) -> Unit,
    onTestFcmNotification: () -> Unit = {}
) {
    var editedAppName by remember(appSettings) { mutableStateOf(appSettings.appName) }
    var editedTagline by remember(appSettings) { mutableStateOf(appSettings.tagline) }
    var editedAppIconUrl by remember(appSettings) { mutableStateOf(appSettings.appIconUrl) }
    var editedContact by remember(appSettings) { mutableStateOf(appSettings.contactNumber) }
    var editedPasscode by remember(appSettings) { mutableStateOf(appSettings.adminPasscode) }
    var editedAutoPlay by remember(appSettings) { mutableStateOf(appSettings.isAutoPlayEnabled) }
    var settingsSavedAlert by remember { mutableStateOf(false) }
    var fcmAlertTriggered by remember { mutableStateOf(false) }

    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            editedAppIconUrl = it.toString()
            settingsSavedAlert = false
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Branding Live Preview Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2A)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🎨 ప్రత్యక్ష యాప్ బ్రాండింగ్ ప్రివ్యూ (Live Preview)",
                        color = GoldAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NewsSurfaceDark)
                            .padding(12.dp)
                    ) {
                        if (editedAppIconUrl.isNotBlank()) {
                            AsyncImage(
                                model = editedAppIconUrl,
                                contentDescription = "App Logo Preview",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📰", fontSize = 22.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = editedAppName.ifBlank { "యాప్ పేరు" },
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = editedTagline.ifBlank { "ట్యాగ్ లైన్" },
                                color = GoldAccent,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = CrimsonPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("యాప్ పేరు, ఐకాన్ & వివరాల సవరణ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    OutlinedTextField(
                        value = editedAppName,
                        onValueChange = {
                            editedAppName = it
                            settingsSavedAlert = false
                        },
                        label = { Text("యాప్ పేరు (App Name - e.g. క్షణ క్షణం)", color = Color.LightGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_name_setting_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    OutlinedTextField(
                        value = editedTagline,
                        onValueChange = {
                            editedTagline = it
                            settingsSavedAlert = false
                        },
                        label = { Text("ట్యాగ్ లైన్ (Tagline - e.g. తాజా వార్తల వీక్షణం)", color = Color.LightGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_tagline_setting_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    Button(
                        onClick = { logoPickerLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("📷 గ్యాలరీ నుండి యాప్ లోగో ఎంచుకోండి (Upload Logo)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedTextField(
                        value = editedAppIconUrl,
                        onValueChange = {
                            editedAppIconUrl = it
                            settingsSavedAlert = false
                        },
                        label = { Text("లేదా లోగో చిత్రం URL పేస్ట్ చేయండి", color = Color.LightGray) },
                        placeholder = { Text("https://example.com/logo.png", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("app_icon_setting_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editedContact,
                        onValueChange = {
                            editedContact = it
                            settingsSavedAlert = false
                        },
                        label = { Text("కంటాక్ట్ ఫోన్ నంబర్", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    OutlinedTextField(
                        value = editedPasscode,
                        onValueChange = {
                            editedPasscode = it
                            settingsSavedAlert = false
                        },
                        label = { Text("అడ్మిన్ PIN పాస్‌కోడ్", color = Color.LightGray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    // Data Saver & Video Auto-Play Setting Switch
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF181824)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎥 వీడియోల ఆటో-ప్లే (Video Auto-Play)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (editedAutoPlay)
                                        "ఆటో-ప్లే ఆన్: ఫీడ్‌లో వీడియోలు స్వయంచాలకంగా ప్లే అవుతాయి."
                                    else
                                        "డేటా సేవర్ ఆన్: ఇంటర్నెట్ డేటా ఆదా చేయడానికి ప్లే బటన్ నొక్కినప్పుడే వీడియో ప్లే అవుతుంది.",
                                    color = if (editedAutoPlay) GoldAccent else Color.LightGray,
                                    fontSize = 11.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = editedAutoPlay,
                                onCheckedChange = {
                                    editedAutoPlay = it
                                    settingsSavedAlert = false
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CrimsonPrimary,
                                    uncheckedThumbColor = Color.Gray,
                                    uncheckedTrackColor = Color(0xFF2A2A38)
                                ),
                                modifier = Modifier.testTag("autoplay_switch")
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onUpdateSettings(editedAppName, editedTagline, editedContact, editedPasscode, editedAppIconUrl, editedAutoPlay)
                            settingsSavedAlert = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_settings_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("సెట్టింగ్స్ సేవ్ చేయి (Save All Settings)", fontWeight = FontWeight.Bold)
                    }

                    if (settingsSavedAlert) {
                        Text("యాప్ సెట్టింగ్స్ మరియు ఆటో-ప్లే విజయవంతంగా అప్‌డేట్ చేయబడ్డాయి! ✅", color = WhatsAppGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // FCM Push Notifications Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔔", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ఫైర్‌బేస్ క్లౌడ్ మెసేజింగ్ (FCM)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "బ్రేకింగ్ న్యూస్ పుష్ నోటిఫికేషన్‌లు ప్రచురణ",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WhatsAppGreen)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("ACTIVE 🟢", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }

                    Text(
                        text = "సబ్‌స్క్రైబ్ చేసిన టాపిక్: 'breaking_news'\nనోటిఫికేషన్ ఛానల్ ID: breaking_news_channel",
                        color = GoldAccent,
                        fontSize = 12.sp
                    )

                    Button(
                        onClick = {
                            onTestFcmNotification()
                            fcmAlertTriggered = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("🔔 టెస్ట్ బ్రేకింగ్ న్యూస్ పుష్ నోటిఫికేషన్ పంపండి", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    if (fcmAlertTriggered) {
                        Text("✅ బ్రేకింగ్ న్యూస్ పుష్ నోటిఫికేషన్ పంపబడింది!", color = WhatsAppGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
