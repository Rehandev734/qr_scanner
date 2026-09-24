package com.example.scanify.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanify.BuildConfig
import com.example.scanify.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val navigateToHome by viewModel.navigateToHome.collectAsStateWithLifecycle()

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            viewModel.consumeNavigation()
            onNavigateToHome()
        }
    }

    // Entrance animation states
    val badgeScale = remember { Animatable(0.7f) }
    val badgeAlpha = remember { Animatable(0f) }
    val contentAlpha = remember { Animatable(0f) }
    val contentOffsetY = remember { Animatable(20f) }
    val footerAlpha = remember { Animatable(0f) }

    // Pulsing laser sweep line animation across the logo badge
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = -36f,
        targetValue = 36f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser"
    )

    LaunchedEffect(Unit) {
        delay(80)
        badgeAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        badgeScale.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(Unit) {
        delay(220)
        contentAlpha.animateTo(1f, tween(500, easing = FastOutSlowInEasing))
        contentOffsetY.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
    }

    LaunchedEffect(Unit) {
        delay(400)
        footerAlpha.animateTo(1f, tween(400, easing = FastOutSlowInEasing))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // Subtle ambient radial background glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(badgeScale.value)
                .alpha(badgeAlpha.value * 0.35f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Elevated Brand Logo Container with cyber scanner effect
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .scale(badgeScale.value)
                    .alpha(badgeAlpha.value)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(28.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.QrCodeScanner,
                    contentDescription = stringResource(id = R.string.app_name),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(54.dp)
                )

                // Laser sweep line
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(2.5.dp)
                        .offset(y = laserOffsetY.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.primary,
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name with modern typography
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .offset(y = contentOffsetY.value.dp)
                    .alpha(contentAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Modern Feature Pill Badge
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier
                    .offset(y = contentOffsetY.value.dp)
                    .alpha(contentAlpha.value)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.welcome_subtitle),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Bottom Loading & Version Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 32.dp)
                .alpha(footerAlpha.value)
        ) {
            LinearProgressIndicator(
                modifier = Modifier
                    .width(100.dp)
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(100.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "v${BuildConfig.VERSION_NAME} • Private & Fast",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
