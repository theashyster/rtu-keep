package lv.theashyster.rtu_keep.repositories

import android.content.Context
import androidx.lifecycle.LiveData

class KeepRepository(private val context: Context) {

    private val db get() = Database.getInstance(context)

    fun fetchAll(): LiveData<List<NoteItem>> = db.noteItemDao().fetchAll()

    fun getItemById(itemId: Long): NoteItem? = db.noteItemDao().getItemById(itemId)

    fun insertItem(item: NoteItem): Long = db.noteItemDao().insertItem(item)

    fun updateItem(item: NoteItem) = db.noteItemDao().updateItem(item)

    fun deleteItem(item: NoteItem) = db.noteItemDao().deleteItem(item)
}
