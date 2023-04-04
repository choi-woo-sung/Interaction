package com.woosung.interaction.water

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun Water(
    waterLevel: Float,
) = BoxWithConstraints {
    require(waterLevel in 0f..1f) { "여기 사이에 있어야함" }
    val density = LocalDensity.current
    val height = with(density) { maxHeight.toPx() }
    val width = with(density) { maxWidth.toPx() }

    val currentY = height * waterLevel

    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(0f, currentY)
            lineTo(width, currentY)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(
            path = path,
            color = androidx.compose.ui.graphics.Color.Blue,
        )
    }
}

fun DrawWave(yList: List<Int>, width: Int, waterHeight: Float): Path = Path().apply {
    moveTo(0f, 0f)
    lineTo(0f, waterHeight)

    // 간격 -> 실제 간격 사이즈
    val interval = width * (1 / (yList.size + 1).toFloat())

    yList.forEachIndexed { idx, y ->

        // 간격 끝의 범위
        val segmentIndex = (idx + 1) / (yList.size + 1).toFloat()

        // 간격 끝의 x
        val x = width * segmentIndex

        cubicTo(
            x1 = if (idx == 0) 0f else x - interval / 2f,
            y1 = yList.getOrNull(idx - 1)?.toFloat() ?: waterHeight,
            // 절반
            x2 = x - interval / 2f,
            y2 = y.toFloat(),
            // 끝점
            x3 = x,
            y3 = y.toFloat(),
        )
    }
    cubicTo(
        x1 = width - interval / 2f,
        y1 = yList.last().toFloat(),
        x2 = width.toFloat(),
        y2 = waterHeight,
        x3 = width.toFloat(),
        y3 = waterHeight,
    )

    lineTo(width.toFloat(), 0f)
    close()
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun WaterPreview() {
    val test = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        test.animateTo(1f, tween(30000))
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,
    ) {
        Water(test.value)
    }
}

@Preview
@Composable
fun DrawWavePreview() {
    Canvas(
        modifier = Modifier
            .graphicsLayer(alpha = 0.99f)
            .fillMaxSize(),
    ) {
        drawPath(
            path = DrawWave(
                listOf(400, 430, 410, 390, 380, 360),
                width = size.width.roundToInt(),
                400f,
            ),
            color = Color.Black,
        )
    }
}
