package com.example.ui.screens

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NewsEntity
import com.example.ui.components.NewsCardItem
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NewsBackgroundDark
import com.example.util.TextToSpeechHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFeedScreen(
    newsList: List<NewsEntity>,
    appName: String,
    tagline: String,
    ttsHelper: TextToSpeechHelper,
    isAutoPlayEnabled: Boolean = false,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onLikeClick: (Long) -> Unit,
    onSaveClick: (Long) -> Unit,
    onViewIncrement: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showOnlySaved by remember { mutableStateOf(false) }

    val filteredNews = remember(newsList, searchQuery, showOnlySaved) {
        newsList.filter { news ->
            val matchesSaved = if (showOnlySaved) news.isSaved else true
            val matchesSearch = if (searchQuery.isBlank()) true else {
                news.headline.contains(searchQuery, ignoreCase = true) ||
                        news.content.contains(searchQuery, ignoreCase = true) ||
                        news.reporterName.contains(searchQuery, ignoreCase = true) ||
                        news.district.contains(searchQuery, ignoreCase = true)
            }
            matchesSaved && matchesSearch
        }
    }

    val listState = rememberLazyListState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NewsBackgroundDark)
    ) {
        // Search & Filter Header Sub-bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Input Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("వార్తలు సెర్చ్ చేయండి...", fontSize = 13.sp, color = Color.Gray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CrimsonPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search",
                                tint = Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("news_search_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CrimsonPrimary,
                    unfocusedBorderColor = Color(0xFF2C2C3A),
                    focusedContainerColor = Color(0xFF1E1E28),
                    unfocusedContainerColor = Color(0xFF181822),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Saved Toggle Chip
            FilterChip(
                selected = showOnlySaved,
                onClick = { showOnlySaved = !showOnlySaved },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Saved",
                            tint = if (showOnlySaved) GoldAccent else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "సేవ్డ్",
                            fontSize = 12.sp,
                            color = if (showOnlySaved) Color.White else Color.Gray
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CrimsonPrimary,
                    containerColor = Color(0xFF181822)
                ),
                shape = RoundedCornerShape(20.dp),
                border = null
            )
        }

        // News Feed List (16:9 Vertical Slide Up & Down Cards with Pull-To-Refresh)
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (filteredNews.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Newspaper,
                            contentDescription = "No News",
                            tint = CrimsonPrimary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (showOnlySaved) "మీరు సేవ్ చేసిన వార్తలేవీ లేవు" else "ఈ కేటగిరీలో వార్తలేవీ లభ్యం కాలేదు",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "రిపోర్టర్ పానల్ ద్వారా లేదా అడ్మిన్ డాష్‌బోర్డ్ నుండి కొత్త వార్తలను జోడించండి.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.LightGray,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            } else {
                val pagerState = rememberPagerState(pageCount = { filteredNews.size })

                androidx.compose.runtime.LaunchedEffect(pagerState.currentPage, filteredNews) {
                    if (filteredNews.isNotEmpty() && pagerState.currentPage in filteredNews.indices) {
                        onViewIncrement(filteredNews[pagerState.currentPage].id)
                    }
                }

                VerticalPager(
                    state = pagerState,
                    key = { page -> filteredNews[page].id },
                    beyondViewportPageCount = 1,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("news_feed_list")
                ) { page ->
                    val newsItem = filteredNews[page]
                    NewsCardItem(
                        news = newsItem,
                        appName = appName,
                        tagline = tagline,
                        ttsHelper = ttsHelper,
                        isAutoPlayEnabled = isAutoPlayEnabled,
                        onLikeClick = onLikeClick,
                        onSaveClick = onSaveClick,
                        onViewIncrement = onViewIncrement,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
