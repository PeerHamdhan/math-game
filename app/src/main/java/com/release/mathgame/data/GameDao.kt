package com.release.mathgame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: GameRecord): Long

    @Query("SELECT * FROM game_records ORDER BY timestamp DESC LIMIT 30")
    fun getRecentRecords(): Flow<List<GameRecord>>

    @Query("SELECT MAX(score) FROM game_records WHERE mode = :mode")
    fun getHighScoreForMode(mode: String): Flow<Int?>

    @Query("SELECT mode, MAX(score) as maxScore FROM game_records GROUP BY mode")
    fun getHighScoresByMode(): Flow<List<ModeHighScore>>

    @Query("SELECT SUM(score) FROM game_records")
    fun getTotalScoreAccumulated(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM game_records")
    fun getTotalGamesPlayed(): Flow<Int>
}

data class ModeHighScore(
    val mode: String,
    val maxScore: Int
)
