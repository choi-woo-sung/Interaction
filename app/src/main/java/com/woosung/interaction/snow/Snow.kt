package com.woosung.interaction.snow

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.isActive
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration.Companion.nanoseconds

internal fun Modifier.snowfall() = composed {
    var snowflakesState by remember {
        mutableStateOf(SnowflakesState(-1, IntSize(0, 0)))
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameNanos { newTick ->
                val elapsedMillis =
                    (newTick - snowflakesState.tickNanos).nanoseconds.inWholeMilliseconds
                val wasFirstRun = snowflakesState.tickNanos < 0
                snowflakesState.tickNanos = newTick

                if (wasFirstRun) return@withFrameNanos
                for (snowflake in snowflakesState.snowflakes) {
                    snowflake.update()
                }
            }
        }
    }


    onSizeChanged { newSize -> snowflakesState = snowflakesState.resize(newSize) }
        .clipToBounds()
        .drawWithContent {
            // drawContent()는 반드시 호출 해야함.
            drawContent()
            snowflakesState.draw(drawContext.canvas)
        }
}

fun ClosedRange<Float>.random() =
    ThreadLocalRandom.current().nextFloat() * (endInclusive - start) + start

fun Float.random() =
    ThreadLocalRandom.current().nextFloat() * this

fun Int.random() =
    ThreadLocalRandom.current().nextInt(this)

fun IntSize.randomPosition() =
    Offset(width.random().toFloat(), height.random().toFloat())

// 빽빽도
private const val snowflakeDensity = 0.3
private val incrementRange = 0.4f..0.8f
private val sizeRange = 5.0f..12.0f
private const val angleSeed = 25.0f
private val angleSeedRange = -angleSeed..angleSeed
const val angleRange = 0.001f
private const val angleDivisor = 10000.0f

internal data class SnowflakesState(
    var tickNanos: Long,
    val snowflakes: List<Snowflake>,
) {

    constructor(tick: Long, canvasSize: IntSize) : this(tick, createSnowflakes(canvasSize))

    fun draw(canvas: Canvas) {
        snowflakes.forEach { it.draw(canvas) }
    }

    fun resize(newSize: IntSize) = copy(snowflakes = createSnowflakes(newSize))

    companion object {

        private fun createSnowflakes(canvasSize: IntSize): List<Snowflake> {
            val canvasArea = canvasSize.width * canvasSize.height
            val normalizedDensity = snowflakeDensity.coerceIn(0.0..1.0) / 1000.0
            val snowflakesCount = (canvasArea * normalizedDensity).roundToInt()

            return List(snowflakesCount) {
                Snowflake(
                    incrementFactor = incrementRange.random(),
                    size = sizeRange.random(),
                    canvasSize = canvasSize,
                    position = canvasSize.randomPosition(),
                    angle = angleSeedRange.random() / angleSeed * angleRange + (PI / 2.0),
                )
            }
        }
    }
}

private val snowflakePaint = Paint().apply {
    isAntiAlias = true
    color = Color.White
    style = PaintingStyle.Fill
}

internal class Snowflake(
    private val incrementFactor: Float,
    private val size: Float,
    private val canvasSize: IntSize,
    position: Offset,
    angle: Double,
) {

    private var position by mutableStateOf(position)
    private var angle by mutableStateOf(angle)

    fun update() {
        val increment = incrementRange.random()

        val xAngle = (increment * cos((angle))).toFloat()

        val yAngle = (increment * sin((angle))).toFloat()

        angle += angleSeedRange.random() / 10000f

        position =
            if (position.x > canvasSize.width || position.x < 0f) {
                position.copy(x = canvasSize.width.random().toFloat(), y = 0f)
            } else if (position.y > canvasSize.height) {
                position.copy(y = 0f)
            } else {
                position.copy(
                    x = position.x + xAngle,
                    y = position.y + yAngle,
                )
            }
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(position, size, snowflakePaint)
    }

    companion object {

        private const val baseFrameDurationMillis = 16f
        private const val baseSpeedPxAt60Fps = 2f
    }
}

@Preview
@Composable
fun a() {
    Surface(modifier = Modifier.fillMaxSize().snowfall(), color = Color.Black) {
    }
}
