package com.release.mathgame.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.release.mathgame.data.GameRecord
import com.release.mathgame.data.MathGameDatabase
import com.release.mathgame.data.MathGameRepository
import com.release.mathgame.data.ModeHighScore
import com.release.mathgame.data.UserProfile
import com.release.mathgame.model.GameMode
import com.release.mathgame.model.MathQuestion
import com.release.mathgame.model.QuestionGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

enum class AnswerStatus {
    NONE, CORRECT, WRONG, TIMEOUT
}

data class GameUiState(
    val mode: GameMode = GameMode.ADDITION,
    val currentQuestion: MathQuestion? = null,
    val score: Int = 0,
    val lives: Int = 3,
    val timeLeft: Int = 60,
    val isTimerActive: Boolean = false,
    val inputAnswer: String = "",
    val selectedOption: Int? = null,
    val answerStatus: AnswerStatus = AnswerStatus.NONE,
    val streak: Int = 0,
    val correctCount: Int = 0,
    val totalAnswered: Int = 0,
    val isGameOver: Boolean = false,
    val isNewHighScore: Boolean = false,
    val bestScoreForCurrentMode: Int = 0
)

class MathGameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MathGameRepository

    val userProfile: StateFlow<UserProfile?>
    val highScores: StateFlow<List<ModeHighScore>>
    val totalScore: StateFlow<Int?>
    val totalGames: StateFlow<Int>
    val recentRecords: StateFlow<List<GameRecord>>

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        val database = MathGameDatabase.getInstance(application)
        repository = MathGameRepository(database.gameDao())

        userProfile = repository.userProfile.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )

        highScores = repository.highScoresByMode.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )

        totalScore = repository.totalScoreAccumulated.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            0
        )

        totalGames = repository.totalGamesPlayed.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            0
        )

        recentRecords = repository.recentRecords.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
    }

    fun saveUserName(name: String, avatar: String = "🎓") {
        viewModelScope.launch {
            repository.saveUserProfile(name, avatar)
        }
    }

    fun startNewGame(mode: GameMode) {
        stopTimer()
        viewModelScope.launch {
            val previousHigh = repository.getHighScoreForMode(mode.name).firstOrNull() ?: 0
            val initialQuestion = QuestionGenerator.generate(mode)
            _uiState.update {
                GameUiState(
                    mode = mode,
                    currentQuestion = initialQuestion,
                    score = 0,
                    lives = 3,
                    timeLeft = 60,
                    isTimerActive = true,
                    inputAnswer = "",
                    selectedOption = null,
                    answerStatus = AnswerStatus.NONE,
                    streak = 0,
                    correctCount = 0,
                    totalAnswered = 0,
                    isGameOver = false,
                    isNewHighScore = false,
                    bestScoreForCurrentMode = previousHigh
                )
            }
            startTimer()
        }
    }

    fun onInputDigit(digit: String) {
        if (_uiState.value.answerStatus != AnswerStatus.NONE || _uiState.value.isGameOver) return
        if (_uiState.value.inputAnswer.length < 6) {
            _uiState.update { it.copy(inputAnswer = it.inputAnswer + digit) }
        }
    }

    fun onClearInput() {
        if (_uiState.value.answerStatus != AnswerStatus.NONE || _uiState.value.isGameOver) return
        _uiState.update { it.copy(inputAnswer = "") }
    }

    fun onDeleteInput() {
        if (_uiState.value.answerStatus != AnswerStatus.NONE || _uiState.value.isGameOver) return
        _uiState.update {
            if (it.inputAnswer.isNotEmpty()) {
                it.copy(inputAnswer = it.inputAnswer.dropLast(1))
            } else {
                it
            }
        }
    }

    fun submitDirectAnswer() {
        val current = _uiState.value
        if (current.inputAnswer.isEmpty() || current.answerStatus != AnswerStatus.NONE) return
        val userAnswer = current.inputAnswer.toIntOrNull() ?: return
        processAnswer(userAnswer)
    }

    fun selectMultipleChoiceOption(option: Int) {
        val current = _uiState.value
        if (current.answerStatus != AnswerStatus.NONE || current.isGameOver) return
        _uiState.update { it.copy(selectedOption = option) }
        processAnswer(option)
    }

    private fun processAnswer(answer: Int) {
        stopTimer()
        val current = _uiState.value
        val question = current.currentQuestion ?: return

        val isCorrect = (answer == question.correctAnswer)
        val newScore = if (isCorrect) current.score + 10 else current.score
        val newLives = if (isCorrect) current.lives else current.lives - 1
        val newStreak = if (isCorrect) current.streak + 1 else 0
        val newCorrectCount = if (isCorrect) current.correctCount + 1 else current.correctCount
        val newTotalAnswered = current.totalAnswered + 1

        val isGameOver = (newLives <= 0)

        _uiState.update {
            it.copy(
                score = newScore,
                lives = newLives,
                streak = newStreak,
                correctCount = newCorrectCount,
                totalAnswered = newTotalAnswered,
                answerStatus = if (isCorrect) AnswerStatus.CORRECT else AnswerStatus.WRONG,
                isTimerActive = false,
                isGameOver = isGameOver
            )
        }

        if (isGameOver) {
            handleGameOver(newScore, newCorrectCount, newTotalAnswered)
        }
    }

    fun nextQuestion() {
        val current = _uiState.value
        if (current.isGameOver) return

        stopTimer()
        val nextQ = QuestionGenerator.generate(current.mode)
        _uiState.update {
            it.copy(
                currentQuestion = nextQ,
                timeLeft = 60,
                isTimerActive = true,
                inputAnswer = "",
                selectedOption = null,
                answerStatus = AnswerStatus.NONE
            )
        }
        startTimer()
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0 && _uiState.value.isTimerActive && !_uiState.value.isGameOver) {
                delay(1000L.milliseconds)
                val newTime = _uiState.value.timeLeft - 1
                if (newTime <= 0) {
                    onTimeExpired()
                    break
                } else {
                    _uiState.update { it.copy(timeLeft = newTime) }
                }
            }
        }
    }

    private fun onTimeExpired() {
        stopTimer()
        val current = _uiState.value
        val newLives = current.lives - 1
        val newTotalAnswered = current.totalAnswered + 1
        val isGameOver = (newLives <= 0)

        _uiState.update {
            it.copy(
                timeLeft = 0,
                lives = newLives,
                streak = 0,
                totalAnswered = newTotalAnswered,
                answerStatus = AnswerStatus.TIMEOUT,
                isTimerActive = false,
                isGameOver = isGameOver
            )
        }

        if (isGameOver) {
            handleGameOver(current.score, current.correctCount, newTotalAnswered)
        }
    }

    private fun handleGameOver(finalScore: Int, correct: Int, total: Int) {
        stopTimer()
        viewModelScope.launch {
            val mode = _uiState.value.mode
            val prevBest = _uiState.value.bestScoreForCurrentMode
            val isNewRecord = finalScore > prevBest
            repository.saveGameRecord(
                mode = mode.name,
                score = finalScore,
                correctCount = correct,
                totalCount = total
            )
            _uiState.update {
                it.copy(
                    isGameOver = true,
                    isNewHighScore = isNewRecord
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}
