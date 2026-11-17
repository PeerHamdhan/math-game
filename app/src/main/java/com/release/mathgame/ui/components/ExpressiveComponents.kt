package com.release.mathgame.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.release.mathgame.ui.theme.AmberWarning
import com.release.mathgame.ui.theme.CoralError
import com.release.mathgame.ui.theme.EmeraldSuccess
import com.release.mathgame.ui.theme.SpaceMonoFamily
import kotlin.random.Random

/**
 * Animated Hearts indicator for user lives (starts at 3).
 */
@Composable
fun LivesIndicator(
    lives: Int,
    maxLives: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("lives_indicator"),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxLives) {
            val isAlive = i <= lives
            val scale by animateFloatAsState(
                targetValue = if (isAlive) 1f else 0.8f,
                animationSpec = spring(dampingRatio = 0.6f),
                label = "heart_scale"
            )
            Icon(
                imageVector = if (isAlive) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = if (isAlive) "Life active" else "Life lost",
                tint = if (isAlive) CoralError else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(22.dp)
                    .scale(scale)
            )
        }
    }
}

/**
 * Expressive Countdown Timer bar with dynamic color transitions and low-time pulse.
 */
@Composable
fun ExpressiveTimerBar(
    secondsLeft: Int,
    totalSeconds: Int = 60,
    modifier: Modifier = Modifier
) {
    val progress = (secondsLeft.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    val timerColor by animateColorAsState(
        targetValue = when {
            secondsLeft > 20 -> MaterialTheme.colorScheme.primary
            secondsLeft > 10 -> AmberWarning
            else -> CoralError
        },
        animationSpec = tween(durationMillis = 300),
        label = "timer_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (secondsLeft <= 10 && secondsLeft > 0) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = "Timer",
                    tint = timerColor,
                    modifier = Modifier
                        .size(18.dp)
                        .scale(pulseScale)
                )
                Text(
                    text = "TIME REMAINING",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 0.8.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${secondsLeft}s",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = SpaceMonoFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = timerColor,
                modifier = Modifier.scale(pulseScale).testTag("timer_value")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                timerColor.copy(alpha = 0.85f),
                                timerColor
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Expressive Score Badge with pop animation on score update.
 */
@Composable
fun ScoreBadge(
    score: Int,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(score) {
        if (score > 0) {
            scale.animateTo(1.25f, animationSpec = tween(120))
            scale.animateTo(1f, animationSpec = spring(dampingRatio = 0.5f))
        }
    }

    Row(
        modifier = modifier
            .scale(scale.value)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("score_badge"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "SCORE",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "$score",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = SpaceMonoFamily,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("score_value")
        )
    }
}

/**
 * Multiple Choice Option Card for Division, Square, and Square Root.
 * Styled in the Sleek Interface aesthetic with rounded-[24px] and tactile border.
 */
@Composable
fun OptionCard(
    value: Int,
    isSelected: Boolean,
    isCorrect: Boolean?,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    optionIndex: Int = 0
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed && isEnabled) 0.95f else 1f,
        label = "press_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected && isCorrect == true -> EmeraldSuccess
            isSelected && isCorrect == false -> CoralError
            isCorrect == true -> EmeraldSuccess.copy(alpha = 0.9f)
            isSelected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.secondaryContainer
        },
        animationSpec = tween(durationMillis = 200),
        label = "opt_bg"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            isSelected || (isCorrect == true) -> Color.White
            else -> MaterialTheme.colorScheme.onSecondaryContainer
        },
        label = "opt_content_color"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected && isCorrect == true -> EmeraldSuccess
            isSelected && isCorrect == false -> CoralError
            isCorrect == true -> EmeraldSuccess
            isSelected -> MaterialTheme.colorScheme.primary
            else -> com.release.mathgame.ui.theme.SleekBorderActive
        },
        label = "opt_border"
    )

    Surface(
        modifier = modifier
            .scale(pressScale)
            .height(82.dp)
            .shadow(
                elevation = if (isSelected) 6.dp else 2.dp,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(
                enabled = isEnabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("option_button_$value"),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$value",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = SpaceMonoFamily,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = "OPTION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected || isCorrect == true) Color.White.copy(alpha = 0.85f)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tactile Numeric Keypad for direct answer modes.
 */
@Composable
fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onClearClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "DEL")
        )

        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (item in row) {
                    KeypadButton(
                        text = item,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (item) {
                                "C" -> onClearClick()
                                "DEL" -> onDeleteClick()
                                else -> onDigitClick(item)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        label = "btn_press"
    )

    val isSpecial = text == "C" || text == "DEL"
    val containerColor = if (isSpecial) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = modifier
            .scale(pressScale)
            .height(58.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                RoundedCornerShape(20.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag("keypad_$text"),
        color = containerColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (text) {
                "DEL" -> {
                    Icon(
                        imageVector = Icons.Filled.Backspace,
                        contentDescription = "Delete digit",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
                "C" -> {
                    Text(
                        text = "CLR",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CoralError
                    )
                }
                else -> {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontFamily = SpaceMonoFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Celebratory Confetti Canvas for Victory & High Score celebrations.
 */
@Composable
fun ConfettiCanvas(
    modifier: Modifier = Modifier
) {
    val particles = remember {
        List(45) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -0.5f,
                speed = Random.nextFloat() * 0.015f + 0.005f,
                radius = Random.nextFloat() * 6f + 4f,
                color = listOf(
                    Color(0xFF4F46E5),
                    Color(0xFF10B981),
                    Color(0xFFF59E0B),
                    Color(0xFFEF4444),
                    Color(0xFF8B5CF6),
                    Color(0xFF06B6D4)
                ).random()
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val currentProgress = progress.value
        particles.forEach { p ->
            val py = ((p.y + currentProgress * p.speed * 80f) % 1.2f) * size.height
            val px = p.x * size.width + kotlin.math.sin((currentProgress * 6f + p.x * 10f).toDouble()).toFloat() * 20f
            drawCircle(
                color = p.color,
                radius = p.radius,
                center = Offset(px, py)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val radius: Float,
    val color: Color
)
