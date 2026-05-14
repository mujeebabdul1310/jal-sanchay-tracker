package com.jalsanchay.tracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaterTankCanvas(
    totalLiters: Double,
    tankCapacity: Double,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    val fillRatio by animateFloatAsState(
        targetValue = (totalLiters / tankCapacity).coerceIn(0.0, 1.0).toFloat(),
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "tank_fill"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    val textPrimary = MaterialTheme.colorScheme.onBackground
    val primaryBlue = Color(0xFF1976D2)
    val darkBlue = Color(0xFF0D47A1)
    val tankBg = Color(0xFF0D1B2A)
    val borderBlue = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val cornerRadius = 20.dp.toPx()

        // Background Tank
        drawRoundRect(
            color = tankBg,
            size = Size(width, height),
            cornerRadius = CornerRadius(cornerRadius)
        )

        // Water
        val waterTop = height * (1 - fillRatio)
        val wavePath = Path().apply {
            moveTo(0f, waterTop)
            for (x in 0..width.toInt() step 4) {
                val y = waterTop + 15 * sin((x / (width / 2) * PI + waveOffset).toFloat())
                lineTo(x.toFloat(), y)
            }
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = wavePath,
            brush = Brush.verticalGradient(
                colors = listOf(primaryBlue, darkBlue),
                startY = waterTop,
                endY = height
            )
        )

        // Dashed lines at 25, 50, 75
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        for (i in 1..3) {
            val h = height * (i * 0.25f)
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = Offset(0f, h),
                end = Offset(width, h),
                pathEffect = dashEffect,
                strokeWidth = 2f
            )
        }

        // Percentage Text
        val percentageString = "${(fillRatio * 100).toInt()}%"
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 80f
                isFakeBoldText = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            drawText(
                percentageString,
                width / 2,
                height / 2 + 30f,
                paint
            )
        }

        // Tank Border
        drawRoundRect(
            color = borderBlue,
            size = Size(width, height),
            cornerRadius = CornerRadius(cornerRadius),
            style = Stroke(width = 6f)
        )
    }
}
