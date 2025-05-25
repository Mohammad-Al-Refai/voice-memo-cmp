package mo.voice.memos.services.audioPlayer

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

actual class AudioPlayer actual constructor(
    private val onProgressCallback: (PlayerState) -> Unit,
    private val onReadyCallback: () -> Unit,
    private val onErrorCallback: (Exception) -> Unit,
    context: Any?
) {

    // Ensure 'context' is actually an Android Context
    private val androidContext: Context = when (context) {
        is Context -> context
        else -> throw IllegalArgumentException("Expected a valid Android Context for 'context' parameter.")
    }

    // ExoPlayer instance for audio playback
    private var mediaPlayer: ExoPlayer = ExoPlayer.Builder(androidContext).build()

    // Holds media items that can be played (useful if you plan to expand to playlists).
    private val mediaItems = mutableListOf<MediaItem>()

    // Tracks the current item being played within 'mediaItems'. If single-track, can remain -1 or 0.
    private var currentItemIndex = -1

    // Internal MutableStateFlow to manage player state updates.
    private val _playerState = MutableStateFlow(PlayerState())

    // Expose the player state flow as a read-only state flow for external subscribers.
    private val playerState = _playerState.asStateFlow()

    // A reference to the file or URL currently being played.
    private var currentPlayingResource: String? = null

    // A Job that handles continuous progress updates while audio is playing.
    private var progressJob: Job? = null

    // A scope for launching coroutines related to the player.
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // ExoPlayer event listener to handle state changes and errors.
    private val listener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_IDLE -> {
                    // Player is idle (before prepare or after release).
                }

                Player.STATE_BUFFERING -> {
                    // Update our state to reflect buffering.
                    _playerState.update { it.copy(isBuffering = true) }
                    updateMediaStatus()
                }

                Player.STATE_READY -> {
                    // Player is ready to play. Notify that we're set.
                    onReadyCallback()

                    // Only set duration if the player actually knows it.
                    val durationMs = mediaPlayer.duration
                    val durationSec = if (durationMs != C.TIME_UNSET) durationMs / 1000f else 0f

                    _playerState.update {
                        it.copy(
                            isBuffering = false,
                            duration = durationSec
                        )
                    }
                    updateMediaStatus()
                }

                Player.STATE_ENDED -> {
                    seek(0f)
                    pause()
                    // Playback ended. You could trigger a callback here if needed.
                    stopProgressUpdates()
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _playerState.update { it.copy(isPlaying = isPlaying) }
            updateMediaStatus()

            // Manage the job that updates progress in real time.
            if (isPlaying) {
                startProgressUpdates()
            } else {
                stopProgressUpdates()
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            // Reset buffering flag on error, then notify via callback.
            _playerState.update { it.copy(isBuffering = false) }
            onErrorCallback(error)
        }
    }

    init {
        // Attach the listener to the ExoPlayer instance.
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        mediaPlayer.setAudioAttributes(audioAttributes, true)
        mediaPlayer.addListener(listener)
    }


    /** Pauses playback if something is currently playing. */
    actual fun pause() {
        mediaPlayer.pause()
        _playerState.update { it.copy(isPlaying = false) }
        updateMediaStatus()
    }

    /** Cleans up resources by stopping and releasing the ExoPlayer instance. */
    actual fun cleanUp() {
        mediaPlayer.stop()
        mediaPlayer.release()
        mediaPlayer.removeListener(listener)
        currentPlayingResource = null
        stopProgressUpdates()
    }

    /**
     * Begins continuous progress updates on a fixed interval while audio is playing.
     * Updates the [PlayerState] and triggers [onProgressCallback].
     */
    private fun startProgressUpdates() {
        stopProgressUpdates() // Ensure we don't create multiple jobs.
        progressJob = coroutineScope.launch {
            while (_playerState.value.isPlaying) {
                val currentPos = mediaPlayer.currentPosition.toFloat()
                val totalDuration = mediaPlayer.duration.takeIf { it != C.TIME_UNSET } ?: 1L
                val progressPercentage = currentPos / totalDuration

                _playerState.update {
                    it.copy(currentTime = currentPos / 1000f)
                }

                onProgressCallback(_playerState.value)
                delay(100) // ~10 updates per second
            }
        }
    }

    /** Stops the continuous progress updates job, if it exists. */
    private fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    /** Checks if the provided path corresponds to an existing local file. */
    private fun isLocalFile(path: String): Boolean {
        val file = File(path)
        return file.exists() && file.isFile
    }

    /**
     * Updates the current playback time and triggers [onProgressCallback].
     */
    private fun updateMediaStatus() {
        val currentPosSec = mediaPlayer.currentPosition / 1000f
        _playerState.update { it.copy(currentTime = currentPosSec) }
        onProgressCallback(_playerState.value)
    }

    /**
     * Seeks to a specific position (in seconds) within the current media.
     * @param position The desired position in seconds.
     */
    actual fun seek(position: Float) {
        mediaPlayer.seekTo((position * 1000).toLong())
    }

    /**
     * Returns the current [PlayerState]. In many setups, you'll want to expose a Flow
     * or LiveData instead of returning a snapshot.
     */
    actual fun playerState(): PlayerState {
        return playerState.value
    }

    actual fun play() {
        mediaPlayer.play()
    }

    actual fun prepare(url: String) {
        currentPlayingResource = url

        // Use Uri.fromFile(...) if it's truly a local file, otherwise parse as a normal URI.
        val mediaItem = if (isLocalFile(url)) {
            MediaItem.fromUri(Uri.fromFile(File(url)))
        } else {
            MediaItem.fromUri(url.toUri())
        }
        mediaPlayer.setMediaItem(mediaItem)
        mediaPlayer.prepare()
        _playerState.update {
            it.copy(
                currentPlayingResource = url
            )
        }
        updateMediaStatus()
    }
}