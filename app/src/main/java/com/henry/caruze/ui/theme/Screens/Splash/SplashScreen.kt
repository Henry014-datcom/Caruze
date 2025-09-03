package com.henry.caruze.ui.theme.Screens.Splash

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.henry.caruze.Navigation.ROUTE_LOGIN
import com.henry.caruze.Navigation.ROUTE_SPLASH
import kotlinx.coroutines.delay
import com.henry.caruze.R

@Composable
fun SplashScreen(navController: NavHostController? = null, onSplashEnd: (() -> Unit)? = null) {
    // Animation states
    var animationStarted by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = LinearOutSlowInEasing),
        label = "alphaAnimation"
    )
    val scaleAnim = animateFloatAsState(
        targetValue = if (animationStarted) 1f else 0.8f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "scaleAnimation"
    )

    // Use primary color from theme instead of colorScheme
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    // Background gradient
    val gradient = Brush.verticalGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.1f),
            surfaceColor
        ),
        startY = 0f,
        endY = 1000f
    )

    LaunchedEffect(true) {
        animationStarted = true
        delay(4000) // 4-second splash duration

        // Call the appropriate callback
        if (navController != null) {
            // Navigate using the navController
            navController.navigate(ROUTE_LOGIN) {
                popUpTo(ROUTE_SPLASH) { inclusive = true }
            }
        } else if (onSplashEnd != null) {
            // Call the provided callback
            onSplashEnd()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        // Optional subtle background pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = primaryColor.copy(alpha = 0.05f),
                center = Offset(size.width * 0.8f, size.height * 0.2f),
                radius = size.minDimension * 0.2f
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.05f),
                center = Offset(size.width * 0.2f, size.height * 0.8f),
                radius = size.minDimension * 0.15f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim.value)
                .alpha(alphaAnim.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.intro),
                contentDescription = "Apps Logo",
                modifier = Modifier
                    .size(150.dp)
                    .shadow(8.dp, shape = CircleShape)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Connecting Trusted",
                color = primaryColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Buyers and Sellers",
                color = primaryColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Loading indicator
            LinearProgressIndicator(
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = primaryColor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashPreview() {
    MaterialTheme {
        SplashScreen()
    }
}