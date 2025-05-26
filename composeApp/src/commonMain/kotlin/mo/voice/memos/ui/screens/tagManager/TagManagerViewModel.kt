package mo.voice.memos.ui.screens.tagManager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.io.files.Path
import mo.voice.memos.data.RecordFile
import mo.voice.memos.data.database.VoiceNoteDatabase
import mo.voice.memos.data.database.entities.voiceNote.VoiceNote
import mo.voice.memos.services.audioRecorderService.AudioRecorderService
import mo.voice.memos.services.permissionsManager.PermissionStatus
import mo.voice.memos.services.permissionsManager.PermissionType
import mo.voice.memos.services.permissionsManager.PermissionsManager
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import kotlin.random.Random

data class TagManagerScreenState(
    val isRecordDialogOpen: Boolean,
    val isRecordPermissionGranted: Boolean,
    val shouldRequestRecordPermission: Boolean,
    val isLoading: Boolean,
    val isError: Boolean,
    val audioAmplitude: Int,
    val records: List<RecordFile>,
    val tagName: String
)

sealed class TagManagerVMSideEffect {
}

class TagManagerViewModel(
    val permissionsManager: PermissionsManager,
    private val audioRecorderService: AudioRecorderService,
    private val voiceNoteDatabase: VoiceNoteDatabase,
    private val tagId: Int
) : ViewModel(),
    ContainerHost<TagManagerScreenState, TagManagerVMSideEffect> {
    override val container =
        container<TagManagerScreenState, TagManagerVMSideEffect>(
            TagManagerScreenState(
                isRecordDialogOpen = false,
                shouldRequestRecordPermission = false,
                isRecordPermissionGranted = false,
                audioAmplitude = 0,
                isError = false,
                isLoading = false,
                records = emptyList(),
                tagName = ""
            )
        )

    init {
        viewModelScope.launch {
            prepareAllRecords(tagId)
            updateTagName()
        }
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
                viewModelScope.launch {
                    voiceNoteDatabase.voiceNoteDao().upsert(
                        VoiceNote(
                            title = "Untitled#${Random.nextInt(from = 0, until = 1000)}",
                            audioFilePath = it,
                            tagId = tagId
                        )
                    )
                }
                intent {
                    reduce {
                        state.copy(isRecordDialogOpen = false)
                    }
                }
                viewModelScope.launch {
                    prepareAllRecords(tagId)
                }
            }
        )
    }

    private suspend fun updateTagName() {
        val tag = voiceNoteDatabase.tagDao().getById(tagId).first()
        intent {
            reduce {
                state.copy(tagName = tag?.name ?: "No title")
            }
        }
    }

    private suspend fun prepareAllRecords(tagId: Int) {
        intent {
            reduce {
                state.copy(isLoading = true)
            }
        }
        val records = mutableListOf<RecordFile>()
        val all = voiceNoteDatabase.voiceNoteDao().getAllByTagId(tagId).first()
        println(all)
        all.forEach { voiceNote ->
            records.add(
                RecordFile(
                    path = Path(voiceNote.audioFilePath),
                    name = voiceNote.title
                )
            )
        }
        intent {
            reduce {
                state.copy(isLoading = false, records = records)
            }
        }
    }
}