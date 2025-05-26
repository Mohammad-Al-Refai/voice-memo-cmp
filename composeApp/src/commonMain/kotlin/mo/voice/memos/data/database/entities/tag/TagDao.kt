package mo.voice.memos.data.database.entities.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Upsert
    suspend fun upsert(tag: Tag)

    @Delete
    suspend fun delete(tag: Tag)

    @Query("SELECT * FROM Tag")
    fun getAll(): Flow<List<Tag>>

    @Query("SELECT * FROM Tag WHERE id = :id")
    fun getById(id: Int): Flow<Tag?>

    @Query("SELECT * FROM Tag WHERE name = :name")
    fun getByName(name: String): Flow<Tag?>

    @Query("SELECT * FROM Tag WHERE name LIKE :name")
    fun getByNameLike(name: String): Flow<List<Tag>>
}