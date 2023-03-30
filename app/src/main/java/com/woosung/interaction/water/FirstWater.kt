package com.woosung.interaction.water

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import java.lang.Math.PI
import java.lang.Math.sin

@Composable
fun WaveAnimation(
    color: Color = Color.Blue,
    waveHeight: Float = 50f,
    animationSpec: AnimationSpec<Float> = infiniteRepeatable(
        repeatMode = RepeatMode.Restart,
        animation = tween(durationMillis = 4000, easing = LinearEasing),
    ),
) {
    val waveProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        waveProgress.animateTo(1f, animationSpec = animationSpec)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val amplitude = waveHeight

        for (x in 0..size.width.toInt()) {
            val y = size.height / 2 + amplitude * sin((2 * PI * (x + waveProgress.value * size.width)) / size.width).toFloat()
            if (x == 0) {
                path.moveTo(x.toFloat(), y)
            } else {
                path.lineTo(x.toFloat(), y)
            }
        }

        path.lineTo(size.width, size.height)
        path.lineTo(0f, size.height)
        path.close()

        drawPath(
            path = path,
            color = color,
        )
    }
}

@Preview
@Composable
fun WaveAnimationPreview() {
    WaveAnimation()
}
