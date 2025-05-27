package mo.voice.memos.services.audioRecorderService

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.datetime.Clock
import kotlinx.io.files.Path
import platform.AVFAudio.AVAudioQualityMax
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSTimer
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.darwin.NSObject

private const val kAudioFormatMPEG4AAC: UInt = 0x61616363u

actual class AudioRecorderService {
    private var recorder: AVAudioRecorder? = null
    private var timer: NSTimer? = null
    private var outputUrl: NSURL? = null
    private var onAmplitudeCallback: ((Int) -> Unit)? = null
    private var onFileReadyCallback: ((String) -> Unit)? = null

    @OptIn(ExperimentalForeignApi::class)
    actual fun startRecording(
        onAmplitude: (Int) -> Unit,
        onFileReady: (String) -> Unit
    ) {
        println("startRecording")
        println("recorder?.recording:${recorder?.recording}")
        if (recorder?.recording == true) return

        val filename = "recording_${Clock.System.now().toEpochMilliseconds()}.m4a"
        val dir = getDocumentsDirectory()
        // Safely assign outputUrl. If dir.URLByAppendingPathComponent returns null, outputUrl will remain null.
        outputUrl = dir.URLByAppendingPathComponent(filename)
        println("outputUrl:${outputUrl}")

        // --- THE FIX IS HERE ---
        val currentOutputUrl = outputUrl
        if (currentOutputUrl == null) {
            println("Failed to get output URL for recording. Cannot start recording.")
            // You might want to notify the UI or log an error here.
            return
        }
        // --- END OF FIX ---

        val settings: Map<Any?, Any?> = mapOf(
            AVFormatIDKey to kAudioFormatMPEG4AAC,
            AVSampleRateKey to 44100,
            AVNumberOfChannelsKey to 1,
            AVEncoderAudioQualityKey to AVAudioQualityMax
        )
        println("settings:${settings}")

        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayAndRecord, null)
        session.setActive(true, null)
        println("session:${session}")

        val newRecorder = AVAudioRecorder(
            currentOutputUrl, // Use the safely checked non-null currentOutputUrl
            settings,
            null
        )
        println("recorder:${newRecorder}")

        newRecorder.meteringEnabled = true
        if (newRecorder.prepareToRecord() && newRecorder.record()) {
            this.recorder = newRecorder
            this.onAmplitudeCallback = onAmplitude
            this.onFileReadyCallback = onFileReady

            // Timer to update amplitude every 0.2s
            timer = NSTimer.scheduledTimerWithTimeInterval(
                0.2, repeats = true
            ) {
                newRecorder.updateMeters()
                val peak = newRecorder.peakPowerForChannel(0u)
                val normalized = ((peak + 160) / 160 * 100).toInt()
                onAmplitude(normalized)
            }
        } else {
            println("Failed to prepare or record audio.")
            // Clean up if recording failed to start after preparation
            newRecorder.stop()
            newRecorder.deleteRecording()
            // You might want to notify the UI about the failure
        }
    }

    actual fun stopRecording() {
        println("stopRecording")
        timer?.invalidate()
        timer = null

        recorder?.stop()
        // Use a safe call here as outputUrl could theoretically be null if startRecording failed early
        val path = outputUrl?.path
        if (path != null) {
            onFileReadyCallback?.invoke(path)
        } else {
            println("No output URL found when stopping recording. File might not have been saved.")
        }


        recorder = null
        outputUrl = null
        onAmplitudeCallback = null
        onFileReadyCallback = null
    }

    actual fun getPlatformRecordsDirPath(): Path? {
        // Ensure getDocumentsDirectory().path is not null before converting to Path
        return getDocumentsDirectory().path?.let { Path(it) }
    }

    private fun getDocumentsDirectory(): NSURL {
        // It's good practice to ensure first() doesn't fail on an empty list, though rare.
        // For production code, you might add a check here or a default fallback.
        return NSFileManager.defaultManager.URLsForDirectory(
            directory = NSDocumentDirectory,
            inDomains = NSUserDomainMask
        ).first() as NSURL
    }
}