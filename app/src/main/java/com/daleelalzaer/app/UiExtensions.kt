package com.daleelalzaer.app

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable as composeRememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch as coroutineLaunch

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

/**
 * Exact generic wrapper around Compose rememberSaveable.
 * It deliberately preserves the concrete state type returned by the initializer
 * (MutableState, MutableIntState, MutableLongState, etc.) so Kotlin does not
 * widen the state and create ++ / delegate overload ambiguities.
 */
@Composable
fun <T> rememberSaveable(init: () -> T): T =
    composeRememberSaveable(init = init)

/** Compatibility helper used by the existing Toggle composable. */
fun Modifier.weight(weight: Float): Modifier =
    fillMaxWidth((weight / 2f).coerceIn(0.01f, 1f))

/** Compatibility wrapper for the existing MainScope().launch calls. */
fun CoroutineScope.launch(block: suspend CoroutineScope.() -> Unit): Job =
    coroutineLaunch(block = block)
