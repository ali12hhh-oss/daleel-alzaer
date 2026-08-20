package com.daleelalzaer.app

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File
import java.io.FileOutputStream

class PdfViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PdfViewerScreen(::finish) }
    }

    @Composable
    private fun PdfViewerScreen(onBack: () -> Unit) {
        val renderer = remember { PdfHolder(applicationContext, "ahilla.pdf") }
        var page by remember { mutableStateOf(0) }
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        DisposableEffect(renderer) { onDispose { renderer.close() } }
        LaunchedEffect(page) { bitmap = renderer.render(page) }

        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "رجوع") }
                Text("أهلة — PDF", style = MaterialTheme.typography.titleLarge)
                Text("${page + 1} / ${renderer.pageCount}", modifier = Modifier.padding(end = 12.dp))
            }

            Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                bitmap?.let { Image(it.asImageBitmap(), "صفحة ${page + 1}", Modifier.fillMaxSize().padding(8.dp)) }
            }

            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(enabled = page > 0, onClick = { page-- }) { Icon(Icons.Default.ChevronRight, "السابق") }
                IconButton(enabled = page + 1 < renderer.pageCount, onClick = { page++ }) { Icon(Icons.Default.ChevronLeft, "التالي") }
            }
        }
    }
}

private class PdfHolder(context: android.content.Context, assetName: String) {
    private val descriptor: ParcelFileDescriptor
    private val renderer: PdfRenderer

    init {
        val file = File(context.cacheDir, assetName)
        if (!file.exists()) {
            context.assets.open(assetName).use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
        }
        descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        renderer = PdfRenderer(descriptor)
    }

    val pageCount: Int get() = renderer.pageCount

    fun render(index: Int): Bitmap? {
        if (index !in 0 until renderer.pageCount) return null
        val page = renderer.openPage(index)
        return try {
            val width = 1200
            val height = (width.toFloat() * page.height / page.width).toInt().coerceAtLeast(1)
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
                bitmap.eraseColor(android.graphics.Color.WHITE)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            }
        } finally {
            page.close()
        }
    }

    fun close() {
        renderer.close()
        descriptor.close()
    }
}
