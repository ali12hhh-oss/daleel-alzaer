package com.daleelalzaer.app

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale

/** Local UI helper used by the native screens for press-scale animation. */
fun Modifier.graphicsLayer(scaleX: Float = 1f, scaleY: Float = 1f): Modifier =
    scale(scaleX = scaleX, scaleY = scaleY)
