package com.woosung.interaction.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

fun Dp.dpToPx(density: Density) = with(density) { this@dpToPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

fun Float.pxToDp(density: Density) = with(density) { this@pxToDp.toDp() }
