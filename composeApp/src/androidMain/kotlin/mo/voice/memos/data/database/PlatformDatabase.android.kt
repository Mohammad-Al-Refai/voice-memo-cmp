package mo.voice.memos.data.database

import android.content.Context
import androidx.room.Room

actual class PlatformDatabase(private val context: Context) {
    actual fun getDatabase(): VoiceNoteDatabase {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<VoiceNoteDatabase>(
            context = context,
            name = dbFile.absolutePath

        ).build()
    }
}