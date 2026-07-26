package com.example.util

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private val mainHandler = Handler(Looper.getMainLooper())

    var isInitialized = mutableStateOf(false)
        private set
    var isSpeaking = mutableStateOf(false)
        private set
    var currentSpeakingNewsId = mutableStateOf<Long?>(null)
        private set

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Try Telugu locale "te", "IN"
            val teLocale = Locale("te", "IN")
            val result = tts?.setLanguage(teLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to default locale
                tts?.language = Locale.getDefault()
            }

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    mainHandler.post {
                        isSpeaking.value = true
                    }
                }

                override fun onDone(utteranceId: String?) {
                    mainHandler.post {
                        isSpeaking.value = false
                        currentSpeakingNewsId.value = null
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    mainHandler.post {
                        isSpeaking.value = false
                        currentSpeakingNewsId.value = null
                    }
                }
            })

            isInitialized.value = true
        }
    }

    fun speak(newsId: Long, headline: String, content: String) {
        if (!isInitialized.value) return

        // If already speaking this item, toggle off / stop
        if (isSpeaking.value && currentSpeakingNewsId.value == newsId) {
            stop()
            return
        }

        // If speaking another item, stop first
        stop()

        val textToSpeak = "$headline. $content"
        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "NEWS_TTS_$newsId")
        }
        
        currentSpeakingNewsId.value = newsId
        isSpeaking.value = true

        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, params, "NEWS_TTS_$newsId")
    }

    fun speak(headline: String, content: String) {
        speak(-1L, headline, content)
    }

    fun stop() {
        tts?.stop()
        isSpeaking.value = false
        currentSpeakingNewsId.value = null
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        isSpeaking.value = false
        currentSpeakingNewsId.value = null
    }
}

