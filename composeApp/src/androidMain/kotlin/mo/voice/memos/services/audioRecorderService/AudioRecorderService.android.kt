package mo.voice.memos.services.audioRecorderService

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class AudioRecorderService(private val context: Context) {
    private val sampleRate = 44100
    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var pcmFile: File? = null
    private var wavFile: File? = null
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    actual fun startRecording(
        onAmplitude: (Int) -> Unit,
        onFileReady: (String) -> Unit
    ) {
        if (recordingJob != null) return

        val timestamp = System.currentTimeMillis()
        pcmFile = File(context.cacheDir, "recording_$timestamp.pcm")
        val externalDir = context.getExternalFilesDir("recordings")
        externalDir?.mkdirs()

        wavFile = File(externalDir, "recording_$timestamp.wav")
        val fos = FileOutputStream(pcmFile)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        ).apply {
            startRecording()
        }

        recordingJob = scope.launch {
            val buffer = ShortArray(bufferSize)
            while (isActive) {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (read > 0) {
                    val byteBuffer = ByteArray(read * 2)
                    var idx = 0
                    for (i in 0 until read) {
                        byteBuffer[idx++] = (buffer[i].toInt() and 0x00FF).toByte()
                        byteBuffer[idx++] = ((buffer[i].toInt() shr 8) and 0xFF).toByte()
                    }
                    fos.write(byteBuffer)

                    val amplitude = buffer.maxOrNull()?.toInt() ?: 0
                    onAmplitude(amplitude)
                }
            }
            fos.close()

            val pcm = pcmFile ?: return@launch
            val wav = wavFile ?: return@launch

            writeWavHeader(pcm, wav, sampleRate)
            pcm.delete() // delete raw PCM
            onFileReady(wav.absolutePath)
        }
    }

    actual fun stopRecording() {
        recordingJob?.cancel()
        recordingJob = null
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

    }

    actual fun getPlatformRecordsDirPath(): Path? {
        val path = context.getExternalFilesDir("recordings")?.absolutePath ?: return null
        return Path(path)
    }

    private fun writeWavHeader(pcmFile: File, wavFile: File, sampleRate: Int) {
        val pcmData = pcmFile.readBytes()
        val byteRate = 2 * sampleRate // 16-bit mono = 2 bytes/sample
        val totalDataLen = pcmData.size + 36

        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray(Charsets.US_ASCII))
        header.putInt(totalDataLen)
        header.put("WAVE".toByteArray(Charsets.US_ASCII))
        header.put("fmt ".toByteArray(Charsets.US_ASCII))
        header.putInt(16) // Subchunk1Size for PCM
        header.putShort(1) // AudioFormat PCM = 1
        header.putShort(1) // NumChannels
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort(2) // BlockAlign = NumChannels * BitsPerSample/8
        header.putShort(16) // BitsPerSample
        header.put("data".toByteArray(Charsets.US_ASCII))
        header.putInt(pcmData.size)

        FileOutputStream(wavFile).use {
            it.write(header.array())
            it.write(pcmData)
        }
    }
}
