package com.woosung.interaction.water

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import java.nio.file.Path

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

@Composable
fun DrawWave(yList: List<Int>, width: Int, height: Int) : androidx.compose.ui.graphics.Path = Path().apply {
    moveTo(0f , 0f)
}

    yList.forEachIndexed{y , idx ->

    }
}
