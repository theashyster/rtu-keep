package lv.theashyster.rtu_keep.repositories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [NoteItem::class])
abstract class KeepDatabase : RoomDatabase() {

    abstract fun noteItemDao(): NoteItemDao
}

object Database {

    private var instance: KeepDatabase? = null

    fun getInstance(context: Context) = instance ?: Room.databaseBuilder(
        context.applicationContext, KeepDatabase::class.java, "keep-db"
    )
        .allowMainThreadQueries()
        .build()
        .also { instance = it }
}
