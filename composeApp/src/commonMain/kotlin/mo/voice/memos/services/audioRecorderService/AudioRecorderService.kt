package mo.voice.memos.services.audioRecorderService

import kotlinx.io.files.Path

expect class AudioRecorderService {
    fun startRecording(
        onAmplitude: (Int) -> Unit,
        onFileReady: (String) -> Unit
    )

    fun stopRecording()
    fun getPlatformRecordsDirPath(): Path?
}