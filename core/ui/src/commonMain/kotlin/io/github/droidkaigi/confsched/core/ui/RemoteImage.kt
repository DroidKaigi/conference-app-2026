package io.github.droidkaigi.confsched.core.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import io.github.droidkaigi.confsched.core.common.httpClientEngineFactory
import io.github.droidkaigi.confsched.core.preview.LocalPreviewImageResolver
import io.ktor.client.HttpClient
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource

@Composable
fun RemoteImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    // Both branches take the same value: a preview that scales its local resource differently from
    // the way production scales the network image makes a screenshot test pass on a layout that never ships.
    contentScale: ContentScale = ContentScale.Crop,
) {
    val resource = LocalPreviewImageResolver.current?.resolve(imageUrl)
    if (resource != null) {
        Image(
            painter = painterResource(resource),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
    } else {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}

/**
 * An image already held in memory. It is decoded on the spot rather than through a loader: the
 * bytes need no fetching, and a screenshot test would otherwise capture the frame before an
 * asynchronous decode lands.
 */
@Composable
fun ByteArrayImage(
    bytes: ByteArray,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val bitmap = remember(bytes, bytes::decodeToImageBitmap)
    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}

@Composable
fun RemoteImageLoaderEffect() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory(HttpClient(httpClientEngineFactory()))) }
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(context, percent = 0.25)
                    .build()
            }
            .diskCache { imageDiskCache(context) }
            .build()
    }
}
