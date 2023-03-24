package com.woosung.interaction.water

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
    depthMeasurement: String,
) = BoxWithConstraints {
    val density = LocalDensity.current
    val height = with(density) { maxHeight.roundToPx() }
    val width = with(density) { maxWidth.roundToPx() }
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
            // 전에 있었던값을 영향을 받아야하네?
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
                listOf(560, 680, 730, 320, 400, 320),
                width = size.width.roundToInt(),
                600f,
            ),
            color = Color.Black,
        )
    }
}
