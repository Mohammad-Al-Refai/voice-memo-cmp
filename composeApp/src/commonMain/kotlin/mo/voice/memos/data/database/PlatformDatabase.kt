package mo.voice.memos.data.database


const val DATABASE_NAME = "voiceNoteDatabase.db"

expect class PlatformDatabase {
    fun getDatabase(): VoiceNoteDatabase
}