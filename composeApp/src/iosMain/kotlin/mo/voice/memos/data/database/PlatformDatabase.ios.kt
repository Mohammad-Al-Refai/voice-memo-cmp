package mo.voice.memos.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


actual class PlatformDatabase {
    actual fun getDatabase(): VoiceNoteDatabase {
        val dbFile = documentDirectory() + "/${DATABASE_NAME}"
        return Room.databaseBuilder<VoiceNoteDatabase>(
            name = dbFile
        ).setDriver(BundledSQLiteDriver())
            .build()
    }
}


@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}