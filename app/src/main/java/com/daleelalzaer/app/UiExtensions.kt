package com.daleelalzaer.app

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable as composeRememberSaveable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/** Local UI helper used by the native screens for press-scale animation. */
fun Modifier.graphicsLayer(scaleX: Float = 1f, scaleY: Float = 1f): Modifier =
    scale(scaleX = scaleX, scaleY = scaleY)

/** Local equivalent of collectIsPressedAsState; kept here to avoid an extra import in MainActivity. */
@Composable
fun InteractionSource.collectIsPressedAsState(): State<Boolean> {
    val state = remember(this) { mutableStateOf(false) }
    LaunchedEffect(this) {
        interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> state.value = true
                is PressInteraction.Release, is PressInteraction.Cancel -> state.value = false
            }
        }
    }
    return state
}

/** Typed saveable-state wrappers. Explicit return types prevent Kotlin 2.x inference ambiguity. */
@Composable
fun <T> rememberSaveable(init: () -> MutableState<T>): MutableState<T> =
    composeRememberSaveable(init = init)

@Composable
fun rememberSaveable(init: () -> MutableIntState): MutableIntState =
    composeRememberSaveable(init = init)

/** Used only by the Toggle helper, which is not itself a RowScope receiver. */
fun Modifier.weight(weight: Float): Modifier =
    fillMaxWidth((weight / 2f).coerceIn(0.01f, 1f))

/** Compatibility wrapper for coroutine launch used by MainActivity. */
fun CoroutineScope.launch(block: suspend CoroutineScope.() -> Unit): Job =
    kotlinx.coroutines.launch(this, block = block)
