package com.example.data

import com.example.util.FirestoreHelper
import kotlinx.coroutines.flow.Flow

class NewsRepository(private val newsDao: NewsDao) {

    val approvedNews: Flow<List<NewsEntity>> = newsDao.getApprovedNewsFeed()
    val pendingNews: Flow<List<NewsEntity>> = newsDao.getPendingNews()
    val allNews: Flow<List<NewsEntity>> = newsDao.getAllNews()
    val activeReporters: Flow<List<ReporterEntity>> = newsDao.getActiveReporters()
    val allReporters: Flow<List<ReporterEntity>> = newsDao.getAllReporters()
    val appSettings: Flow<AppSettingsEntity?> = newsDao.getAppSettings()

    fun getNewsByCategory(category: String): Flow<List<NewsEntity>> {
        return newsDao.getNewsByCategory(category)
    }

    suspend fun getNewsCount(): Int {
        return newsDao.getNewsCount()
    }

    suspend fun submitNews(news: NewsEntity): Long {
        val insertedId = newsDao.insertNews(news)
        val newsWithId = if (news.id == 0L) news.copy(id = insertedId) else news
        FirestoreHelper.saveNewsArticle(newsWithId)
        return insertedId
    }

    suspend fun updateNews(news: NewsEntity) {
        newsDao.updateNews(news)
        FirestoreHelper.saveNewsArticle(news)
    }

    suspend fun deleteNews(id: Long) {
        newsDao.deleteNewsById(id)
        FirestoreHelper.deleteNewsArticle(id)
    }

    suspend fun updateNewsStatus(id: Long, status: String) {
        newsDao.updateNewsStatus(id, status)
        val updatedNews = newsDao.getNewsById(id)
        if (updatedNews != null) {
            FirestoreHelper.saveNewsArticle(updatedNews)
        }
    }

    suspend fun likeNews(id: Long) {
        newsDao.likeNews(id)
        val updatedNews = newsDao.getNewsById(id)
        if (updatedNews != null) {
            FirestoreHelper.saveNewsArticle(updatedNews)
        }
    }

    suspend fun toggleSaveNews(id: Long) {
        newsDao.toggleSaveNews(id)
        val updatedNews = newsDao.getNewsById(id)
        if (updatedNews != null) {
            FirestoreHelper.saveNewsArticle(updatedNews)
        }
    }

    suspend fun incrementViews(id: Long) {
        newsDao.incrementViews(id)
        val updatedNews = newsDao.getNewsById(id)
        if (updatedNews != null) {
            FirestoreHelper.saveNewsArticle(updatedNews)
        }
    }

    suspend fun addReporter(reporter: ReporterEntity): Long {
        val insertedId = newsDao.insertReporter(reporter)
        val reporterWithId = if (reporter.id == 0L) reporter.copy(id = insertedId) else reporter
        FirestoreHelper.saveReporterProfile(reporterWithId)
        return insertedId
    }

    suspend fun updateReporter(reporter: ReporterEntity) {
        newsDao.updateReporter(reporter)
        FirestoreHelper.saveReporterProfile(reporter)
    }

    suspend fun deleteReporter(id: Long) {
        newsDao.deleteReporter(id)
        FirestoreHelper.deleteReporterProfile(id)
    }

    suspend fun updateSettings(settings: AppSettingsEntity) {
        newsDao.updateAppSettings(settings)
    }
}
