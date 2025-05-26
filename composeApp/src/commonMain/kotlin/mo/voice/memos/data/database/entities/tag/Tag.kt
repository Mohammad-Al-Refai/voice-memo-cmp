package mo.voice.memos.data.database.entities.tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val color: String
)
