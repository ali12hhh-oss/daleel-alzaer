package com.daleelalzaer.app

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent as activitySetContent
import androidx.compose.runtime.Composable

/** Local bridge so MainActivity can call setContent without adding another import. */
fun ComponentActivity.setContent(content: @Composable () -> Unit) {
    activitySetContent { content() }
}
