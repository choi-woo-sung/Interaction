package com.woosung.interaction.water

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun WaterAnimation2(
    waterLevel: Float,
) = BoxWithConstraints {
    val density = LocalDensity.current
    val height = with(density) { maxHeight.roundToPx() }
    val width = with(density) { maxWidth.roundToPx() }

    val aYs = calculateYs2(height = height, waterLevel = waterLevel, intensityMultiplier = .4f)
    val aYs2 = calculateYs2(height = height, waterLevel = waterLevel, intensityMultiplier = .5f)
    val aYs3 = calculateYs2(height = height, waterLevel = waterLevel, intensityMultiplier = .7f)

    // 현재 물높이
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
                    path = ayPath2(
                        aYs,
                        size,
                        currentY,
                        animatedY,
                    ),
                    color = androidx.compose.ui.graphics.Color.White,
                )

                drawPath(
                    path = ayPath2(
                        aYs2,
                        size,
                        currentY,
                        animatedY,
                    ),
                    alpha = .5f,
                    color = androidx.compose.ui.graphics.Color.White,
                )

                drawPath(
                    path = ayPath2(
                        aYs3,
                        size,
                        currentY,
                        animatedY,
                    ),
                    alpha = .3f,
                    color = androidx.compose.ui.graphics.Color.White,
                )
            },
        )
    }
}

// cubicTo()를 이용해 파도를 계산한다.

fun ayPath2(aYs: List<Int>, size: Size, currentY: Float, animatedY: Float): Path {
    return Path().apply {
        moveTo(0f, 0f)
        lineTo(0f, animatedY)

        // size.width 나누기
        val interval = size.width * (1 / (aYs.size + 1).toFloat())
        aYs.forEachIndexed { index, y ->
            // 파도를 그릴때 한 구간을 나타낸다.  x좌표를 계산하는데 사용한다.
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
 * @param intensityMultiplier 파도의 강도 -> 0.4 0.5 0.7
 * @return
 */
@Composable
fun calculateYs2(height: Int, waterLevel: Float, intensityMultiplier: Float): List<Int> {
    // 파도의 개수
    val total = 6
    return (0..total).map {
        calculateY2(
            height = height,
            waterLevel = waterLevel,
            // 파도의 개수는 무조건 양수로 떨어진다.
            // 0.4 0.5 0.7
            ((if (it > total / 2f) total - it else it) / (total / 2f) * 1f) * intensityMultiplier,
        )
    }.toList()
}

/**
 * TODO
 *
 * @param height 물높이
 * @param waterLevel 물의 레벨
 * @param intensity 강도 많을수록 탄력이 커진다.(좀더 팡팡티긴다.)
 * @return
 */
@Composable
fun calculateY2(height: Int, waterLevel: Float, intensity: Float): Int {
    var y1 by remember { mutableStateOf(0) }

    val duration = remember {
        Random.nextInt(300) +
            300
    }

    // 이걸 더해서 파도를 만들어낼 수 있다.
    val yNoiseAnimation = rememberInfiniteTransition()

    // 파도의 높이 계산
    val yNoise by yNoiseAnimation.animateFloat(
        initialValue = 40f,
        targetValue = -40f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    LaunchedEffect(key1 = waterLevel) {
        // 물의 평균위치 + yNoise 값
        y1 = ((waterLevel * height).toInt() + yNoise).toInt()
    }

    // 감쇠비 변동이 크기
    val ay1 by animateIntAsState(
        targetValue = y1,
        animationSpec = spring(
            dampingRatio = 1f - intensity,
            stiffness = 100f, // Spring.StiffnessVeryLow
        ),
    )

    return ay1
}

@RequiresApi(Build.VERSION_CODES.Q)
@Preview
@Composable
fun a3() {
    val test = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        test.animateTo(1f, tween(30000))
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color.Blue,
    ) {
        WaterAnimation2(test.value)
    }
}
