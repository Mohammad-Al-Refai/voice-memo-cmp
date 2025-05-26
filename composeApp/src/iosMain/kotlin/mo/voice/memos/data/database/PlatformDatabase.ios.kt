package mo.voice.memos.data.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import platform.Foundation.NSHomeDirectory

actual class PlatformDatabase {
    actual fun getDatabase(): VoiceNoteDatabase {
        val dbFile = NSHomeDirectory() + "/${DATABASE_NAME}"
        return Room.databaseBuilder<VoiceNoteDatabase>(
            name = dbFile,
            factory = {
                VoiceNoteDatabase::class.instantiateImpl()
            }

        ).setDriver(BundledSQLiteDriver()).build()
    }
}