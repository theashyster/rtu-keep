package lv.theashyster.rtu_keep.screens.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import lv.theashyster.rtu_keep.repositories.KeepRepository
import lv.theashyster.rtu_keep.repositories.NoteItem

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val keepRepository: KeepRepository = KeepRepository(application)

    val items: LiveData<List<NoteItem>>
        get() = keepRepository.fetchAll()

    fun deleteItem(item: NoteItem) = keepRepository.deleteItem(item)
}
