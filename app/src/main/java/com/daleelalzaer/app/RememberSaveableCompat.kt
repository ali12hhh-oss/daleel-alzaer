package com.daleelalzaer.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable as composeRememberSaveable

/**
 * Package-local compatibility wrapper for MainActivity.
 * It preserves the exact type returned by the initializer, including
 * MutableState, MutableIntState and other Compose state holders.
 */
@Composable
fun <T : Any> rememberSaveable(init: () -> T): T =
    composeRememberSaveable { init() }
