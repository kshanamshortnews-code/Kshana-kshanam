package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.widget.Toast
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.data.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    suspend fun shareNewsCardImage(
        context: Context,
        news: NewsEntity,
        appName: String,
        tagline: String
    ) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "న్యూస్ కార్డ్ ఇమేజ్ తయారవుతోంది...", Toast.LENGTH_SHORT).show()
        }

        withContext(Dispatchers.IO) {
            try {
                // 1. Fetch news image if available
                val mediaBitmap = fetchMediaBitmap(context, news.mediaUrl)

                // 2. Render News Card Image Bitmap
                val cardBitmap = generateNewsCardBitmap(context, news, appName, tagline, mediaBitmap)

                // 3. Save to Cache File
                val cachePath = File(context.cacheDir, "shared_images")
                cachePath.mkdirs()
                val imageFile = File(cachePath, "news_card_${news.id}.jpg")
                FileOutputStream(imageFile).use { out ->
                    cardBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }

                // 4. Get FileProvider Content URI
                val imageUri: Uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    imageFile
                )

                // 5. Build Share Text & Intent
                val captionText = """
                    🔴 *${appName} - ${news.category}*
                    📰 *${news.headline}*

                    ${news.content}

                    ✍️ *రిపోర్టర్:* ${news.reporterName} | 📍 *ప్రాంతం:* ${news.district}

                    📲 మరింత సమాచారం కోసం *${appName}* యాప్‌ను వీక్షించండి!
                """.trimIndent()

                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, captionText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                // Try opening directly in WhatsApp if available, fallback to chooser
                withContext(Dispatchers.Main) {
                    val whatsappIntent = Intent(shareIntent).apply {
                        setPackage("com.whatsapp")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    try {
                        context.startActivity(whatsappIntent)
                    } catch (e: Exception) {
                        val chooserIntent = Intent.createChooser(shareIntent, "న్యూస్ కార్డ్‌ని షేర్ చేయండి").apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(chooserIntent)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "ఇమేజ్ షేరింగ్ విఫలమైంది: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Shares the news title and formatted content directly to WhatsApp using Intent.ACTION_SEND
     */
    fun shareToWhatsApp(
        context: Context,
        news: NewsEntity,
        appName: String = "క్షణ క్షణం",
        tagline: String = "తెలుగు వార్తలు"
    ) {
        val formattedText = """
            🔴 *$appName - ${news.category}*

            📰 *${news.headline}*

            ${news.content}

            ✍️ *రిపోర్టర్:* ${news.reporterName}
            📍 *ప్రాంతం:* ${news.district}

            📲 ప్రతి క్షణం తాజా వార్తల కోసం *$appName* యాప్‌ను చూడండి!
        """.trimIndent()

        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, news.headline)
            putExtra(Intent.EXTRA_TEXT, formattedText)
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            // Fallback if WhatsApp is not installed or package dispatch fails
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, news.headline)
                putExtra(Intent.EXTRA_TEXT, formattedText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(fallbackIntent, "వాట్సాప్‌ ద్వారా వార్తను షేర్ చేయండి").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        }
    }

    private suspend fun fetchMediaBitmap(context: Context, url: String): Bitmap? {
        if (url.isBlank()) return null
        return try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false) // Required for canvas drawing
                .build()
            val result = loader.execute(request)
            if (result is SuccessResult) {
                (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun generateNewsCardBitmap(
        context: Context,
        news: NewsEntity,
        appName: String,
        tagline: String,
        mediaBitmap: Bitmap?
    ): Bitmap {
        val width = 1080
        val padding = 40f
        val usableWidth = (width - padding * 2).toInt()

        // Create TextPaints to calculate layout height first
        val headlinePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 46f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val contentPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#E0E0EC")
            textSize = 34f
        }

        val headlineLayout = createStaticLayout(news.headline, headlinePaint, usableWidth - 40)
        val contentLayout = createStaticLayout(news.content, contentPaint, usableWidth)

        // Calculate dynamic height
        val headerHeight = 180f
        val mediaHeight = if (mediaBitmap != null || news.mediaUrl.isNotBlank()) 562f else 180f
        val headlineSectionHeight = headlineLayout.height + 40f
        val contentSectionHeight = contentLayout.height + 40f
        val footerHeight = 260f

        val totalHeight = (headerHeight + mediaHeight + headlineSectionHeight + contentSectionHeight + footerHeight + 80f).toInt()

        val bitmap = Bitmap.createBitmap(width, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Canvas Background
        val bgPaint = Paint().apply { color = Color.parseColor("#12121A") }
        canvas.drawRect(0f, 0f, width.toFloat(), totalHeight.toFloat(), bgPaint)

        // 2. Header Bar with Red Gradient
        val headerGradient = LinearGradient(
            0f, 0f, width.toFloat(), headerHeight,
            Color.parseColor("#C62828"), Color.parseColor("#800000"),
            Shader.TileMode.CLAMP
        )
        val headerPaint = Paint().apply { shader = headerGradient }
        canvas.drawRect(0f, 0f, width.toFloat(), headerHeight, headerPaint)

        // Header Title
        val appNamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 50f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("🔴 $appName", padding, 75f, appNamePaint)

        val taglinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            textSize = 28f
        }
        canvas.drawText(tagline, padding, 125f, taglinePaint)

        // Category & District Pill Badges on top right
        val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E1E2E") }
        val badgeTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val categoryBadgeText = "📌 ${news.category}"
        val badgeWidth = badgeTextPaint.measureText(categoryBadgeText) + 30f
        val badgeRect = RectF(width - padding - badgeWidth, 50f, width - padding, 110f)
        canvas.drawRoundRect(badgeRect, 16f, 16f, badgePaint)
        canvas.drawText(categoryBadgeText, width - padding - badgeWidth + 15f, 90f, badgeTextPaint)

        var currentY = headerHeight + 30f

        // 3. Media Image
        val mediaRect = RectF(padding, currentY, width - padding, currentY + mediaHeight)
        if (mediaBitmap != null) {
            val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            val path = Path().apply {
                addRoundRect(mediaRect, 24f, 24f, Path.Direction.CW)
            }
            canvas.save()
            canvas.clipPath(path)
            canvas.drawBitmap(mediaBitmap, null, mediaRect, imagePaint)
            canvas.restore()
        } else {
            // Placeholder graphic box if image is not loaded
            val placeholderPaint = Paint().apply { color = Color.parseColor("#1E1E2C") }
            canvas.drawRoundRect(mediaRect, 24f, 24f, placeholderPaint)
            val placeholderTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFD700")
                textSize = 36f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText("📰 $appName - ${news.category}", padding + 40f, currentY + (mediaHeight / 2) + 10f, placeholderTextPaint)
        }

        currentY += mediaHeight + 40f

        // 4. Headline Section with Left Accent Bar
        val accentBarPaint = Paint().apply { color = Color.parseColor("#D32F2F") }
        val barRect = RectF(padding, currentY, padding + 14f, currentY + headlineLayout.height)
        canvas.drawRoundRect(barRect, 6f, 6f, accentBarPaint)

        canvas.save()
        canvas.translate(padding + 30f, currentY)
        headlineLayout.draw(canvas)
        canvas.restore()

        currentY += headlineLayout.height + 30f

        // 5. Content Section
        canvas.save()
        canvas.translate(padding, currentY)
        contentLayout.draw(canvas)
        canvas.restore()

        currentY += contentLayout.height + 40f

        // 6. Divider Line
        val dividerPaint = Paint().apply {
            color = Color.parseColor("#333348")
            strokeWidth = 3f
        }
        canvas.drawLine(padding, currentY, width - padding, currentY, dividerPaint)

        currentY += 30f

        // 7. Footer: Reporter & District Info
        val footerInfoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("✍️ రిపోర్టర్: ${news.reporterName}", padding, currentY + 30f, footerInfoPaint)

        val districtPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#A0A0B0")
            textSize = 28f
        }
        val timeString = android.text.format.DateUtils.getRelativeTimeSpanString(
            news.timestamp,
            System.currentTimeMillis(),
            android.text.format.DateUtils.MINUTE_IN_MILLIS
        ).toString()
        canvas.drawText("📍 ప్రాంతం: ${news.district} | 🕒 $timeString", padding, currentY + 70f, districtPaint)

        currentY += 100f

        // Bottom App Download Banner
        val bannerRect = RectF(padding, currentY, width - padding, currentY + 80f)
        val bannerBgPaint = Paint().apply { color = Color.parseColor("#2A1215") }
        canvas.drawRoundRect(bannerRect, 16f, 16f, bannerBgPaint)

        val bannerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 28f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("📲 ప్రతి క్షణం తాజా వార్తల కోసం '$appName' యాప్‌ను ఉచితంగా డౌన్‌లోడ్ చేసుకోండి!", padding + 20f, currentY + 50f, bannerTextPaint)

        return bitmap
    }

    private fun createStaticLayout(text: String, textPaint: TextPaint, width: Int): StaticLayout {
        val alignment = Layout.Alignment.ALIGN_NORMAL
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(text, 0, text.length, textPaint, width)
                .setAlignment(alignment)
                .setLineSpacing(0f, 1.2f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(text, textPaint, width, alignment, 1.2f, 0f, false)
        }
    }
}
