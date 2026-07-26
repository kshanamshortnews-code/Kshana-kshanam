package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_cards")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val headline: String,
    val content: String,
    val mediaUrl: String,
    val mediaType: String = "IMAGE", // "IMAGE" or "VIDEO"
    val reporterName: String,
    val category: String,
    val district: String = "హైదరాబాద్",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "APPROVED", // "APPROVED", "PENDING", "REJECTED"
    val likesCount: Int = 0,
    val viewsCount: Int = 1,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isPinned: Boolean = false
)
