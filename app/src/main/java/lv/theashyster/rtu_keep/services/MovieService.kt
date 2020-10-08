package lv.theashyster.rtu_keep.services

import retrofit2.http.GET

interface MovieService {

    @GET("netflix")
    suspend fun getMovie(): MovieEntry
}
