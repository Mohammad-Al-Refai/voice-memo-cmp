package mo.voice.memos.data.database.entities.voiceNote

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface VoiceNoteDao {
    @Upsert
    suspend fun upsert(voiceNote: VoiceNote)

    @Delete
    suspend fun delete(voiceNote: VoiceNote)

    @Query("SELECT * FROM VoiceNote")
     fun getAll(): Flow<List<VoiceNote>>

    @Query("SELECT * FROM VoiceNote WHERE id = :id")
     fun getById(id: Int): Flow<VoiceNote?>

    @Query("SELECT * FROM VoiceNote WHERE title = :title")
     fun getByTitle(title: String): Flow<VoiceNote?>

    @Query("SELECT * FROM VoiceNote WHERE tagId = :tagId")
     fun getAllByTagId(tagId: Int): Flow<List<VoiceNote>>

    @Query("SELECT * FROM VoiceNote WHERE title LIKE :title")
     fun getByTitleLike(title: String): Flow<List<VoiceNote>>
}