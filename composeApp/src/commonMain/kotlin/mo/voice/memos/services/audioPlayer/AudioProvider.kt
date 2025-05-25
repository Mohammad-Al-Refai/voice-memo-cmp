package mo.voice.memos.services.audioPlayer

import androidx.compose.runtime.Composable

@Composable
expect fun AudioProvider( audioUpdates: AudioUpdates,composable: @Composable (AudioPlayer) -> Unit)

interface AudioUpdates {
    fun onProgressUpdate(playerState: PlayerState)
    fun onReady()
    fun onError(exception: Exception)
}