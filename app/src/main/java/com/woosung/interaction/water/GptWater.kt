package com.woosung.interaction.water

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview

data class WaveConfig(
    val waveHeight: Float,
    val waveSpeed: Float,
    val waveColor: Color,
    val waveLengthMultiplier: Float
)

@Composable
fun WaveAnimation(
    waveConfigs: List<WaveConfig>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height


        waveConfigs.forEach { config ->

            

            val path = Path()
            val waveLength = width * config.waveLengthMultiplier
            val halfWaveLength = waveLength / 2

            path.moveTo(0f, height / 2)

            for (i in 0..width.toInt() step halfWaveLength.toInt()) {
                val x1 = i.toFloat() + (waveLength * wavePhase)
                val y1 = height / 2 + if (i % waveLength == 0f) config.waveHeight else -config.waveHeight
                val x2 = i + halfWaveLength.toFloat() + (waveLength * wavePhase)
                val y2 = height / 2 + if (i % waveLength == 0f) -config.waveHeight else config.waveHeight
                val x3 = i + waveLength.toFloat() + (waveLength * wavePhase)

                path.cubicTo(
                    x1, y1,
                    x2, y2,
                    x3, height / 2
                )
            }

            drawPath(path, config.waveColor)
        }
    }
}

@Preview
@Composable
fun WaveAnimationPreview() {
    WaveAnimation(
        waveConfigs = listOf(
            WaveConfig(
                waveHeight = 50f,
                waveSpeed = 2000f,
                waveColor = Color(0x550080FF),
                waveLengthMultiplier = 1f
            ),
            WaveConfig(
                waveHeight = 40f,
                waveSpeed = 3000f,
                waveColor = Color(0x990080FF),
                waveLengthMultiplier = 1.5f
            ),
            WaveConfig(
                waveHeight = 30f,
                waveSpeed = 4000f,
                waveColor = Color(0xFF0080FF),
                waveLengthMultiplier = 2f
            )
        )
    )
}