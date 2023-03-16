package com.woosung.interaction.snow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.isActive
import java.util.concurrent.ThreadLocalRandom

internal fun Modifier.snowfall() = composed {
    var snowflakesState by remember { mutableStateOf(SnowState(listOf())) }

    LaunchedEffect(Unit) {
        while (isActive) {
            awaitFrame()
            for (snowflake in snowflakesState.snows) {
                snowflake.update()
            }
        }
    }

    onSizeChanged { newSize -> snowflakesState = snowflakesState.copy(createSnowList(newSize)) }
        .clipToBounds()
        .drawWithContent {
            // drawContent()는 반드시 호출 해야함.
            drawContent()
            snowflakesState.snows.forEach { it.draw(drawContext.canvas) }
        }
}

fun ClosedRange<Float>.random() = ThreadLocalRandom.current().nextFloat() * (endInclusive - start) + start

fun Float.random() =
    ThreadLocalRandom.current().nextFloat() * this

fun Int.random() =
    ThreadLocalRandom.current().nextInt(this)

fun IntSize.randomPosition() =
    Offset(width.random().toFloat(), height.random().toFloat())

// 빽빽도
private const val snowflakeDensity = 0.3
private val incrementRange = 1.4f..1.6f
private val sizeRange = 5.0f..12.0f
private const val angleSeed = 25.0f
private val angleSeedRange = -angleSeed..angleSeed
private const val angleRange = 0.1f
private const val angleDivisor = 10000.0f

// internal data class SnowflakesState(
//    var tickNanos: Long,
//    val snowflakes: List<Snowflake>,
// ) {
//
//    constructor(tick: Long, canvasSize: IntSize) : this(tick, createSnowflakes(canvasSize))
//
//    fun draw(canvas: Canvas) {
//        snowflakes.forEach { it.draw(canvas) }
//    }
//
//    fun resize(newSize: IntSize) = copy(snowflakes = createSnowflakes(newSize))
//
//    companion object {
//
//        private fun createSnowflakes(canvasSize: IntSize): List<Snowflake> {
//            val canvasArea = canvasSize.width * canvasSize.height
//            val normalizedDensity = snowflakeDensity.coerceIn(0.0..1.0) / 1000.0
//            val snowflakesCount = (canvasArea * normalizedDensity).roundToInt()
//
//            return List(snowflakesCount) {
//                Snowflake(
//                    incrementFactor = incrementRange.random(),
//                    size = 20f,
//                    canvasSize = canvasSize,
//                    position = canvasSize.randomPosition(),
//                    angle = angleSeed.random() / angleSeed * angleRange + (PI / 2.0),
//                )
//            }
//        }
//    }
// }
//
// private val snowflakePaint = Paint().apply {
//    isAntiAlias = true
//    color = Color.White
//    style = PaintingStyle.Fill
// }

// internal class Snowflake(
//    private val incrementFactor: Float,
//    private val size: Float,
//    private val canvasSize: IntSize,
//    position: Offset,
//    angle: Double,
// ) {
//
//    private var position by mutableStateOf(position)
//    private var angle by mutableStateOf(angle)
//
//    fun update(elapsedMillis: Long) {
//
//        val increment = incrementRange.random()
//
//        val xAngle = (increment * cos((angle))).toFloat()
//        val yAngle = (increment * sin((angle))).toFloat()
//        Log.d("test", "update: position.x = $xAngle")
//        Log.d("test", "angle = $angle")
//        Log.d("test", "update: position.y = $yAngle")
//
//        position =
//                /*            if (position.x > screenSize.width || position.x < 0f) {
//                                position.copy(x = screenSize.width.random().toFloat(), y = 0f)
//                            } else*/ if (position.y > canvasSize.height) {
//                position.copy(y = 0f)
//            } else {
//                position.copy(
//                    x = position.x + xAngle,
//                    y = position.y + yAngle,
//                )
//            }
//
//        angle += angleSeedRange.random() / 10000f
//    }
//
//    fun draw(canvas: Canvas) {
//        canvas.drawCircle(position, size, snowflakePaint)
//    }
// }

@Preview
@Composable
fun a() {
    Surface(modifier = Modifier.fillMaxSize().snowfall(), color = Color.Black) {
    }
}
