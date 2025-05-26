package mo.voice.memos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.PauseCircle
import compose.icons.fontawesomeicons.regular.PlayCircle
import compose.icons.fontawesomeicons.solid.AngleDown
import compose.icons.fontawesomeicons.solid.AngleUp
import mo.voice.memos.data.RecordFile
import mo.voice.memos.services.audioPlayer.AudioPlayer
import mo.voice.memos.services.audioPlayer.AudioProvider
import mo.voice.memos.services.audioPlayer.AudioUpdates
import mo.voice.memos.services.audioPlayer.PlayerState
import mo.voice.memos.ui.theme.IconSize
import mo.voice.memos.ui.theme.Radius
import mo.voice.memos.ui.theme.Spacing

@Composable
fun AudioPlayerItem(recordFile: RecordFile) {
    val audioPlayerState: MutableState<PlayerState?> = remember { mutableStateOf(PlayerState()) }
    val isOpen = remember { mutableStateOf(false) }
    AudioProvider(audioUpdates = object : AudioUpdates {
        override fun onProgressUpdate(playerState: PlayerState) {
            audioPlayerState.value = playerState
        }

        override fun onReady() {
        }

        override fun onError(exception: Exception) {
            exception.printStackTrace()
        }

    }) { audioPlayer ->
        Column(
            Modifier.fillMaxWidth().background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(Radius.M.dp)
            ).padding(Spacing.S.value)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(recordFile.name)
                IconButton(onClick = {
                    if (audioPlayerState.value?.isPlaying == true) return@IconButton
                    isOpen.value = !isOpen.value
                }) {
                    Icon(
                        imageVector = if (isOpen.value) {
                            FontAwesomeIcons.Solid.AngleUp
                        } else {
                            FontAwesomeIcons.Solid.AngleDown
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(IconSize.M.dp)
                    )
                }
            }
            AnimatedVisibility(isOpen.value) {
                AudioControl(
                    state = audioPlayerState.value,
                    audioPlayer = audioPlayer,
                    recordFile = recordFile
                )
            }
        }
    }
}

@Composable
private fun AudioControl(state: PlayerState?, audioPlayer: AudioPlayer, recordFile: RecordFile) {
    LaunchedEffect(Unit) {
        val url = recordFile.path.parent.let {
            it.toString() + "/" + recordFile.path.name
        }
        audioPlayer.prepare(url = url)
    }
    if (state == null) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val durationTotalSeconds = state.duration.toInt()
            val dMinutes = durationTotalSeconds / 60
            val dSeconds = durationTotalSeconds % 60
            Text(text = "$dMinutes:$dSeconds", modifier = Modifier.padding(end = 4.dp))
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .padding(horizontal = 4.dp),
                drawStopIndicator = {
                    drawLine(
                        Color.Green,
                        start = Offset(0f, 0f),
                        end = Offset(10f, 0f),
                        strokeWidth = 4f
                    )
                }
            )
            val totalSeconds = state.currentTime.toInt()
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            Text(text = "$minutes:$seconds", modifier = Modifier.padding(start = 4.dp))
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    if (state.isPlaying) {
                        audioPlayer.pause()
                        return@IconButton
                    }
                    audioPlayer.play()
                }
            ) {
                Icon(
                    imageVector = if (state.isPlaying)
                        FontAwesomeIcons.Regular.PauseCircle
                    else
                        FontAwesomeIcons.Regular.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(IconSize.S.dp)
                )

            }
        }
    }
}
