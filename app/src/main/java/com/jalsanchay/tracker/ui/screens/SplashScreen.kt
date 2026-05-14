package com.jalsanchay.tracker.ui.screens

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.jalsanchay.tracker.navigation.Screen
import com.jalsanchay.tracker.ui.components.WaterDropLogo
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var startAnims by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnims) 1f else 0.3f,
        animationSpec = tween(800, easing = EaseOutBack),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (startAnims) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )
    val tagAlpha by animateFloatAsState(
        targetValue = if (startAnims) 1f else 0f,
        animationSpec = tween(800, delayMillis = 800),
        label = "tag_alpha"
    )

    var progress by remember { mutableStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(2000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        startAnims = true
        progress = 1f
        delay(2500)
        
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1628)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            WaterDropLogo(size = 140.dp, animated = true)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Jal-Sanchay",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            )
            
            Text(
                text = "TRACKER",
                fontSize = 20.sp,
                letterSpacing = 2.sp,
                color = Color(0xFF00BCD4),
                modifier = Modifier
                    .alpha(alpha)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "💧 Measure Your Water Wealth",
                color = MaterialTheme.colorScheme.tertiary,
                fontSize = 15.sp,
                modifier = Modifier.alpha(tagAlpha)
            )
        }

        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            color = Color(0xFF00BCD4),
            trackColor = Color(0xFF132337)
        )
    }
}
