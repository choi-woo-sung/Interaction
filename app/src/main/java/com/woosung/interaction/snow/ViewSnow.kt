package com.woosung.interaction.snow

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import com.woosung.interaction.ext.dpToPx
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SnowScreen() {
    val density = LocalDensity.current
    val screenWidth =
        (Dp(LocalConfiguration.current.screenWidthDp.toFloat())).dpToPx(density).toInt()
    val screenHeight =
        (Dp(LocalConfiguration.current.screenHeightDp.toFloat())).dpToPx(density).toInt()

    var snowState by remember {
        mutableStateOf(
            SnowState(
                createSnowList(
                    IntSize(
                        screenWidth,
                        screenHeight,
                    ),
                ),
            ),
        )
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val canvas = drawContext.canvas
        for (snow in snowState.snows) {
            snow.draw(canvas)
        }
    }
    LaunchedEffect(Unit) {
        while (isActive) {
            awaitFrame()
            for (snow in snowState.snows) {
                snow.update()
            }
        }
    }
}

data class SnowState(val snows: List<Snow>)

class Snow(
    val size: Float,
    position: Offset,
    val screenSize: IntSize,
    private val incrementRange: Float,
    angle: Float,

) {

    val paint: Paint = Paint().apply {
        isAntiAlias = true
        color = Color.White
        style = PaintingStyle.Fill
    }

    // 초기 위치는 정해져있지만, 값은 변화해야한다.
    private var position by mutableStateOf(position)
    private var angle by mutableStateOf(angle)
    fun draw(canvas: Canvas) {
        canvas.drawCircle(position, size, paint)
    }

    fun update() {
        // speed
        val increment = incrementRange.random()

        val xAngle = (increment * cos((angle)))

        val yAngle = (increment * sin((angle)))

        angle += angleSeedRange.random() / 10000f

        position =
            if (position.x > screenSize.width || position.x < 0f) {
                position.copy(x = screenSize.width.random().toFloat(), y = 0f)
            } else if (position.y > screenSize.height) {
                position.copy(y = 0f)
            } else {
                position.copy(
                    x = position.x + xAngle,
                    y = position.y + yAngle,
                )
            }
    }
}
// 1라디안 57.3도

private const val angleSeed = 30.0f
private val angleSeedRange = -angleSeed..angleSeed
private val incrementRange = 1.04f..1.8f

fun createSnowList(canvas: IntSize): List<Snow> {
    return List(200) {
        Snow(
            size = 20f,
            position = Offset(
                x = canvas.width.randomTest().toFloat(),
                y = canvas.height.randomTest().toFloat(),
            ),
            canvas,
            incrementRange = incrementRange.random(),
            angle = angleSeed.random() / angleSeed * 0.1f + (PI.toFloat() / 2.0.toFloat()),
        )
    }
}

fun Int.randomTest() = Random.nextInt(this)
fun Float.randomTest() = Random.nextFloat() * this

@Preview
@Composable
fun SnowPreview() {
    SnowScreen()
}
