package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news_cards WHERE status = 'APPROVED' ORDER BY isPinned DESC, timestamp DESC")
    fun getApprovedNewsFeed(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news_cards WHERE status = 'APPROVED' AND category = :category ORDER BY isPinned DESC, timestamp DESC")
    fun getNewsByCategory(category: String): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news_cards WHERE status = 'PENDING' ORDER BY timestamp DESC")
    fun getPendingNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news_cards ORDER BY timestamp DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("SELECT COUNT(*) FROM news_cards")
    suspend fun getNewsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity): Long

    @Update
    suspend fun updateNews(news: NewsEntity)

    @Query("SELECT * FROM news_cards WHERE id = :id")
    suspend fun getNewsById(id: Long): NewsEntity?

    @Query("DELETE FROM news_cards WHERE id = :id")
    suspend fun deleteNewsById(id: Long)

    @Query("UPDATE news_cards SET likesCount = likesCount + 1, isLiked = 1 WHERE id = :id")
    suspend fun likeNews(id: Long)

    @Query("UPDATE news_cards SET isSaved = CASE WHEN isSaved = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun toggleSaveNews(id: Long)

    @Query("UPDATE news_cards SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("UPDATE news_cards SET status = :status WHERE id = :id")
    suspend fun updateNewsStatus(id: Long, status: String)

    // Reporters
    @Query("SELECT * FROM reporters WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveReporters(): Flow<List<ReporterEntity>>

    @Query("SELECT * FROM reporters ORDER BY id DESC")
    fun getAllReporters(): Flow<List<ReporterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReporter(reporter: ReporterEntity): Long

    @Update
    suspend fun updateReporter(reporter: ReporterEntity)

    @Query("DELETE FROM reporters WHERE id = :id")
    suspend fun deleteReporter(id: Long)

    // App Settings
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getAppSettings(): Flow<AppSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAppSettings(settings: AppSettingsEntity)
}
