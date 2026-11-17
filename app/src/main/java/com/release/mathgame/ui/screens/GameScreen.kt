package com.release.mathgame.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.release.mathgame.model.GameMode
import com.release.mathgame.ui.components.ExpressiveTimerBar
import com.release.mathgame.ui.components.LivesIndicator
import com.release.mathgame.ui.components.NumericKeypad
import com.release.mathgame.ui.components.OptionCard
import com.release.mathgame.ui.components.ScoreBadge
import com.release.mathgame.ui.theme.CoralError
import com.release.mathgame.ui.theme.EmeraldSuccess
import com.release.mathgame.ui.theme.ModeColors
import com.release.mathgame.ui.theme.SpaceMonoFamily
import com.release.mathgame.ui.viewmodel.AnswerStatus
import com.release.mathgame.ui.viewmodel.GameUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    state: GameUiState,
    onInputDigit: (String) -> Unit,
    onClearInput: () -> Unit,
    onDeleteInput: () -> Unit,
    onSubmitDirect: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onQuitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuitDialog by remember { mutableStateOf(false) }

    val accentColor = when (state.mode) {
        GameMode.ADDITION -> ModeColors.Addition
        GameMode.SUBTRACTION -> ModeColors.Subtraction
        GameMode.MULTIPLICATION -> ModeColors.Multiplication
        GameMode.DIVISION -> ModeColors.Division
        GameMode.SQUARE -> ModeColors.Square
        GameMode.SQUARE_ROOT -> ModeColors.SquareRoot
    }

    val isAnswerSubmitted = state.answerStatus != AnswerStatus.NONE

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.mode.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = SpaceMonoFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = state.mode.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                    ) {
                        IconButton(
                            onClick = { showQuitDialog = true },
                            modifier = Modifier.testTag("exit_game_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Exit game",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        ScoreBadge(score = state.score)
                        LivesIndicator(lives = state.lives)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Timer Bar
            ExpressiveTimerBar(
                secondsLeft = state.timeLeft,
                totalSeconds = 60,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Streak & Round info
            if (state.streak >= 2) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🔥", fontSize = 14.sp)
                    Text(
                        text = "${state.streak} IN A ROW!",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Question Display Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(36.dp))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        RoundedCornerShape(36.dp)
                    )
                    .testTag("question_card"),
                shape = RoundedCornerShape(36.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mode Tag Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "MODE: ${state.mode.title.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = state.currentQuestion?.prompt ?: "...",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = SpaceMonoFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("question_text")
                    )

                    // Feedback Banner
                    AnimatedVisibility(
                        visible = isAnswerSubmitted,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))
                        val (bannerBg, bannerTextColor, bannerText) = when (state.answerStatus) {
                            AnswerStatus.CORRECT -> Triple(
                                EmeraldSuccess.copy(alpha = 0.15f),
                                EmeraldSuccess,
                                "🎉 Correct! +10 Points"
                            )

                            AnswerStatus.WRONG -> Triple(
                                CoralError.copy(alpha = 0.15f),
                                CoralError,
                                "❌ Wrong! Correct answer: ${state.currentQuestion?.correctAnswer}"
                            )

                            AnswerStatus.TIMEOUT -> Triple(
                                CoralError.copy(alpha = 0.15f),
                                CoralError,
                                "⏱️ Time's up! Correct answer: ${state.currentQuestion?.correctAnswer}"
                            )

                            AnswerStatus.NONE -> Triple(Color.Transparent, Color.Transparent, "")
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bannerBg)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("feedback_banner")
                        ) {
                            Text(
                                text = bannerText,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = bannerTextColor,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Input / Multiple Choice Section
            if (state.mode.isMultipleChoice) {
                // Multiple Choice Grid (4 options)
                val options = state.currentQuestion?.options ?: emptyList()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (row in options.chunked(2)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            for (option in row) {
                                val isSelected = state.selectedOption == option
                                val isCorrectOption = if (isAnswerSubmitted) {
                                    option == state.currentQuestion?.correctAnswer
                                } else null

                                OptionCard(
                                    value = option,
                                    isSelected = isSelected,
                                    isCorrect = isCorrectOption,
                                    isEnabled = !isAnswerSubmitted && !state.isGameOver,
                                    onClick = { onSelectOption(option) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            } else {
                // Direct Input Mode (Addition, Subtraction, Multiplication)
                // Display entered number
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            2.dp,
                            if (state.inputAnswer.isNotEmpty()) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 16.dp)
                        .testTag("answer_input_box"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.inputAnswer.ifEmpty { "Enter answer..." },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = SpaceMonoFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (state.inputAnswer.isNotEmpty()) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Numeric Keypad
                NumericKeypad(
                    onDigitClick = onInputDigit,
                    onClearClick = onClearInput,
                    onDeleteClick = onDeleteInput
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Submit Button
                Button(
                    onClick = onSubmitDirect,
                    enabled = state.inputAnswer.isNotEmpty() && !isAnswerSubmitted && !state.isGameOver,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("submit_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Answer",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "Next Question" Button (appears after answer or timeout)
            AnimatedVisibility(
                visible = isAnswerSubmitted && !state.isGameOver,
                enter = fadeIn() + slideInVertically()
            ) {
                Button(
                    onClick = onNextQuestion,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("next_button")
                ) {
                    Text(
                        text = "Next Question",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showQuitDialog) {
        AlertDialog(
            onDismissRequest = { showQuitDialog = false },
            title = { Text("Leave Game?") },
            text = { Text("Are you sure you want to exit? Your current session progress will end.") },
            confirmButton = {
                Button(
                    onClick = {
                        showQuitDialog = false
                        onQuitGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralError),
                    modifier = Modifier.testTag("confirm_exit_button")
                ) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
