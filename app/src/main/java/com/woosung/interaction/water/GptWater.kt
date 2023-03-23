package com.woosung.interaction.water

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.lang.Math.PI
import java.lang.Math.sin

@Composable
fun WaveAnimation(
    color: Color = Color.Blue,
    waveHeight: Float = 50f,
    durationMillis: Int = 1000,
    animationSpec: AnimationSpec<Float> = infiniteRepeatable(tween(durationMillis))
) {
    val waveProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        waveProgress.animateTo(1f, animationSpec = animationSpec)
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val amplitude = waveHeight / 2

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
            style = Stroke(width = 1.dp.toPx() , cap = StrokeCap.Round) ,
        )
    }
}

@Preview
@Composable
fun WaveAnimationPreview() {
    WaveAnimation()
}