package com.example.util

import android.content.Context
import android.util.Log
import com.example.data.NewsEntity
import com.example.data.ReporterEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object FirestoreHelper {

    private const val TAG = "FirestoreHelper"
    private const val COLLECTION_NEWS = "news_articles"
    private const val COLLECTION_REPORTERS = "reporter_profiles"

    fun initialize(context: Context) {
        try {
            val appContext = context.applicationContext
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId("1:731604639569:android:com.aistudio.kshanakshanam.news")
                    .setApiKey("AIzaSyB_SampleApiKeyForFCM")
                    .setProjectId("kshanakshanam-news")
                    .setGcmSenderId("731604639569")
                    .build()
                FirebaseApp.initializeApp(appContext, options)
                Log.d(TAG, "FirebaseApp initialized for Firestore successfully.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseApp for Firestore", e)
        }
    }

    private fun getFirestoreInstance(): FirebaseFirestore? {
        return try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving Firestore instance", e)
            null
        }
    }

    /**
     * Stores or updates a news article in Firestore ("news_articles" collection)
     */
    fun saveNewsArticle(news: NewsEntity) {
        try {
            val db = getFirestoreInstance() ?: return
            val docId = news.id.toString()
            val articleMap = mapOf(
                "id" to news.id,
                "headline" to news.headline,
                "content" to news.content,
                "mediaUrl" to news.mediaUrl,
                "mediaType" to news.mediaType,
                "reporterName" to news.reporterName,
                "category" to news.category,
                "district" to news.district,
                "timestamp" to news.timestamp,
                "status" to news.status,
                "likesCount" to news.likesCount,
                "viewsCount" to news.viewsCount,
                "isPinned" to news.isPinned
            )

            db.collection(COLLECTION_NEWS)
                .document(docId)
                .set(articleMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "News article successfully saved to Firestore: $docId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error saving news article to Firestore", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception saving news article to Firestore", e)
        }
    }

    /**
     * Deletes a news article from Firestore
     */
    fun deleteNewsArticle(id: Long) {
        try {
            val db = getFirestoreInstance() ?: return
            db.collection(COLLECTION_NEWS)
                .document(id.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "News article deleted from Firestore: $id")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error deleting news article from Firestore", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception deleting news article from Firestore", e)
        }
    }

    /**
     * Stores or updates a reporter profile in Firestore ("reporter_profiles" collection)
     */
    fun saveReporterProfile(reporter: ReporterEntity) {
        try {
            val db = getFirestoreInstance() ?: return
            val docId = reporter.id.toString()
            val reporterMap = mapOf(
                "id" to reporter.id,
                "name" to reporter.name,
                "district" to reporter.district,
                "role" to reporter.role,
                "isActive" to reporter.isActive,
                "isVerified" to reporter.isVerified,
                "bio" to reporter.bio
            )

            db.collection(COLLECTION_REPORTERS)
                .document(docId)
                .set(reporterMap, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Reporter profile successfully saved to Firestore: $docId")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error saving reporter profile to Firestore", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception saving reporter profile to Firestore", e)
        }
    }

    /**
     * Deletes a reporter profile from Firestore
     */
    fun deleteReporterProfile(id: Long) {
        try {
            val db = getFirestoreInstance() ?: return
            db.collection(COLLECTION_REPORTERS)
                .document(id.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Reporter profile deleted from Firestore: $id")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error deleting reporter profile from Firestore", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception deleting reporter profile from Firestore", e)
        }
    }

    /**
     * Queries news articles from Firestore by specific category (e.g. 'Politics'/'రాజకీయాలు', 'Sports'/'క్రీడలు', 'Technology'/'విద్యా / టెక్నాలజీ')
     */
    fun queryNewsByCategory(category: String, onArticlesUpdated: (List<NewsEntity>) -> Unit) {
        try {
            val db = getFirestoreInstance() ?: return
            val query = if (category == "అన్ని" || category.isBlank()) {
                db.collection(COLLECTION_NEWS)
            } else {
                db.collection(COLLECTION_NEWS).whereEqualTo("category", category)
            }

            query.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot != null) {
                        val articles = snapshot.documents.mapNotNull { doc ->
                            try {
                                NewsEntity(
                                    id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L,
                                    headline = doc.getString("headline") ?: "",
                                    content = doc.getString("content") ?: "",
                                    mediaUrl = doc.getString("mediaUrl") ?: "",
                                    mediaType = doc.getString("mediaType") ?: "IMAGE",
                                    reporterName = doc.getString("reporterName") ?: "",
                                    category = doc.getString("category") ?: "అన్ని",
                                    district = doc.getString("district") ?: "హైదరాబాద్",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    status = doc.getString("status") ?: "APPROVED",
                                    likesCount = (doc.getLong("likesCount") ?: 0L).toInt(),
                                    viewsCount = (doc.getLong("viewsCount") ?: 1L).toInt(),
                                    isPinned = doc.getBoolean("isPinned") ?: false
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onArticlesUpdated(articles)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error querying news by category: $category", e)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception querying news by category: $category", e)
        }
    }

    /**
     * Listens for real-time changes to news_articles collection in Firestore
     */
    fun listenToNewsArticles(onArticlesUpdated: (List<NewsEntity>) -> Unit) {
        try {
            val db = getFirestoreInstance() ?: return
            db.collection(COLLECTION_NEWS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Firestore news articles listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val articles = snapshot.documents.mapNotNull { doc ->
                            try {
                                NewsEntity(
                                    id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L,
                                    headline = doc.getString("headline") ?: "",
                                    content = doc.getString("content") ?: "",
                                    mediaUrl = doc.getString("mediaUrl") ?: "",
                                    mediaType = doc.getString("mediaType") ?: "IMAGE",
                                    reporterName = doc.getString("reporterName") ?: "",
                                    category = doc.getString("category") ?: "అన్ని",
                                    district = doc.getString("district") ?: "హైదరాబాద్",
                                    timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis(),
                                    status = doc.getString("status") ?: "APPROVED",
                                    likesCount = (doc.getLong("likesCount") ?: 0L).toInt(),
                                    viewsCount = (doc.getLong("viewsCount") ?: 1L).toInt(),
                                    isPinned = doc.getBoolean("isPinned") ?: false
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onArticlesUpdated(articles)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception setting up news articles listener", e)
        }
    }

    /**
     * Listens for real-time changes to reporter_profiles collection in Firestore
     */
    fun listenToReporterProfiles(onReportersUpdated: (List<ReporterEntity>) -> Unit) {
        try {
            val db = getFirestoreInstance() ?: return
            db.collection(COLLECTION_REPORTERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Firestore reporter profiles listener error", error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val reporters = snapshot.documents.mapNotNull { doc ->
                            try {
                                ReporterEntity(
                                    id = doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L,
                                    name = doc.getString("name") ?: "",
                                    district = doc.getString("district") ?: "",
                                    role = doc.getString("role") ?: "రిపోర్టర్",
                                    isActive = doc.getBoolean("isActive") ?: true,
                                    isVerified = doc.getBoolean("isVerified") ?: true,
                                    bio = doc.getString("bio") ?: ""
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        onReportersUpdated(reporters)
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Exception setting up reporter profiles listener", e)
        }
    }
}
