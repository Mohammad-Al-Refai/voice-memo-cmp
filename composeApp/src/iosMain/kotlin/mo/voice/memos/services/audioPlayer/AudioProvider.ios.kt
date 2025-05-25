package mo.voice.memos.services.audioPlayer

import androidx.compose.runtime.Composable

@Composable
actual fun AudioProvider(
    audioUpdates: AudioUpdates,
    composable: @Composable (AudioPlayer) -> Unit
) {
}