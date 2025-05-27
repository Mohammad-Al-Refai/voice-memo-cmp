package mo.voice.memos.services.permissionsManager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionDenied
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted
import platform.AVFAudio.AVAudioSessionRecordPermissionUndetermined

actual class PermissionsManager {
    @Composable
    actual fun RequestPermission(
        permission: PermissionType,
        onResult: (PermissionStatus) -> Unit
    ) {
        LaunchedEffect(permission) {
            when (permission) {
                PermissionType.AUDIO_RECORD -> {
                    getPermissionStatus(permission) { result ->
                        println("result:${result}")
                        when (result) {
                            PermissionStatus.GRANTED -> onResult(PermissionStatus.GRANTED)
                            PermissionStatus.DENIED -> onResult(PermissionStatus.DENIED)
                            PermissionStatus.NOT_DETERMINED -> {
                                val session = AVAudioSession.sharedInstance()
                                println("Requesting mic permission now...")
                                session.requestRecordPermission { granted ->
                                    onResult(if (granted) PermissionStatus.GRANTED else PermissionStatus.DENIED)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    actual fun getPermissionStatus(
        permission: PermissionType,
        onResult: (PermissionStatus) -> Unit
    ) {
        when (permission) {
            PermissionType.AUDIO_RECORD -> {
                val recordPermission = AVAudioSession.sharedInstance().recordPermission()

                val status = when (recordPermission) {
                    AVAudioSessionRecordPermissionGranted -> PermissionStatus.GRANTED
                    AVAudioSessionRecordPermissionDenied -> PermissionStatus.DENIED
                    AVAudioSessionRecordPermissionUndetermined -> PermissionStatus.NOT_DETERMINED
                    else -> PermissionStatus.NOT_DETERMINED
                }

                onResult(status)
            }

            else -> {
                // Handle other permission types if needed
                onResult(PermissionStatus.NOT_DETERMINED)
            }
        }
    }
}