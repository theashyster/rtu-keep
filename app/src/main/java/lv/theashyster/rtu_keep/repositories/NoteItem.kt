package lv.theashyster.rtu_keep.repositories

import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(tableName = "note_item")
data class NoteItem(
    var type: Int,
    var color: Int,
    var title: String? = null,
    var note: String? = null,
    var rating: Float? = null,
    var uri: String? = null,
    @PrimaryKey(autoGenerate = true) var id: Long? = null
)

@Dao
interface NoteItemDao {

    @Query("SELECT * FROM note_item")
    fun fetchAll(): LiveData<List<NoteItem>>

    @Query("SELECT * FROM note_item WHERE id = :itemId")
    fun getItemById(itemId: Long): NoteItem?

    @Insert
    fun insertItem(item: NoteItem): Long

    @Update
    fun updateItem(item: NoteItem)

    @Delete
    fun deleteItem(item: NoteItem)
}
