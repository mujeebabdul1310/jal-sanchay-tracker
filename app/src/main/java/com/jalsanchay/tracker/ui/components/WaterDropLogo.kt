package com.jalsanchay.tracker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WaterDropLogo(
    size: Dp = 100.dp,
    animated: Boolean = true
) {
    val scaleAnim by animateFloatAsState(
        targetValue = if (animated) 1.0f else 1.0f,
        animationSpec = if (animated) {
            tween(1000, easing = EaseOutBack)
        } else {
            snap()
        },
        label = "logo_scale"
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (animated) 1.0f else 1.0f,
        animationSpec = if (animated) {
            tween(800)
        } else {
            snap()
        },
        label = "logo_alpha"
    )

    Canvas(
        modifier = Modifier
            .size(size)
            .graphicsLayer(
                scaleX = scaleAnim,
                scaleY = scaleAnim,
                alpha = alphaAnim
            )
    ) {
        val width = this.size.width
        val height = this.size.height

        val path = Path().apply {
            moveTo(width / 2, 0f) // Tip
            cubicTo(
                width / 2, 0f,
                width * 0.9f, height * 0.6f,
                width * 0.9f, height * 0.75f
            )
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    width * 0.1f, height * 0.5f,
                    width * 0.9f, height * 1.0f
                ),
                startAngleDegrees = 0f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            cubicTo(
                width * 0.1f, height * 0.6f,
                width / 2, 0f,
                width / 2, 0f
            )
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF00BCD4), Color(0xFF1565C0)),
                startY = 0f,
                endY = height
            )
        )
    }
}
