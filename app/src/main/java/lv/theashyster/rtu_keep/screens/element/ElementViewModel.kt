package lv.theashyster.rtu_keep.screens.element

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import lv.theashyster.rtu_keep.repositories.KeepRepository
import lv.theashyster.rtu_keep.repositories.MovieRepository
import lv.theashyster.rtu_keep.repositories.NoteItem

class ElementViewModel(application: Application) : AndroidViewModel(application) {

    private val movieRepository: MovieRepository = MovieRepository

    private val keepRepository: KeepRepository = KeepRepository(application)

    fun getMovie() = MovieRepository.getMovie()

    fun getItemById(itemId: Long): NoteItem? = keepRepository.getItemById(itemId)

    fun insertItem(item: NoteItem): Long = keepRepository.insertItem(item)

    fun updateItem(item: NoteItem) = keepRepository.updateItem(item)

    fun deleteItem(item: NoteItem) = keepRepository.deleteItem(item)
}
