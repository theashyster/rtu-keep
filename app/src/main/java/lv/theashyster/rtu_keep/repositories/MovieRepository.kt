package lv.theashyster.rtu_keep.repositories

import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers.IO
import lv.theashyster.rtu_keep.services.MovieServiceProvider

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val message: String?) : Resource<T>()
    class Loaded<T> : Resource<T>()
}

object MovieRepository {

    private val service by lazy { MovieServiceProvider.instance }

    fun getMovie() = request { service.getMovie() }

    private fun <T> request(request: suspend () -> T) = liveData<Resource<T>>(IO) {
        emit(Resource.Loading())

        try {
            emit(Resource.Success(request()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message))
        } finally {
            emit(Resource.Loaded())
        }
    }
}
