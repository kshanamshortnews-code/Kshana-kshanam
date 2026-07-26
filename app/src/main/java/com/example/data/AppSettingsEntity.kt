package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val appName: String = "క్షణ క్షణం",
    val tagline: String = "తాజా వార్తల వీక్షణం",
    val contactNumber: String = "+91 98765 43210",
    val adminPasscode: String = "1234",
    val appIconUrl: String = "",
    val isAutoPlayEnabled: Boolean = false
)
