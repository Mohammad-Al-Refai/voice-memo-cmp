package mo.voice.memos.services.audioPlayer

expect class AudioPlayer(
    onProgressCallback: (PlayerState) -> Unit,
    onReadyCallback: () -> Unit,
    onErrorCallback: (Exception) -> Unit,
    context: Any?,
) {
    fun pause()
    fun play()
    fun cleanUp()
    fun prepare(url: String)
    fun seek(position: Float)
    fun playerState(): PlayerState
}