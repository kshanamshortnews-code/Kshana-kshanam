package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.messaging.FirebaseMessaging

object NotificationHelper {

    const val CHANNEL_ID = "breaking_news_channel"
    const val CHANNEL_NAME = "తాజా వార్తల హెచ్చరికలు (Breaking News)"
    const val CHANNEL_DESC = "ముఖ్యాంశాలు మరియు బ్రేకింగ్ న్యూస్ తాజా సమాచారం కోసం నోటిఫికేషన్‌లు"
    private const val TAG = "NotificationHelper"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun initializeFirebase(context: Context) {
        try {
            val appContext = context.applicationContext
            if (com.google.firebase.FirebaseApp.getApps(appContext).isEmpty()) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId("1:731604639569:android:com.aistudio.kshanakshanam.news")
                    .setApiKey("AIzaSyB_SampleApiKeyForFCM")
                    .setProjectId("kshanakshanam-news")
                    .setGcmSenderId("731604639569")
                    .build()
                com.google.firebase.FirebaseApp.initializeApp(appContext, options)
                Log.d(TAG, "FirebaseApp successfully initialized programmatically.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseApp initialization error", e)
        }
    }

    fun subscribeToBreakingNewsTopic(context: Context? = null, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val isAppInitialized = context?.let {
                initializeFirebase(it)
                com.google.firebase.FirebaseApp.getApps(it.applicationContext).isNotEmpty()
            } ?: false

            if (isAppInitialized) {
                FirebaseMessaging.getInstance().subscribeToTopic("breaking_news")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Subscribed successfully to FCM topic: breaking_news")
                            onComplete?.invoke(true)
                        } else {
                            Log.e(TAG, "Failed to subscribe to FCM topic", task.exception)
                            onComplete?.invoke(false)
                        }
                    }
            } else {
                Log.w(TAG, "FirebaseApp is not initialized yet. Skipping topic subscription.")
                onComplete?.invoke(false)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error subscribing to FCM topic", e)
            onComplete?.invoke(false)
        }
    }

    fun showBreakingNewsNotification(
        context: Context,
        title: String,
        content: String,
        newsId: Long = System.currentTimeMillis()
    ) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("NEWS_ID", newsId)
            putExtra("FROM_NOTIFICATION", true)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            newsId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(newsId.toInt(), builder.build())
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing POST_NOTIFICATIONS permission", e)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to display notification", e)
        }
    }
}
