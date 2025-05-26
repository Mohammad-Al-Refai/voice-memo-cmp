package mo.voice.memos.services.audioPlayer

actual class AudioPlayer actual constructor(
    onProgressCallback: (PlayerState) -> Unit,
    onReadyCallback: () -> Unit,
    onErrorCallback: (Exception) -> Unit,
    context: Any?
) {
    actual fun pause() {
    }

    actual fun play() {
    }

    actual fun cleanUp() {
    }

    actual fun seek(position: Float) {
    }

    actual fun prepare(url: String) {
    }

    actual fun playerState(): PlayerState {
       return PlayerState()
    }

}