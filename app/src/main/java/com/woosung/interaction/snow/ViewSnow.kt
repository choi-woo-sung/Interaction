package com.woosung.interaction.snow

import android.util.Log
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
import androidx.compose.ui.draw.clipToBounds
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
            .background(Color.Black)
            .clipToBounds(),
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
    private var increment: Float,
    angle: Double,

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

        increment += incrementPerNanoFrame

        increment.coerceIn(0.6f.. 2.0f)

        val xAngle = (increment * cos((angle))).toFloat()
        val yAngle = (increment * sin((angle))).toFloat()
        Log.d("test", "update: position.x = $xAngle")
        Log.d("test", "angle = $angle")
        Log.d("test", "update: position.y = $yAngle")
        angle += angleSeedRange.random() / 10000f
        position =
/*            if (position.x > screenSize.width || position.x < 0f) {
                position.copy(x = screenSize.width.random().toFloat(), y = 0f)
            } else*/ if (position.y > screenSize.height) {
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

private const val angleSeed = 25.0f
private val angleSeedRange = -angleSeed..angleSeed
private val incrementRange = 0.8f..1.5f
private val incrementPerNanoFrame = (-1f..1f).random() / 10000f

fun createSnowList(canvas: IntSize): List<Snow> {
    return List(100) {
        Snow(
            size = (1f..15f).random(),
            position = Offset(
                x = canvas.width.randomTest().toFloat(),
                y = canvas.height.randomTest().toFloat(),
            ),
            canvas,
            increment = incrementRange.random(),
            angle = (PI / 2.0) + (angleSeed.random() / angleSeed * 0.1f),
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
