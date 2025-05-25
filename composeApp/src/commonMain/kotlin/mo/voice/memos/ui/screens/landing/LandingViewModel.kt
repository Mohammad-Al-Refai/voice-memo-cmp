package mo.voice.memos.ui.screens.landing

import androidx.lifecycle.ViewModel
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import mo.voice.memos.data.RecordFile
import mo.voice.memos.services.audioRecorderService.AudioRecorderService
import mo.voice.memos.services.permissionsManager.PermissionStatus
import mo.voice.memos.services.permissionsManager.PermissionType
import mo.voice.memos.services.permissionsManager.PermissionsManager
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class LandingScreenState(
    val isRecordDialogOpen: Boolean,
    val isRecordPermissionGranted: Boolean,
    val shouldRequestRecordPermission: Boolean,
    val audioAmplitude: Int,
    val records: List<RecordFile> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean
)

sealed class LandingVMSideEffect {

}

class LandingViewModel(
    val permissionsManager: PermissionsManager,
    private val audioRecorderService: AudioRecorderService
) : ViewModel(),
    ContainerHost<LandingScreenState, LandingVMSideEffect> {
    override val container =
        container<LandingScreenState, LandingVMSideEffect>(
            LandingScreenState(
                isRecordDialogOpen = false,
                shouldRequestRecordPermission = false,
                isRecordPermissionGranted = false,
                audioAmplitude = 0,
                isError = false,
                isLoading = false
            )
        )

    init {
        prepareAllRecords()
    }

    fun onRecordPermissionResult(status: PermissionStatus) {
        intent {
            when (status) {
                PermissionStatus.GRANTED -> {
                    intent {
                        reduce {
                            state.copy(isRecordDialogOpen = true)
                        }
                    }
                    startRecording()
                }

                PermissionStatus.DENIED -> {

                }

                PermissionStatus.NOT_DETERMINED -> {}
            }
        }
    }


    fun onDismissRecordDialog() {
        intent {
            reduce {
                state.copy(isRecordDialogOpen = false)
            }
        }
    }

    fun onRecordButtonClick() {
        permissionsManager.getPermissionStatus(PermissionType.AUDIO_RECORD) {
            when (it) {
                PermissionStatus.GRANTED -> {
                    intent {
                        reduce {
                            state.copy(isRecordDialogOpen = true)
                        }
                    }
                    startRecording()
                }

                PermissionStatus.DENIED -> intent {
                    reduce {
                        state.copy(shouldRequestRecordPermission = true)
                    }
                }

                PermissionStatus.NOT_DETERMINED -> {

                }
            }
        }
    }

    fun onStopRecording() {
        intent {
            reduce {
                state.copy(isRecordDialogOpen = false)
            }
        }
        audioRecorderService.stopRecording()
    }

    private fun startRecording() {
        audioRecorderService.startRecording(
            onAmplitude = {
                intent {
                    reduce {
                        state.copy(audioAmplitude = it)
                    }
                }
            },
            onFileReady = {
                intent {
                    reduce {
                        state.copy(isRecordDialogOpen = false)
                    }
                }
                prepareAllRecords()
            }
        )
    }

    private fun prepareAllRecords() {
        intent {
            reduce {
                state.copy(isLoading = true)
            }
        }
        val records = mutableListOf<RecordFile>()
        val dir = audioRecorderService.getPlatformRecordsDirPath() ?: return
        try {
            SystemFileSystem.list(dir).forEach {
                records.add(
                    RecordFile(
                        path = Path(dir, it.name),
                        name = it.name,
                    )
                )
            }
            intent {
                reduce {
                    state.copy(records = records, isLoading = false)
                }
            }
        } catch (e: Exception) {
            intent {
                reduce {
                    state.copy(isError = true, isLoading = false)
                }
            }
        }

    }
}