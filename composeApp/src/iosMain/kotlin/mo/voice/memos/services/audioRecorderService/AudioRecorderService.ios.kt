package mo.voice.memos.services.audioRecorderService

import kotlinx.io.files.Path

actual class AudioRecorderService {
    actual fun startRecording(
        onAmplitude: (Int) -> Unit,
        onFileReady: (String) -> Unit
    ) {
    }

    actual fun stopRecording() {
    }

    actual fun getPlatformRecordsDirPath(): Path? {
        return null
    }

}