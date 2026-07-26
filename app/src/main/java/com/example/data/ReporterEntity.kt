package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reporters")
data class ReporterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val district: String,
    val role: String = "రిపోర్టర్",
    val isActive: Boolean = true,
    val isVerified: Boolean = true,
    val bio: String = "అధికారిక నమోదిత జర్నలిస్ట్. నిజమైన, ఖచ్చితమైన తాజా ప్రాంతీయ వార్తలను అందించడంలో నిపుణులు."
)
