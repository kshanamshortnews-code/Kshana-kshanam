package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Badge
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.NavigationMode
import com.example.ui.theme.CrimsonDark
import com.example.ui.theme.CrimsonPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NewsSurfaceDark

@Composable
fun TopNewsBar(
    appName: String,
    tagline: String,
    appIconUrl: String = "",
    currentMode: NavigationMode,
    selectedCategory: String,
    categories: List<String>,
    pendingCount: Int,
    onModeSelected: (NavigationMode) -> Unit,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NewsSurfaceDark,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App Branding Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onModeSelected(NavigationMode.FEED) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Custom App Logo or Live Flash Icon
                        if (appIconUrl.isNotBlank()) {
                            AsyncImage(
                                model = appIconUrl,
                                contentDescription = "App Icon",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(LiveRed, CrimsonPrimary))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Live Flash",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = appName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = Color.White
                            ),
                            modifier = Modifier.testTag("app_title")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        // Live Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(LiveRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "లైవ్",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GoldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(start = 36.dp)
                    )
                }

                // Mode Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Feed Button
                    IconButton(
                        onClick = { onModeSelected(NavigationMode.FEED) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (currentMode == NavigationMode.FEED) CrimsonPrimary else Color.Transparent)
                            .testTag("nav_feed_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Newspaper,
                            contentDescription = "వార్తల ఫీడ్",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reporter Panel Button
                    IconButton(
                        onClick = { onModeSelected(NavigationMode.REPORTER_PANEL) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (currentMode == NavigationMode.REPORTER_PANEL) CrimsonPrimary else Color.Transparent)
                            .testTag("nav_reporter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "రిపోర్టర్ పానల్",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Admin Dashboard Button
                    Box {
                        IconButton(
                            onClick = { onModeSelected(NavigationMode.ADMIN_DASHBOARD) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (currentMode == NavigationMode.ADMIN_DASHBOARD) CrimsonPrimary else Color.Transparent)
                                .testTag("nav_admin_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "అడ్మిన్ డాష్‌బోర్డ్",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (pendingCount > 0) {
                            Badge(
                                modifier = Modifier.align(Alignment.TopEnd),
                                containerColor = LiveRed,
                                contentColor = Color.White
                            ) {
                                Text(text = "$pendingCount", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Categories Row (Only when in Feed Mode)
            AnimatedVisibility(visible = currentMode == NavigationMode.FEED) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("category_filter_bar"),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == selectedCategory
                        val categoryIcon = when (category) {
                            "రాజకీయాలు" -> "🏛️"
                            "క్రీడలు" -> "⚽"
                            "విద్యా / టెక్నాలజీ" -> "💻"
                            "సినిమా" -> "🎬"
                            "తాజా వార్తలు" -> "🔥"
                            "క్రైమ్" -> "🚨"
                            "భక్తి/ఆధ్యాత్మికం" -> "🛕"
                            else -> "📰"
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { onCategorySelected(category) },
                            label = {
                                Text(
                                    text = "$categoryIcon $category",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CrimsonPrimary,
                                containerColor = Color(0xFF2A2A36)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = null,
                            modifier = Modifier.testTag("category_chip_$category")
                        )
                    }
                }
            }
        }
    }
}
