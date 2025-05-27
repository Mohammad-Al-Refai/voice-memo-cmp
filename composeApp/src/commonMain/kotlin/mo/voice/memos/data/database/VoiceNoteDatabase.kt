package mo.voice.memos.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import mo.voice.memos.data.database.entities.tag.Tag
import mo.voice.memos.data.database.entities.tag.TagDao
import mo.voice.memos.data.database.entities.voiceNote.VoiceNote
import mo.voice.memos.data.database.entities.voiceNote.VoiceNoteDao


@Database(
    entities = [Tag::class, VoiceNote::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class VoiceNoteDatabase : RoomDatabase() {
    abstract fun tagDao(): TagDao
    abstract fun voiceNoteDao(): VoiceNoteDao
}


// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<VoiceNoteDatabase> {
    override fun initialize(): VoiceNoteDatabase
}
