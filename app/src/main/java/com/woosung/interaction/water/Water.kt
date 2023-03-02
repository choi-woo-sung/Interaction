package com.woosung.interaction.water

import android.graphics.BlendMode
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WaterAnimation(
    waterLevel: Float,
    depthMeasurement: String,
) = BoxWithConstraints {
    val density = LocalDensity.current
    val height = with(density) { maxHeight.roundToPx() }
    val width = with(density) { maxWidth.roundToPx() }

    val aYs = calculateYs(height = height, waterLevel = waterLevel, intensityMultiplier = .4f)
    val aYs2 = calculateYs(height = height, waterLevel = waterLevel, intensityMultiplier = .5f)
    val aYs3 = calculateYs(height = height, waterLevel = waterLevel, intensityMultiplier = .7f)

    val currentY = height * waterLevel
    val animatedY by animateFloatAsState(
        targetValue = height * waterLevel,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessVeryLow,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Canvas(
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .fillMaxSize(),
            onDraw = {
                drawPath(
                    path = ayPath(
                        aYs,
                        size,
                        currentY,
                        animatedY,
                    ),
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF41E0E0),
                            Color(0xFF279ED5),
                        ),
                    ),
                )

                drawPath(
                    path = ayPath(
                        aYs2,
                        size,
                        currentY,
                        animatedY,
                    ),
                    alpha = .5f,
                    color = androidx.compose.ui.graphics.Color.Cyan,
                )

                drawPath(
                    path = ayPath(
                        aYs3,
                        size,
                        currentY,
                        animatedY,
                    ),
                    alpha = .3f,
                    color = androidx.compose.ui.graphics.Color.Cyan,
                )

                val paint = androidx.compose.ui.graphics.Paint().asFrameworkPaint()
                paint.apply {
                    isAntiAlias = true
                    textSize = 100.sp.toPx()
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    color = Color.parseColor("#41E0E0")
                    textAlign = Paint.Align.CENTER
                    blendMode = BlendMode.XOR
                }

                drawIntoCanvas {
                    it.nativeCanvas.apply {
                        drawText(
                            depthMeasurement,
                            width / 2f,
                            height / 2f,
                            paint,
                        )
                    }
                }

                (0..60).forEach {
                    val y = it * (size.height / 60)
                    val lineWidth = if (it % 10 == 0) 80f else 40f
                    val strokeWidth = 4f
                    drawLine(
                        start = Offset(0f, y),
                        end = Offset(lineWidth, y),
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF41E0E0),
                                Color(0xFF279ED5),
                            ),
                        ),
                        blendMode = androidx.compose.ui.graphics.BlendMode.Xor,
                        strokeWidth = strokeWidth,
                    )

                    drawLine(
                        start = Offset(size.width, y),
                        end = Offset(size.width - lineWidth, y),
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF41E0E0),
                                Color(0xFF279ED5),
                            ),
                        ),
                        blendMode = androidx.compose.ui.graphics.BlendMode.Xor,
                        strokeWidth = strokeWidth,
                    )
                }
            },
        )
    }
}

fun ayPath(aYs: List<Int>, size: Size, currentY: Float, animatedY: Float): Path {
    return Path().apply {
        moveTo(0f, 0f)
        lineTo(0f, animatedY)
        val interval = size.width * (1 / (aYs.size + 1).toFloat())
        aYs.forEachIndexed { index, y ->
            val segmentIndex = (index + 1) / (aYs.size + 1).toFloat()
            val x = size.width * segmentIndex
            cubicTo(
                x1 = if (index == 0) 0f else x - interval / 2f,
                y1 = aYs.getOrNull(index - 1)?.toFloat() ?: currentY,
                x2 = x - interval / 2f,
                y2 = y.toFloat(),
                x3 = x,
                y3 = y.toFloat(),
            )
        }

        cubicTo(
            x1 = size.width - interval / 2f,
            y1 = aYs.last().toFloat(),
            x2 = size.width,
            y2 = animatedY,
            x3 = size.width,
            y3 = animatedY,
        )
        lineTo(size.width, 0f)
        close()
    }
}

/**
 *
 *
 * @param height 캔버스의 높이
 * @param waterLevel 물로채워야하는 컨테이너 높이
 * @param intensityMultiplier 파도의 강도
 * @return
 */
@Composable
fun calculateYs(height: Int, waterLevel: Float, intensityMultiplier: Float): List<Int> {
    val total = 6
    return (0..total).map {
        calculateY(
            height = height,
            waterLevel = waterLevel,
            ((if (it > total / 2f) total - it else it) / (total / 2f) * 1f) *
                intensityMultiplier,
        )
    }.toList()
}

@Composable
fun calculateY(height: Int, waterLevel: Float, intensity: Float): Int {
    val density = LocalDensity.current

    var y1 by remember {
        mutableStateOf(0)
    }

    val duration = remember {
        Random.nextInt(300) + 300
    }

    val yNoiseAnimation = rememberInfiniteTransition()
    val yNoise by yNoiseAnimation.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    LaunchedEffect(key1 = waterLevel, block = {
        val nextY = (waterLevel * height).toInt()
        val midPoint = height / 2
        val textHeight = with(density) { 100.sp.roundToPx() }
        y1 = if (nextY in (midPoint - textHeight)..(midPoint)) {
            lerp(
                midPoint - textHeight.toFloat(),
                (waterLevel * height),
                (1f - intensity) * .4f,
            ).toInt()
        } else {
            (waterLevel * height).toInt()
        }
        y1 = (y1 + yNoise).toInt()
    })

    val ay1 by animateIntAsState(
        targetValue = y1,
        animationSpec = spring(
            dampingRatio = 1f - intensity,
            stiffness = 100f, // Spring.StiffnessVeryLow
        ),
    )

    return ay1
}

internal fun lerp(start: Float, stop: Float, fraction: Float) = (start * (1 - fraction) + stop * fraction)

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun a() {
    val test = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit){
        test.animateTo(1f, tween(30000))
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color.Black,
    ) {
        WaterAnimation(test.value, "호호")
    }
}
