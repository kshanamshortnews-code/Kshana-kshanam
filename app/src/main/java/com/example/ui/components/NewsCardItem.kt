package com.example.ui.components

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.NewsEntity
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NewsSurfaceDark
import com.example.ui.theme.WhatsAppGreen
import com.example.util.ShareHelper
import com.example.util.TextToSpeechHelper

@Composable
fun NewsCardItem(
    news: NewsEntity,
    appName: String,
    tagline: String,
    ttsHelper: TextToSpeechHelper,
    isAutoPlayEnabled: Boolean = false,
    onLikeClick: (Long) -> Unit,
    onSaveClick: (Long) -> Unit,
    onViewIncrement: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isExpanded by remember { mutableStateOf(false) }
    var isVideoPlaying by remember(isAutoPlayEnabled) { mutableStateOf(isAutoPlayEnabled) }
    var showReporterBioDialog by remember { mutableStateOf(false) }

    // Double-tap to like states & animated values
    var showDoubleTapHeart by remember { mutableStateOf(false) }
    var heartAnimationTrigger by remember { mutableIntStateOf(0) }
    val heartScale = remember { Animatable(0f) }
    val heartAlpha = remember { Animatable(0f) }

    LaunchedEffect(heartAnimationTrigger) {
        if (heartAnimationTrigger > 0) {
            showDoubleTapHeart = true
            heartScale.snapTo(0.2f)
            heartAlpha.snapTo(1f)

            // Bouncy spring zoom up & relax
            launch {
                heartScale.animateTo(
                    targetValue = 1.35f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                heartScale.animateTo(
                    targetValue = 1.0f,
                    animationSpec = tween(durationMillis = 150)
                )
            }

            delay(400)
            heartAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250)
            )
            showDoubleTapHeart = false
        }
    }

    val formattedTime = remember(news.timestamp) {
        DateUtils.getRelativeTimeSpanString(
            news.timestamp,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS
        ).toString()
    }

    val imageRequest = remember(context, news.mediaUrl) {
        ImageRequest.Builder(context)
            .data(news.mediaUrl)
            .crossfade(true)
            .build()
    }

    val overlayGradient = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.Black.copy(alpha = 0.5f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.7f)
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .testTag("news_card_${news.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NewsSurfaceDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 9:16 / FULL FRAME TOP MEDIA SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Color(0xFF15151C))
                    .pointerInput(news.id) {
                        detectTapGestures(
                            onDoubleTap = {
                                onLikeClick(news.id)
                                heartAnimationTrigger++
                            }
                        )
                    }
                    .testTag("media_box_${news.id}")
            ) {
                // Media Image / Video Placeholder
                AsyncImage(
                    model = imageRequest,
                    contentDescription = news.headline,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay Gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(overlayGradient)
                )

                // Double-Tap Animated Heart Overlay
                if (showDoubleTapHeart) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = heartScale.value
                                scaleY = heartScale.value
                                alpha = heartAlpha.value
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                // Background glow circle
                                Box(
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(CircleShape)
                                        .background(LiveRed.copy(alpha = 0.45f))
                                )
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Double tap heart like",
                                    tint = LiveRed,
                                    modifier = Modifier.size(86.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.85f))
                                    .border(1.dp, LiveRed, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "❤️ లైక్ చేయబడింది +1",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Category Chip (Top Left)
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CrimsonPrimary)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = news.category,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // District Location Chip (Top Right)
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "District",
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = news.district,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Video Indicator / Play Button (If Video Type)
                if (news.mediaType == "VIDEO") {
                    val pulseScale = if (isVideoPlaying) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.12f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulseScale"
                        )
                        scale
                    } else 1f

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .scale(pulseScale)
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isVideoPlaying) LiveRed else Color.Black.copy(alpha = 0.75f))
                            .border(2.dp, GoldAccent, CircleShape)
                            .clickable { isVideoPlaying = !isVideoPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Video Label Badge Bottom Left
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.BottomStart)
                            .clip(RoundedCornerShape(6.dp))
                            .background(LiveRed)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isVideoPlaying) "▶ వీడియో ప్లే అవుతోంది" else if (!isAutoPlayEnabled) "💾 డేటా సేవర్ - ప్లే క్లిక్ చేయండి" else "🎥 16:9 వీడియో వార్త",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Pinned Indicator Badge
                if (news.isPinned) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 12.dp, end = 12.dp)
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldAccent)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "📌 ముఖ్యమైన వార్త",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // 2. CONTENT & ACTIONS ROW (Bottom half of 9:16 full-frame card)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(12.dp)
            ) {
                // Left Column: News Details (Unicode Telugu)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Headline (ముఖ్యాంశం)
                        Text(
                            text = news.headline,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                lineHeight = 24.sp,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Content Summary (వివరాలు)
                        Text(
                            text = news.content,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = Color(0xFFD0D0E0)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Reporter Badge (Interactive with Verification Icon) & Timestamp
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Reporter Verified Badge Chip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF222232))
                                .border(1.dp, CrimsonPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable { showReporterBioDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("reporter_badge_${news.id}")
                        ) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "రిపోర్టర్ ప్రొఫైల్",
                                    tint = CrimsonPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = news.reporterName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            // Verification status blue checkmark badge
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified Reporter",
                                tint = Color(0xFF1DA1F2),
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• $formattedTime",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Right Column: Vertical Reel / TikTok Style Action Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Like Button
                    val heartColor by animateColorAsState(
                        targetValue = if (news.isLiked) LiveRed else Color.LightGray,
                        label = "heartColor"
                    )
                    IconButton(
                        onClick = { onLikeClick(news.id) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF242432))
                            .testTag("like_button_${news.id}")
                    ) {
                        Icon(
                            imageVector = if (news.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "లైక్ చేయండి",
                            tint = heartColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "${news.likesCount}",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Share Button (Right under Like)
                    IconButton(
                        onClick = {
                            ShareHelper.shareToWhatsApp(context, news, appName, tagline)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(WhatsAppGreen)
                            .testTag("share_button_${news.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "వాట్సాప్‌లో షేర్ చేయండి",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "షేర్",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Bookmark / Save Button
                    IconButton(
                        onClick = { onSaveClick(news.id) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF242432))
                            .testTag("save_button_${news.id}")
                    ) {
                        Icon(
                            imageVector = if (news.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "సేవ్ చేయండి",
                            tint = if (news.isSaved) GoldAccent else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Audio ReadAloud TTS Speaker Button ("వినండి" / Listen)
                    val isSpeakingThisCard = ttsHelper.isSpeaking.value && ttsHelper.currentSpeakingNewsId.value == news.id
                    IconButton(
                        onClick = {
                            ttsHelper.speak(news.id, news.headline, news.content)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSpeakingThisCard) LiveRed else Color(0xFF242432))
                            .testTag("tts_button_${news.id}")
                    ) {
                        Icon(
                            imageVector = if (isSpeakingThisCard) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                            contentDescription = "వార్త చదవండి (Listen)",
                            tint = if (isSpeakingThisCard) GoldAccent else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = if (isSpeakingThisCard) "ఆపు" else "వినండి",
                        color = if (isSpeakingThisCard) LiveRed else Color.LightGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // REPORTER BIO MODAL DIALOG
    if (showReporterBioDialog) {
        AlertDialog(
            onDismissRequest = { showReporterBioDialog = false },
            containerColor = NewsSurfaceDark,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray,
            icon = {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(CrimsonPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Reporter Avatar",
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = news.reporterName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Status",
                            tint = Color(0xFF1DA1F2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAccent.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "✓ అధికారిక గుర్తింపు పొందిన రిపోర్టర్",
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Stats & District Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF181822))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("జిల్లా (District)", color = Color.Gray, fontSize = 11.sp)
                            Text(news.district, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("హోదా (Role)", color = Color.Gray, fontSize = 11.sp)
                            Text("జర్నలిస్ట్", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("పరిస్థితి (Status)", color = Color.Gray, fontSize = 11.sp)
                            Text("యాక్టివ్ (Active)", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Brief Reporter Bio
                    Text(
                        text = "📝 రిపోర్టర్ పరిచయం (Reporter Bio):",
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${news.reporterName} గారు ${news.district} ప్రాంతంలో సుదీర్ఘ అనుభవం గల సీనియర్ జర్నలిస్ట్. నిజాయితీతో కూడిన క్షేత్రస్థాయి విశ్లేషణ, తాజా ముఖ్యాంశాలను క్షణ క్షణం నిష్పాక్షికంగా అందిస్తారు.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReporterBioDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_reporter_bio_dialog")
                ) {
                    Text(
                        text = "ముగించు (Close)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}
