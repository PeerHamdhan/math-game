package com.release.mathgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.release.mathgame.ui.screens.GameScreen
import com.release.mathgame.ui.screens.HomeScreen
import com.release.mathgame.ui.screens.ResultScreen
import com.release.mathgame.ui.screens.UserNameDialog
import com.release.mathgame.ui.theme.MathGameTheme
import com.release.mathgame.ui.viewmodel.MathGameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MathGameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathGameTheme {
                MathGameApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MathGameApp(
    viewModel: MathGameViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val highScores by viewModel.highScores.collectAsStateWithLifecycle()
    val totalScore by viewModel.totalScore.collectAsStateWithLifecycle()
    val totalGames by viewModel.totalGames.collectAsStateWithLifecycle()
    val recentRecords by viewModel.recentRecords.collectAsStateWithLifecycle()
    val gameState by viewModel.uiState.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }

    // Auto-prompt user name on first run if no user profile exists
    LaunchedEffect(userProfile) {
        if (userProfile == null) {
            showProfileDialog = true
        }
    }

    // Auto-navigate to result screen when game is over
    LaunchedEffect(gameState.isGameOver) {
        if (gameState.isGameOver) {
            navController.navigate("result") {
                popUpTo("game") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier.fillMaxSize()
    ) {
        composable("home") {
            HomeScreen(
                userProfile = userProfile,
                highScores = highScores,
                totalScore = totalScore ?: 0,
                totalGames = totalGames,
                recentRecords = recentRecords,
                onSelectMode = { mode ->
                    viewModel.startNewGame(mode)
                    navController.navigate("game")
                },
                onEditProfile = {
                    showProfileDialog = true
                }
            )
        }

        composable("game") {
            GameScreen(
                state = gameState,
                onInputDigit = viewModel::onInputDigit,
                onClearInput = viewModel::onClearInput,
                onDeleteInput = viewModel::onDeleteInput,
                onSubmitDirect = viewModel::submitDirectAnswer,
                onSelectOption = viewModel::selectMultipleChoiceOption,
                onNextQuestion = viewModel::nextQuestion,
                onQuitGame = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("result") {
            ResultScreen(
                finalScore = gameState.score,
                mode = gameState.mode,
                correctCount = gameState.correctCount,
                totalAnswered = gameState.totalAnswered,
                isNewHighScore = gameState.isNewHighScore,
                onPlayAgain = {
                    viewModel.startNewGame(gameState.mode)
                    navController.navigate("game") {
                        popUpTo("home")
                    }
                },
                onBackHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }

    if (showProfileDialog) {
        val isFirstLaunch = userProfile == null
        UserNameDialog(
            initialName = userProfile?.name ?: "",
            initialAvatar = userProfile?.avatarEmoji ?: "🎓",
            canDismiss = !isFirstLaunch,
            onDismiss = { showProfileDialog = false },
            onSaveName = { name, avatar ->
                viewModel.saveUserName(name, avatar)
                showProfileDialog = false
            }
        )
    }
}
