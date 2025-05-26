package mo.voice.memos.data.database.entities.voiceNote

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VoiceNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val audioFilePath: String,
    val tagId: Int? = null,
)
