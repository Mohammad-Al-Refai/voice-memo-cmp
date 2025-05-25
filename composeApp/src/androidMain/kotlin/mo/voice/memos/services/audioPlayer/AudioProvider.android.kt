package mo.voice.memos.services.audioPlayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun AudioProvider(
    audioUpdates: AudioUpdates,
    composable: @Composable (AudioPlayer) -> Unit
) {
    val context = LocalContext.current
    val audioPlayer = remember {
        AudioPlayer(
            onProgressCallback = {
                audioUpdates.onProgressUpdate(it)
            },
            onReadyCallback = {
                audioUpdates.onReady()
            },
            onErrorCallback = {
                audioUpdates.onError(it)
            },
            context = context
        )
    }
    composable(audioPlayer)
}