package mo.voice.memos.services.audioPlayer

data class PlayerState(
    var isPlaying: Boolean = false,
    var isBuffering: Boolean = false,
    var currentTime: Float = 0f,
    var duration: Float = 0f,
    var currentPlayingResource: String? = null

) {
    val progress = if ((currentTime / duration).isNaN()) {
        0f
    } else {
        currentTime / duration
    }
}