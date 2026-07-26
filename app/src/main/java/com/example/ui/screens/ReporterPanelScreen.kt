package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ReporterEntity
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NewsBackgroundDark
import com.example.ui.theme.NewsSurfaceDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReporterPanelScreen(
    reporters: List<ReporterEntity>,
    categories: List<String>,
    onSubmitNews: (headline: String, content: String, mediaUrl: String, mediaType: String, reporterName: String, category: String, district: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var headline by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mediaUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1585829365295-ab7cd400c167?auto=format&fit=crop&w=1080&q=80") }
    var mediaType by remember { mutableStateOf("IMAGE") } // IMAGE or VIDEO

    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            mediaUrl = it.toString()
        }
    }
    var selectedReporterName by remember { mutableStateOf(reporters.firstOrNull()?.name ?: "వి. ప్రకాష్ రెడ్డి") }
    var selectedCategory by remember { mutableStateOf(categories.getOrElse(1) { "తాజా వార్తలు" }) }
    var district by remember { mutableStateOf(reporters.firstOrNull()?.district ?: "హైదరాబాద్") }
    var submissionSuccess by remember { mutableStateOf(false) }

    // Preset 16:9 News Image Choices
    val sampleMediaUrls = listOf(
        "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=1080&q=80",
        "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1080&q=80"
    )

    var reporterDropdownExpanded by remember { mutableStateOf(false) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Sample preset news topics for 1-tap generation without typing on keyboard
    data class PresetNews(
        val category: String,
        val headline: String,
        val content: String,
        val mediaUrl: String,
        val icon: String
    )

    val quickPresets = listOf(
        PresetNews(
            category = "తాజా వార్తలు",
            headline = "ప్రాంతీయ అభివృద్ధి పనులకు శంకుస్థాపన చేసిన స్థానిక ప్రజాప్రతినిధులు",
            content = "ప్రాంతంలో మౌలిక సదుపాయాల మెరుగుదలకు ప్రతిపాదించిన నూతన రహదారి మరియు తాగునీటి ప్రాజెక్ట్ పనులు వేగంగా ప్రారంభమయ్యాయి. ప్రజలకు మెరుగైన వసతులు కల్పించడమే తమ లక్ష్యమని అధికారులు స్పష్టం చేశారు.",
            mediaUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&w=1080&q=80",
            icon = "🏛️"
        ),
        PresetNews(
            category = "జిల్లా వార్తలు",
            headline = "రాబోయే 24 గంటల్లో మోస్తరు నుండి భారీ వర్షాలు - వాతావరణ శాఖ హెచ్చరిక",
            content = "అల్పపీడన ప్రభావంతో జిల్లా వ్యాప్తంగా మోస్తరు నుండి భారీ వర్షాలు కురిసే అవకాశం ఉందని అధికారులు తెలిపారు. రైతులు, లోతట్టు ప్రాంతాల ప్రజలు తగిన జాగ్రత్తలు తీసుకోవాలని సూచించారు.",
            mediaUrl = "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?auto=format&fit=crop&w=1080&q=80",
            icon = "🌧️"
        ),
        PresetNews(
            category = "రాజకీయాలు",
            headline = "ప్రభుత్వ సంక్షేమ పథకాల లబ్ధిదారుల ఎంపిక ప్రక్రియ వేగవంతం",
            content = "అర్హులైన ప్రతి ఒక్కరికీ సంక్షేమ పథకాలు అందించేందుకు సచివాలయాల ద్వారా సర్వే నిర్వహించి వివరాలు సేకరిస్తున్నారు. అర్హులకు పూర్తి న్యాయం జరుగుతుందని ప్రకటించారు.",
            mediaUrl = "https://images.unsplash.com/photo-1500937386664-56d1dfef3854?auto=format&fit=crop&w=1080&q=80",
            icon = "📜"
        ),
        PresetNews(
            category = "క్రీడలు",
            headline = "జిల్లా స్థాయి క్రికెట్ టోర్నమెంట్‌లో ఘన విజయం సాధించిన విజేతలు",
            content = "ఉత్కంఠభరితంగా జరిగిన ఫైనల్ పోరులో యంగ్ స్టార్స్ జట్టు అత్యుత్తమ ప్రదర్శన కనబరిచి ట్రోఫీని కైవసం చేసుకుంది. విజేతలకు ప్రముఖుల చేతుల మీదుగా బహుమతులు అందజేశారు.",
            mediaUrl = "https://images.unsplash.com/photo-1531415074968-036ba1b575da?auto=format&fit=crop&w=1080&q=80",
            icon = "🏆"
        ),
        PresetNews(
            category = "సినిమా",
            headline = "భారీ అంచనాల నూతన చిత్రానికి సంబంధించిన ముఖ్యాంశాలు విడుదల",
            content = "ప్రముఖ నటుల కాంబినేషన్‌లో వస్తున్న నూతన సినిమా ఫస్ట్ లుక్ సోషల్ మీడియాలో విశేష ఆదరణ పొందుతోంది. టీజర్ త్వరలోనే విడుదల చేయనున్నట్లు ప్రకటించారు.",
            mediaUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1080&q=80",
            icon = "🎬"
        )
    )

    val headlineQuickChips = listOf(
        "నగరంలో ట్రాఫిక్ మళ్లింపు హెచ్చరిక",
        "నూతన డ్రైనేజీ పనుల ప్రారంభం",
        "రైతులకు ఉచిత ఎరువుల పంపిణీ",
        "స్థానిక పాఠశాలలో విద్యార్థులకు సత్కారం",
        "జిల్లా వైద్యాధికారుల ఆకస్మిక తనిఖీ"
    )

    val contentQuickPhrases = listOf(
        "స్థానిక అధికారులు ఘటనా స్థలానికి చేరుకుని వివరాలు సేకరించారు.",
        "ప్రజలు మరియు స్థానికులు హర్షం వ్యక్తం చేశారు.",
        "బాధితులకు తక్షణ పరిహారం అందించనున్నట్లు హామీ ఇచ్చారు.",
        "వేగవంతమైన పరిష్కారం చూపుతామని స్పష్టం చేశారు."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NewsBackgroundDark)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Banner Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CrimsonPrimary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Reporter Panel",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "రిపోర్టర్ వార్తా వేదిక",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "క్షణ క్షణం వార్తలను తెలుగు యూనికోడ్‌లో నమోదు చేసి అడ్మిన్‌కు పంపండి",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldAccent
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Success Alert Box
        if (submissionSuccess) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = Color(0xFF1B5E20),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = GoldAccent,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "వార్త విజయవంతంగా అడ్మిన్ పరిశీలనకు పంపబడింది!",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 1-TAP NEWS GENERATOR SECTION (No Keyboard Typing Needed)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ 1-టాప్‌తో వార్త తయారు చేయండి (కీబోర్డ్ అవసరం లేదు):",
                        color = GoldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(
                        onClick = {
                            headline = ""
                            content = ""
                        }
                    ) {
                        Text("🗑️ క్లియర్ చేయి", color = Color.LightGray, fontSize = 11.sp)
                    }
                }

                Text(
                    text = "కింది ఏదైనా సిద్ధంగా ఉన్న వార్తపై క్లిక్ చేయండి - ఆటోమాటిక్‌గా వివరాలు నింపబడతాయి:",
                    color = Color.Gray,
                    fontSize = 11.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickPresets) { preset ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2A2A38))
                                .border(1.dp, CrimsonPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable {
                                    headline = preset.headline
                                    content = preset.content
                                    selectedCategory = preset.category
                                    mediaUrl = preset.mediaUrl
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Column {
                                Text(
                                    text = "${preset.icon} ${preset.category}",
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = preset.headline,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Reporter Dropdown Selector
                Text(
                    text = "✍️ రిపోర్టర్ ఎంచుకోండి:",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = reporterDropdownExpanded,
                    onExpandedChange = { reporterDropdownExpanded = !reporterDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedReporterName,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reporterDropdownExpanded) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CrimsonPrimary) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("reporter_select_dropdown"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = reporterDropdownExpanded,
                        onDismissRequest = { reporterDropdownExpanded = false }
                    ) {
                        reporters.forEach { reporter ->
                            DropdownMenuItem(
                                text = { Text("${reporter.name} (${reporter.district})") },
                                onClick = {
                                    selectedReporterName = reporter.name
                                    district = reporter.district
                                    reporterDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Headline Input (తెలుగు ముఖ్యాంశం)
                Text(
                    text = "📰 వార్త శీర్షిక (తెలుగు యూనికోడ్‌లో):",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    placeholder = { Text("ఉదా: హైదరాబాద్‌లో నూతన మెట్రో మార్గం ప్రారంభం", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reporter_headline_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CrimsonPrimary
                    ),
                    maxLines = 3
                )

                // Quick Headline Chips (Tap to add)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(headlineQuickChips) { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF333344))
                                .clickable {
                                    headline = if (headline.isBlank()) chip else "$headline - $chip"
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+ $chip", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }

                // Category & District Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("వర్గం (Category)", color = Color.LightGray) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier.menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            categories.filter { it != "అన్ని" }.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // District Input
                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("జిల్లా/ప్రాంతం", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = CrimsonPrimary) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                // Full Content Input (వార్త వివరాలు)
                Text(
                    text = "📝 పూర్తి వార్త వివరాలు (Unicode Details):",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    placeholder = { Text("వార్త వివరాలను ఇక్కడ రాయండి...", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .testTag("reporter_content_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CrimsonPrimary
                    ),
                    maxLines = 8
                )

                // Quick Content Sentence Chips (Tap to add)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(contentQuickPhrases) { phrase ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF333344))
                                .clickable {
                                    content = if (content.isBlank()) phrase else "$content $phrase"
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("+ $phrase", color = Color.LightGray, fontSize = 10.sp)
                        }
                    }
                }

                // Media Type Selector (16:9 Image vs Video)
                Text(
                    text = "📷 మీడియా రకం (Media Type):",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = mediaType == "IMAGE",
                        onClick = { mediaType = "IMAGE" },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ఫోటో (Image)")
                        }
                    }

                    SegmentedButton(
                        selected = mediaType == "VIDEO",
                        onClick = { mediaType = "VIDEO" },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("వీడియో (Video)")
                        }
                    }
                }

                // Direct Media Upload Button from Device Gallery
                Button(
                    onClick = {
                        val mime = if (mediaType == "VIDEO") "video/*" else "image/*"
                        mediaPickerLauncher.launch(mime)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("upload_media_file_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0288D1),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (mediaType == "VIDEO") "📹 డివైజ్ / గ్యాలరీ నుండి వీడియో ఎంచుకోండి" else "📷 డివైజ్ / గ్యాలరీ నుండి ఫోటో ఎంచుకోండి",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                if (mediaUrl.startsWith("content://")) {
                    Surface(
                        color = Color(0xFF1B5E20),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "డివైజ్ నుండి మీడియా ఫైల్ ఎంచుకోబడింది! ✅",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Optional URL Input or Preset Selection
                OutlinedTextField(
                    value = mediaUrl,
                    onValueChange = { mediaUrl = it },
                    label = { Text("లేదా ఫోటో/వీడియో లింక్ URL పేస్ట్ చేయండి", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Text(
                    text = "లేదా కింది 16:9 శాంపిల్ ఫోటో ఎంచుకోండి:",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sampleMediaUrls) { url ->
                        val isSelected = url == mediaUrl
                        Box(
                            modifier = Modifier
                                .size(width = 80.dp, height = 45.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) GoldAccent else Color.Gray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { mediaUrl = url }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(url).build(),
                                contentDescription = "Sample Media",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // 16:9 Live Preview Frame
                Text(
                    text = "🖼️ 16:9 మీడియా ప్రివ్యూ (16:9 Preview):",
                    color = GoldAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(mediaUrl).build(),
                        contentDescription = "Media Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (headline.isNotBlank() && content.isNotBlank()) {
                            onSubmitNews(
                                headline,
                                content,
                                mediaUrl,
                                mediaType,
                                selectedReporterName,
                                selectedCategory,
                                district
                            )
                            headline = ""
                            content = ""
                            submissionSuccess = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_news_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CrimsonPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = headline.isNotBlank() && content.isNotBlank()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Submit",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "అడ్మిన్ పరిశీలనకు పంపు (Submit to Admin)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}
