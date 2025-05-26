package mo.voice.memos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mo.voice.memos.data.database.entities.tag.Tag
import mo.voice.memos.data.database.entities.tag.TagDao
import mo.voice.memos.data.database.entities.voiceNote.VoiceNote
import mo.voice.memos.data.database.entities.voiceNote.VoiceNoteDao


@Database(
    entities = [Tag::class, VoiceNote::class],
    version = 1
)
abstract class VoiceNoteDatabase : RoomDatabase() {
    abstract fun tagDao(): TagDao
    abstract fun voiceNoteDao(): VoiceNoteDao
}


