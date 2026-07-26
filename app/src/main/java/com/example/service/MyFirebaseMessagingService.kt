package com.example.service

import android.util.Log
import com.example.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM Device Registration Token: $token")
        // Automatically subscribe device to breaking news topic
        NotificationHelper.subscribeToBreakingNewsTopic(applicationContext)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message Received from: ${remoteMessage.from}")

        var title = "🔴 ಕ್ಷಣ ಕ್ಷಣం తాజా బ్రేకింగ్ వార్త"
        var content = ""
        var newsId = System.currentTimeMillis()

        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "FCM Data Payload: ${remoteMessage.data}")
            title = remoteMessage.data["headline"]
                ?: remoteMessage.data["title"]
                ?: title
            content = remoteMessage.data["content"]
                ?: remoteMessage.data["body"]
                ?: content
            newsId = remoteMessage.data["news_id"]?.toLongOrNull() ?: newsId
        }

        // Check if message contains notification payload
        remoteMessage.notification?.let { notification ->
            if (notification.title != null) title = notification.title!!
            if (notification.body != null) content = notification.body!!
        }

        if (content.isNotBlank() || title.isNotBlank()) {
            NotificationHelper.showBreakingNewsNotification(
                context = applicationContext,
                title = title,
                content = content,
                newsId = newsId
            )
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
