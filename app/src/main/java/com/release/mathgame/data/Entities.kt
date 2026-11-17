package com.release.mathgame.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val avatarEmoji: String = "🎓",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val score: Int,
    val correctCount: Int,
    val totalCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
