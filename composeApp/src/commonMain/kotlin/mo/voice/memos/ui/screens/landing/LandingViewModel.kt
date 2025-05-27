package mo.voice.memos.ui.screens.landing

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mo.voice.memos.data.database.VoiceNoteDatabase
import mo.voice.memos.data.database.entities.tag.Tag
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

data class TagItem(
    val name: String,
    val voiceNotesCount: Int,
    val id: Int,
    val color: Color
)

data class LandingScreenState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isAddTagDialogVisible: Boolean = false,
    val tags: List<TagItem> = emptyList()
)

sealed class LandingScreenSideEffect {

}

class LandingViewModel(private val voiceNoteDatabase: VoiceNoteDatabase) : ViewModel(),
    ContainerHost<LandingScreenState, LandingScreenSideEffect> {
    override val container =
        container<LandingScreenState, LandingScreenSideEffect>(LandingScreenState())

    init {
        viewModelScope.launch {
            getAllTags()
        }
    }

    fun onAddTagClick() {
        intent {
            reduce { state.copy(isAddTagDialogVisible = true) }
        }
    }

    fun onSaveTag(tag: Tag) {
        intent {
            reduce { state.copy(isAddTagDialogVisible = false) }
        }

        viewModelScope.launch {
            voiceNoteDatabase.tagDao().upsert(tag)
            getAllTags()
        }
    }

    fun onDismissAddTagDialog() {
        intent {
            reduce { state.copy(isAddTagDialogVisible = false) }
        }
    }

    private suspend fun getAllTags() {
        intent {
            reduce { state.copy(isLoading = true) }
        }
        val allTags = withContext(Dispatchers.IO) {
            val tagDao = voiceNoteDatabase.tagDao()
            val noteDao = voiceNoteDatabase.voiceNoteDao()

            tagDao.getAll().first().map { tag ->
                val voiceNotesCount = noteDao.getAllByTagId(tag.id).first().size
                TagItem(
                    name = tag.name,
                    voiceNotesCount = voiceNotesCount,
                    id = tag.id,
                    color = tag.color.toColor()
                )
            }
        }

        intent {
            reduce { state.copy(tags = allTags, isLoading = false) }
        }
    }

}

fun String.toColor(): Color {
    val hex = this.removePrefix("#")
    val r: Int
    val g: Int
    val b: Int
    val a: Int

    when (hex.length) {
        6 -> { // RRGGBB
            a = 255
            r = hex.substring(0, 2).toInt(16)
            g = hex.substring(2, 4).toInt(16)
            b = hex.substring(4, 6).toInt(16)
        }

        8 -> { // AARRGGBB
            a = hex.substring(0, 2).toInt(16)
            r = hex.substring(2, 4).toInt(16)
            g = hex.substring(4, 6).toInt(16)
            b = hex.substring(6, 8).toInt(16)
        }

        else -> throw IllegalArgumentException("Invalid color format: $this")
    }

    return Color(
        red = r / 255f,
        green = g / 255f,
        blue = b / 255f,
        alpha = a / 255f
    )
}