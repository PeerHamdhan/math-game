package com.release.mathgame.data

import kotlinx.coroutines.flow.Flow

class MathGameRepository(private val dao: GameDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile()
    val recentRecords: Flow<List<GameRecord>> = dao.getRecentRecords()
    val highScoresByMode: Flow<List<ModeHighScore>> = dao.getHighScoresByMode()
    val totalScoreAccumulated: Flow<Int?> = dao.getTotalScoreAccumulated()
    val totalGamesPlayed: Flow<Int> = dao.getTotalGamesPlayed()

    fun getHighScoreForMode(mode: String): Flow<Int?> {
        return dao.getHighScoreForMode(mode)
    }

    suspend fun saveUserProfile(name: String, avatarEmoji: String = "🎓") {
        dao.saveUserProfile(
            UserProfile(
                id = 1,
                name = name.trim(),
                avatarEmoji = avatarEmoji,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveGameRecord(
        mode: String,
        score: Int,
        correctCount: Int,
        totalCount: Int
    ): Long {
        return dao.insertRecord(
            GameRecord(
                mode = mode,
                score = score,
                correctCount = correctCount,
                totalCount = totalCount,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
